package com.leyou.search.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ElasticsearchTemplate template;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private SpecClient specClient;
    @Autowired
    private GoodsClient goodsClient;
    @Override
    public SearchResult<Goods> search(SearchRequest searchRequest) {

        String key = searchRequest.getKey();
        if(StringUtils.isBlank(key)){
            throw  new LyException(ExceptionEnum.INVALID_PARAM);
        }
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //过滤想要的字段
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subtitle","skus"},null));
        //分页排序
        searchWithPageAndSort(queryBuilder,searchRequest);
        //基本搜索条件
        QueryBuilder basicQuery = buildBasicQuery(searchRequest);
        queryBuilder.withQuery(basicQuery);
        //对分类和品牌聚合
        String categoryAggName="categoryAgg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        String brandAggName="brandAgg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //查询结果
        AggregatedPage<Goods> goodsResult = template.queryForPage(queryBuilder.build(), Goods.class);
        //解析聚合结果
        Aggregations aggs = goodsResult.getAggregations();
        //解析分类聚合
        List<Category> categories = handleCategoryAgg(aggs.get(categoryAggName));
        //解析品牌类聚合
        List<Brand> brands = handleBrandAgg(aggs.get(brandAggName));
        //对规格参数聚合
        List<Map<String,Object>> specs = null;
        if(categories!=null&&categories.size()==1){
            specs = handleSpecs(categories.get(0).getId(),basicQuery);
        }
        //解析分页结果
        long totalElements = goodsResult.getTotalElements();
        int totalPages = goodsResult.getTotalPages();
        List<Goods> item = goodsResult.getContent();
        return new SearchResult<>(totalElements,totalPages,item,categories,brands,specs);
    }

    @Override
    public Goods buildGoods(Spu spu) {
        Long spuId = spu.getId();
        //查询商品分类名
        List<String> names = categoryClient.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList());
        //查询品牌
        Brand brand = brandClient.queryById(spu.getBrandId());
        if(brand==null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //所有的搜索字段拼接到all中，all存入索引库，并进行分词处理，搜索时与all中的字段进行匹配查询
        String all = spu.getTitle()+StringUtils.join(names," ")+brand.getName();
        //查询sku
        List<Sku> skuList = goodsClient.querySkuBySpuId(spuId);
        if(CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //存储price价格
        TreeSet<Double> priceSet = new TreeSet<>();
        //设置存储skus的json结构的集合，用map结果转化sku对象，转化为json之后与对象结构相似（或者重新定义一个对象，存储前台要展示的数据，并把sku对象转化成自己定义的对象）
        List<Map<String,Object>> skus = new ArrayList<>();

        for(Sku sku:skuList){
            priceSet.add(sku.getPrice());
            HashMap<String, Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            //sku中有多个图片，只展示第一张
            map.put("image",StringUtils.substringBefore(sku.getImages(),","));
            map.put("price",sku.getPrice());
            skus.add(map);
        }
        //查询规格参数，规格参数中分为通用规格参数和特有规格参数
        List<SpecParam> specParams = specClient.querySpecParams(null, spu.getCid3(), true, null);
        if(CollectionUtils.isEmpty(specParams)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        //查询商品详情
        SpuDetail spuDetail = goodsClient.querySpuDetailById(spuId);
        //获取通用规格参数
       String string = spuDetail.getGenericSpec();
        JSONObject jsonObject = JSONObject.parseObject(string);
        

        Map<Long, String> genericSpec = JsonUtils.toMap(spuDetail.getGenericSpec(), Long.class, String.class);
        //获取特有规格参数
        Map<String, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<String>>>() {
        });

        //定义spec对应的map
        HashMap<String,Object> map = new HashMap<>();

        for(SpecParam specParam:specParams){
            //key是规定参说的名字
            String key = specParam.getName();
            Object value = "";
            if(specParam.getGeneric()){
                //参数是通用属性，通过规格参数的ID从商品详情存储的规格参数中查出值
                value = genericSpec.get(specParam.getId());
                if(specParam.getNumeric()){
                    //参数是数值类型，处理成段，方便后期对数值类型进行范围过滤
                    value =chooseSegment(value.toString(),specParam);
                }
            }else {
                value = specialSpec.get(specParam.getId());
            }
            value = (value ==null ? "其他":value);
            //存入map
            map.put(key,value);
        }
        Goods goods = new Goods();
        goods.setId(spuId);
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setAll(all);
        goods.setPrice(priceSet);
        goods.setSubtitle(spu.getSubTitle());
        goods.setSpecs(map);
        goods.setSkus(JsonUtils.toString(skus));
        return goods;
    }

    /**
     * 分页排序
     */
    private void searchWithPageAndSort(NativeSearchQueryBuilder queryBuilder,SearchRequest searchRequest){

       Integer page =  searchRequest.getPage()-1;
       Integer size = searchRequest.getSize();

        String sortBy = searchRequest.getSortBy();
        Boolean desc = searchRequest.getDescending();

        queryBuilder.withPageable(PageRequest.of(page,size));

        if(StringUtils.isNotBlank(sortBy)){
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
        }

    }

    /**
     *构建基本条件
     * @param searchRequest
     * @return
     */
    private QueryBuilder buildBasicQuery(SearchRequest searchRequest){

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //搜索条件
        boolQuery.must(QueryBuilders.matchQuery("all",searchRequest.getKey()));
        Map<String, String> filterMap = searchRequest.getFilter();
        if(!CollectionUtils.isEmpty(filterMap)){
            for(Map.Entry<String, String> entry : filterMap.entrySet()){
                String key = entry.getKey();
                //判断key是否是分类或者品牌过滤条件
                if(!"cid2".equals(key)&&!"brandID".equals(key)){
                    key ="specs."+key+".keyword";
                }
                //过滤条件
                String value = entry.getValue();
                boolQuery.filter(QueryBuilders.termQuery(key,value));
            }
        }
        return boolQuery;
    }

    /**
     *对分类聚合结果进行解析
     * @param terms
     */
    private List<Category> handleCategoryAgg(LongTerms terms){
        //获取id
        List<Long> ids = terms.getBuckets()
                .stream()
                .map(b -> b.getKeyAsNumber().longValue())
                .collect(Collectors.toList());

        List<Category> categories = categoryClient.queryByIds(ids);
        if(CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        for(Category category:categories){
            category.setIsParent(null);
            category.setParentId(null);
            category.setSort(null);
        }
        return categories;
    }

    /**
     * 解析品牌聚合结果
     * @param terms
     * @return
     */
    private  List<Brand> handleBrandAgg(LongTerms terms){
        List<Long> ids = terms.getBuckets()
                .stream()
                .map(b -> b.getKeyAsNumber().longValue())
                .collect(Collectors.toList());

        List<Brand> brands = brandClient.queryBrandsByIds(ids);
        if(CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brands;
    }

    /**
     *对规格参数进行聚合并解析结果
     * @param id
     * @param basicQuery
     * @return
     */
    private List<Map<String,Object>>handleSpecs(Long id, QueryBuilder basicQuery){
        List<Map<String,Object>> specs = new ArrayList<>();
        //查询可过滤的规格参数
        List<SpecParam> specParams = specClient.querySpecParams(null, id, true, null);
        //基本查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(basicQuery);
        queryBuilder.withPageable(PageRequest.of(0,1));
        for(SpecParam specParam:specParams){
            //聚合
            String name = specParam.getName();
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("spec."+name+".keyword"));
        }
        //查询
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        //对聚合进行解析
        Aggregations aggs = result.getAggregations();
        for(SpecParam specParam:specParams){
            String name = specParam.getName();
            Terms terms = aggs.get(name);
            //创建聚合结果
            HashMap<String,Object> map = new HashMap<>();
            map.put("k",name);
            map.put("options",terms.getBuckets()
            .stream()
            .map(b->((Terms.Bucket) b).getKey())
            .collect(Collectors.toList())
            );
            specs.add(map);
        }
        return specs;
    }

    /**
     * 将规格参数为数值型的参数划分为段
     *
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }
}
