package com.leyou.service.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Spu;
import com.leyou.service.mapper.SpuMapper;
import com.leyou.service.service.GoodsService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuMapper spuMapper;
    /**
     * 分页查询
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @param key
     * @param saleable
     * @return
     */
    @Override
    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key, Boolean saleable) {
       //开启分页
        PageHelper.startPage(page,rows);

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(key)){
            criteria.orLike("title","%"+key+"%");
        }
        if(saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }
        //排序方式
        example.setOrderByClause("last_update_time  desc");

        //只查询未删除的商品
        criteria.andEqualTo("valid",1);

        List<Spu> spus = spuMapper.selectByExample(example);

        if(CollectionUtils.isEmpty(spus)){
            throw  new LyException(ExceptionEnum.SPU_NOT_FOUND);
        }

        PageInfo<Spu> spuPageInfo = new PageInfo<>(spus);

        return new PageResult<>(spuPageInfo.getTotal(),spus);
    }
}
