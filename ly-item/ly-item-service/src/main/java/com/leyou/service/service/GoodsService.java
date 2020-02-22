package com.leyou.service.service;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;

import java.util.List;

public interface GoodsService {
    PageResult<Spu> querySpuByPage(Integer page, Integer rows, String key,Boolean saleable);

    void aadGoods(Spu spu);

    void updateGoods(Spu spu);

    void handleSaleable(Spu spu);

    void deleteGoodsBySpuId(Long spuId);

    List<Sku> querySkuBySpuId(Long spuId);

    List<Sku> querySkusByIds(List<Long> ids);

    Spu querySpuBySpuId(Long spuId);

    SpuDetail querySpuDetailBySpuId(Long spuId);

}
