package com.leyou.service.service.impl;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.service.service.SpecService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpecServiceImplTest {
    @Autowired
    private SpecService specService;
    @Test
    public void querySpecGroupByCid() {
        List<SpecGroup> specGroups = specService.querySpecGroupByCid(1L);
    }

    @Test
    public void querySpecsByCid() {

        specService.querySpecParams(1L,1L,true,true);
    }

    @Test
    public void saveGroup() {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(1L);
        specGroup.setName("音响");
        specService.saveSpecGroup(specGroup);
    }



}