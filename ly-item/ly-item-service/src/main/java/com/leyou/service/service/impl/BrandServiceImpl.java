package com.leyou.service.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.vo.BrandVo;
import com.leyou.service.mapper.BrandMapper;
import com.leyou.service.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;


import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 分页查询
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @param key
     * @return
     */
    @Override
    public PageResult<Brand> queryBrandAndSort(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //开启分页
        PageHelper.startPage(page, rows);

        Example example = new Example(Brand.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.orLike("name", "%" + key + "%").orLike("letter", key);

        if (StringUtils.isNotEmpty(sortBy)) {
            //String sortByClause = sortBy + (desc ? "DESC" : "ASC");
            String sortByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(sortByClause);
        }
        List<Brand> brands = brandMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(brands)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        PageInfo<Brand> brandPageInfo = new PageInfo<>(brands);

        return new PageResult<>(brandPageInfo.getTotal(), brands);
    }

    /**
     * 添加
     * @param brand
     * @param cids
     */
    @Transactional
    @Override
    public void saveBrand(Brand brand, List<Long> cids) {
        brand.setId(null);
        if(brand==null){
            throw new LyException(ExceptionEnum.BRAND_CREATE_FAILED);
        }
        int count = brandMapper.insertSelective(brand);
        if(count<0){
            throw new LyException(ExceptionEnum.BRAND_CREATE_FAILED);
        }
        //更新中间表
        for(Long cid:cids){
            int resultCount = brandMapper.saveCategoryBrand(cid, brand.getId());
            if(resultCount==0){
                throw new LyException(ExceptionEnum.BRAND_CREATE_FAILED);
            }
        }
    }

    /**
     * 删除品牌
     * @param bid
     */
    @Transactional
    @Override
    public void deleteBrand(Long bid) {
        if(bid==null){
            throw new LyException(ExceptionEnum.DELETE_BRAND_EXCEPTION);
        }
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",bid);
        int count = brandMapper.deleteByExample(example);
        if(count<=0){
            throw new LyException(ExceptionEnum.DELETE_BRAND_EXCEPTION);
        }
        int resultCount = brandMapper.deleteCategoryBrandByBid(bid);
        if(resultCount<=0){
            throw new LyException(ExceptionEnum.DELETE_BRAND_EXCEPTION);
        }
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    public Brand queryByBid(Long id) {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",id);
        Brand brand = brandMapper.selectOneByExample(example);

        if(brand==null){
            throw  new LyException(ExceptionEnum.BRAND_CREATE_FAILED);
        }
        return brand;
    }

    /**
     *
     * @param ids
     * @return
     */
    @Override
    public List<Brand> queryBrandByids(List<Long> ids) {

        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);
        List<Brand> brands = brandMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(brands)){
            throw  new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brands;
    }

    /**
     * 根据bid在中间件表获取category_id再进行查询Category
     * @param bid
     * @return
     */
    @Override
    public List<Category> queryCategoryByBid(Long bid) {

        if(bid==null){
            throw  new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        List<Category> categories = brandMapper.queryCategoryByBid(bid);
        if(CollectionUtils.isEmpty(categories)){
            throw  new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return categories;
    }

    /**
     * 根据cid通过中间表查询先获取brand_id,再查询多Brand
     * @param cid
     * @return
     */
    @Override
    public List<Brand> queryBrandByCid(Long cid) {
        if(cid == null){
            throw  new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        List<Brand> brands = brandMapper.queryBrandByCid(cid);
        if(CollectionUtils.isEmpty(brands)){
            throw  new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brands;
    }

    /**
     * 更新品牌
     * @param brandVo
     */
    @Transactional
    @Override
    public void updateBrand(BrandVo brandVo) {

        Brand brand = new Brand();
        if(brandVo.getId()!=null){
            brand.setId(brandVo.getId());
        }
        if(brandVo.getName()!=null){
            brand.setName(brandVo.getName());
        }

        if(brandVo.getImage()!=null){
            brand.setImage(brandVo.getImage());
        }

        if(brandVo.getLetter()!=null){
            brand.setLetter(brandVo.getLetter());
        }

        int count = brandMapper.updateByPrimaryKey(brand);

        if(count==0){
            throw new LyException(ExceptionEnum.UPDATE_BRAND_FAILED);
        }

        //更新中间表

        List<Long> cids = brandVo.getCids();

        int resultCount = brandMapper.deleteCategoryBrandByBid(brandVo.getId());

        if( resultCount==0){
            throw new LyException(ExceptionEnum.UPDATE_BRAND_FAILED);
        }

        for(Long cid:cids){
            //添加中间表
            int saveCount = brandMapper.saveCategoryBrand(cid, brandVo.getId());
            if(saveCount==0){
                throw new LyException(ExceptionEnum.UPDATE_BRAND_FAILED);
            }
        }

    }
}
