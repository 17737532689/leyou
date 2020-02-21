package com.leyou.service.service.impl;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Spu;
import com.leyou.service.service.GoodsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsServiceImplTest {

    @Autowired
    private GoodsService goodsService;
    @Test
    public void querySpuByPage() {
        PageResult<Spu> spuPageResult = goodsService.querySpuByPage(1, 2, "id", true, "华为", true);

    }
}