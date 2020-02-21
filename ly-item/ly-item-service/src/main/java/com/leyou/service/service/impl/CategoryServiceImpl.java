package com.leyou.service.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.Category;
import com.leyou.service.mapper.CategoryMapper;
import com.leyou.service.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据商品分类id查询
     * @param pid
     * @return
     */
    @Override
    public List<Category> queryCategoryByPid(Long pid) {
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parentId",pid);
        if(pid<0){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        List<Category> categories = categoryMapper.selectByExample(example);
        return categories;
    }

    /**
     * 根据商品ids查询
     * @param ids
     * @return
     */
    @Override
    public List<Category> queryCategoryByIds(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
         throw  new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        List<Category> categories = categoryMapper.selectByIdList(ids);
        return categories;
    }

    /**
     *根据cid3查询分类
     * @param id
     * @return
     */
    @Override
    public List<Category> queryAllByCid3(Long id) {
        Category category3 = categoryMapper.selectByPrimaryKey(id);
        Category category2 = categoryMapper.selectByPrimaryKey(category3.getParentId());
        Category category1 = categoryMapper.selectByPrimaryKey(category2.getParentId());
        List<Category> categories = Arrays.asList(category1, category2, category3);
        if(CollectionUtils.isEmpty(categories)){
            throw  new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return categories;
    }
}
