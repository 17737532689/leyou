package com.leyou.service.service.impl;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.vo.BrandVo;
import com.leyou.service.service.BrandService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
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

        /*for(Brand item:items){
            log.info("信息：{}",item);
        }
*/
        items.forEach(i->{
            log.info("信息：{}",i);
        });

    }

    @Test
    public void save() {
        Brand brand = new Brand();
        brand.setName("老杨豹子");
        brand.setImage("http://image.leyou.com/group1/M00/00/00/wKg4ZVro8haAdtRLAALAv4YTyno368.png");
        brand.setLetter('L');
        List<Long> lists = new ArrayList();
        lists.add(1425L);
        brandService.saveBrand(brand,lists);
    }

    @Test
    public void delete() {
      brandService.deleteBrand(325406L);
    }

    @Test
    public void queryByBid() {
        Brand brand = brandService.queryByBid(1115L);

    }

    @Test
    public void queryBrandByids() {
        List<Long> longs = Arrays.asList(1115L, 1528L);
        List<Brand> brands = brandService.queryBrandByids(longs);

    }

    @Test
    public void queryCategoryByBid2() {
        //List<Long> longs = Arrays.asList(1115L, 1528L);
        List<Category> categories = brandService.queryCategoryByBid(18374L);

    }

    @Test
    public void queryBrandByCid() {
        //List<Long> longs = Arrays.asList(1115L, 1528L);
        List<Brand> brands = brandService.queryBrandByCid(84L);

    }



}