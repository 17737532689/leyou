package com.leyou.service.service.impl;

import com.leyou.item.pojo.Category;
import com.leyou.service.service.CategoryService;
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
public class CategoryServiceImplTest {
    @Autowired
    private CategoryService categoryService;
    @Test
    public void queryCategoryByPid() {
        List<Category> categories = categoryService.queryCategoryByPid(1L);
        log.info("信息：{}",categories);
    }
}