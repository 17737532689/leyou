package com.leyou.service.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.service.mapper.SpecGroupMapper;
import com.leyou.service.mapper.SpecParamMapper;
import com.leyou.service.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecServiceImpl implements SpecService {
    @Autowired
    private SpecGroupMapper specGroupMapper;
   @Autowired
    private SpecParamMapper specParamMapper;
    /**
     * 查询
     * @param cid
     * @return
     */
    @Override
    public List<SpecGroup> querySpecGroupByCid(Long cid) {
        if(cid==null){
            throw  new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        Example example = new Example(SpecGroup.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cid",cid);
        List<SpecGroup> specGroups = specGroupMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(specGroups)){
            throw  new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return specGroups;
    }

    /**
     * 新增
     * @param specGroup
     */
    @Override
    public void saveSpecGroup(SpecGroup specGroup) {
        int count = specGroupMapper.insert(specGroup);
        if(count!=1){
            throw  new LyException(ExceptionEnum.SPEC_GROUP_CREATE_FAILED   );
        }
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void deleteSpecGroup(Long id) {
        int count = specGroupMapper.deleteByPrimaryKey(id);
        if(count!=1){
            throw  new LyException(ExceptionEnum.DELETE_SPEC_GROUP_FAILED);
        }
    }

    /**
     * 更新
     * @param specGroup
     */
    @Override
    public void updateSpecGroup(SpecGroup specGroup) {
        int count = specGroupMapper.updateByPrimaryKey(specGroup);
        if (count != 1) {
            throw new LyException(ExceptionEnum.UPDATE_SPEC_GROUP_FAILED);
        }
    }

    /**
     * 规格查询
     * @param gid
     * @param cid
     * @param searching
     * @param generic
     * @return
     */
    @Override
    public List<SpecParam> querySpecParams(Long gid, Long cid, Boolean searching, Boolean generic) {

        Example example = new Example(SpecParam.class);
        Example.Criteria criteria = example.createCriteria();
        if(cid!=null){
            criteria.andEqualTo("groupId",gid);
        }
        if(gid!=null){
            criteria.andEqualTo("cid",cid);
        }
        criteria.andEqualTo("searching",searching);
        criteria.andEqualTo("generic",generic);
        List<SpecParam> specParams = specParamMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(specParams)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return specParams;
    }

    /**
     * 新增
     * @param specParam
     */
    @Override
    public void saveSpecParam(SpecParam specParam) {

        int count = specParamMapper.insert(specParam);
        if(count!=1){
            throw  new LyException(ExceptionEnum.SPEC_PARAM_CREATE_FAILED);
        }
    }

    /**
     * 更新
     * @param specParam
     */
    @Override
    public void updateSpecParam(SpecParam specParam) {

        int count = specParamMapper.updateByPrimaryKey(specParam);
        if(count!=1){
            throw  new LyException(ExceptionEnum.UPDATE_SPEC_PARAM_FAILED);
        }

    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void deleteSpecParam(Long id) {

        int count = specParamMapper.deleteByPrimaryKey(id);
        if(count!=1){
            throw  new LyException(ExceptionEnum.DELETE_SPEC_PARAM_FAILED);
        }
    }

    /**
     * 根据cid查询
     * @param cid
     * @return
     */
    @Override
    public List<SpecGroup> querySpecsByCid(Long cid) {

        Example example = new Example(SpecParam.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cid",cid);
        List<SpecParam> specParams = specParamMapper.selectByExample(example);
        List<SpecGroup> specGroups = querySpecGroupByCid(cid);
       /* for(SpecGroup specGroup:specGroups){
            specGroup.setParams(specParams);
        }*/
        Map<Long,List<SpecParam>> map = new HashMap<>();
        for(SpecParam specParam:specParams){
            Long groupId = specParam.getGroupId();
            if(!map.keySet().contains(groupId)){
                map.put(groupId,new ArrayList<>());
            }
            map.get(groupId).add(specParam);
        }
        for(SpecGroup specGroup:specGroups){
            specGroup.setParams(specParams);
        }
        return specGroups;
    }
}
