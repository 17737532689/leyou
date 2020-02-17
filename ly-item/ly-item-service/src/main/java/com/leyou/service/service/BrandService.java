package com.leyou.service.service;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;

public interface BrandService {

    PageResult<Brand> queryBrandAndSort(Integer page,Integer rows,String sortBy,Boolean desc,String key);
}
