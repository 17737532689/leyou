package com.leyou.service.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.service.mapper.*;
import com.leyou.service.service.GoodsService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.beans.Transient;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BrandMapper brandMapper;
    /**
     * 分页查询
     *
     * @param page
     * @param rows
     * @param
     * @param key
     * @param saleable
     * @return
     */
    @Override
    public PageResult<Spu> querySpuByPage(Integer page, Integer rows,  String key, Boolean saleable) {
        //开启分页
        PageHelper.startPage(page, rows);

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(key)) {
            criteria.orLike("title", "%" + key + "%");
        }
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }
        //排序方式
        example.setOrderByClause("last_update_time  desc");

        //只查询未删除的商品
        criteria.andEqualTo("valid", 1);

        List<Spu> spus = spuMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(spus)) {
            throw new LyException(ExceptionEnum.SPU_NOT_FOUND);
        }
        //处理商品分类名和品牌名字
        handleCategoryAndBrand(spus);
        PageInfo<Spu> spuPageInfo = new PageInfo<>(spus);
        return new PageResult<>(spuPageInfo.getTotal(), spus);
    }

    /**
     * 商品添加
     *
     * @param spu
     */
    @Transactional
    @Override
    public void aadGoods(Spu spu) {
        //添加商品要添加四个表 spu, spuDetail, sku, stock四张表
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(new Date());
        spu.setValid(true);
        spu.setSaleable(true);
        //插入spu
        int insertSpuCount = spuMapper.insert(spu);
        if (insertSpuCount != 1) {
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        //插入详情
        int insertSpuDetailCount = spuDetailMapper.insert(spuDetail);
        if (insertSpuDetailCount != 1) {
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        //插入库存
        saveSkuAndStock(spu);
    }

    //更新商品
    @Transactional
    @Override
    public void updateGoods(Spu spu) {
        if (spu.getId() == null) {
            throw new LyException(ExceptionEnum.INVALID_PARAM);
        }
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        List<Sku> skuList = skuMapper.select(sku);
        if (!CollectionUtils.isEmpty(skuList)) {
            //删除sku
            skuMapper.delete(sku);
            //删除库存
            List<Long> longList = skuList.stream()
                    .map(Sku::getId)
                    .collect(Collectors.toList());

            stockMapper.deleteByIdList(longList);
        }
        //更新商品
        int spuCount = spuMapper.updateByPrimaryKeySelective(spu);
        if (spuCount != 1) {
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
        //更新详情
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        int detailCount = spuDetailMapper.updateByPrimaryKeySelective(spuDetail);
        if (detailCount != 1) {
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
        //更新sku stock
        saveSkuAndStock(spu);
    }

    /**
     * 手动上下架
     *
     * @param spu
     */
    @Override
    public void handleSaleable(Spu spu) {
        spu.setSaleable(!spu.getSaleable());
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count != 1) {
            throw new LyException(ExceptionEnum.UPDATE_SALEABLE_ERROR);
        }

    }

    //删除商品
    @Override
    public void deleteGoodsBySpuId(Long spuId) {
        if (spuId == null) {
            throw new LyException(ExceptionEnum.INVALID_PARAM);
        }
        Spu spu = new Spu();
        spu.setValid(false);
        spu.setId(spuId);
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count == 0) {
            throw new LyException(ExceptionEnum.DELETE_GOODS_ERROR);
        }
    }

    /**
     * 通过spuid查询sku
     *
     * @param spuId
     * @return
     */
    @Override
    public List<Sku> querySkuBySpuId(Long spuId) {
        if (spuId == null) {
            throw new LyException(ExceptionEnum.INVALID_PARAM);
        }
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skuList = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnum.SKU_NOT_FOUND);
        }
        //查询库存
        for (Sku sku1 : skuList) {
            sku1.setStock(stockMapper.selectByPrimaryKey(sku1.getId()).getStock());
        }
        return skuList;
    }

    /**
     * 通过ids查询sku
     *
     * @param ids
     * @return
     */
    @Override
    public List<Sku> querySkusByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new LyException(ExceptionEnum.INVALID_PARAM);
        }

        List<Sku> skuList = skuMapper.selectByIdList(ids);

        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //填充库存
        fillStock(ids, skuList);

        return skuList;
    }

    /**
     * 根据id查询spu
     *
     * @param spuId
     * @return
     */
    @Override
    public Spu querySpuBySpuId(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        if (spu == null) {
            throw new LyException(ExceptionEnum.SPU_NOT_FOUND);
        }

        return spu;
    }

    /**
     * 查询详情
     *
     * @param spuId
     * @return
     */
    @Override
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        if (spuId == null) {
            throw new LyException(ExceptionEnum.INVALID_PARAM);
        }
        Example example = new Example(SpuDetail.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("spuId", spuId);

        SpuDetail spuDetail = spuDetailMapper.selectOneByExample(example);

        if (spuDetail == null) {
            throw new LyException(ExceptionEnum.SPU_NOT_FOUND);
        }
        return spuDetail;
    }

    /**
     * 插入库存
     *
     * @param spu
     */
    public void saveSkuAndStock(Spu spu) {
        List<Sku> skus = spu.getSkus();
        List<Stock> stocks = new ArrayList<>();
        skus.stream().forEach(sku -> {
            sku.setSpuId(sku.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getLastUpdateTime());
            //插入sku
            int skuCount = skuMapper.insert(sku);
            if (skuCount != 1) {
                throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }
            Stock stock = new Stock();
            stock.setStock(sku.getStock());
            stock.setSkuId(sku.getId());
            stocks.add(stock);
        });
        //插入库存
        int insertList = stockMapper.insertList(stocks);
        if (insertList != 1) {
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
    }

    /**
     * 填充库存
     *
     * @param ids
     * @param skus
     */
    public void fillStock(List<Long> ids, List<Sku> skus) {
        List<Stock> stocks = stockMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(stocks)) {
            throw new LyException(ExceptionEnum.STOCK_NOT_FOUND);
        }
        //将库存转为map key为skuid
        Map<Long, Integer> map = stocks.stream()
                .collect(Collectors.toMap(s -> s.getSkuId(), s -> s.getStock()));

        for (Sku sku : skus) {
            sku.setStock(map.get(sku.getId()));
        }
    }

    /**
     *处理商品分类名和品牌名字
     * @param spuList
     */
    public void handleCategoryAndBrand(List<Spu> spuList){

        for(Spu spu:spuList){
            List<String> nameList = categoryMapper.selectByIdList(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream()
                    .map(Category::getName)
                    .collect(Collectors.toList());
            //商品分类名
            spu.setCname(StringUtils.join(nameList,"/"));
            //品牌名
            spu.setBname( brandMapper.selectByPrimaryKey(spu.getBrand_id()).getName());
        }
    }

}
