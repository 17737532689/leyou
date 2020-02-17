package com.leyou.service.service.impl;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.service.service.BrandService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class BrandServiceImplTest {

    @Autowired
    private BrandService brandService;
    @Test
    public void queryBrandAndSort() {
        PageResult<Brand> brandPageResult = brandService.queryBrandAndSort(1, 2, "id", true, "H");
        List<Brand> items = brandPageResult.getItems();

        for(Brand item:items){
            log.info("信息：{}",item);
        }


    }
}