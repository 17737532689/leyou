package com.leyou.service.service;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Spu;

public interface GoodsService {
    PageResult<Spu> querySpuByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key,Boolean saleable);

}
