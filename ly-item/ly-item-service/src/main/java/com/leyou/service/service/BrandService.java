package com.leyou.service.service;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.vo.BrandVo;

import java.util.List;

public interface BrandService {

    PageResult<Brand> queryBrandAndSort(Integer page,Integer rows,String sortBy,Boolean desc,String key);

    void saveBrand(Brand brand, List<Long>cids);

    void deleteBrand(Long bid);

    Brand queryByBid(Long id);

    List<Brand> queryBrandByids(List<Long> ids);

    List<Category> queryCategoryByBid(Long bid);

    List<Brand> queryBrandByCid(Long cid);

    void updateBrand(BrandVo brandVo);


}
