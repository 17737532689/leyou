package com.leyou.service.service;

import com.leyou.item.pojo.Category;

import java.util.List;

public interface CategoryService {
   //根据父节点查询商品分类
    List<Category> queryCategoryByPid(Long pid);

    List<Category> queryCategoryByIds(List<Long> ids);


    List<Category> queryAllByCid3(Long id);
}
