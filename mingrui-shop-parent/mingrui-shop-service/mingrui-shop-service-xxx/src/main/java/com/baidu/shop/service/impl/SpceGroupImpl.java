package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.mapper.SpceGroupMapper;
import com.baidu.shop.mapper.SpceParamsMapper;
import com.baidu.shop.service.SpceGroupService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class SpceGroupImpl extends BaseApiService implements SpceGroupService {

    @Resource
    private SpceGroupMapper spceGroupMapper;

    @Resource
    private SpceParamsMapper spceParamsMapper;

    //规格参数查询
    @Override
    public Result<List<SpecParamEntity>> getSpceParamsList(SpecParamDTO specParamDTO) {

        SpecParamEntity specParamEntity = BaiduBeanUtil.copyProperties(specParamDTO, SpecParamEntity.class);

        Example example = new Example(SpecParamEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if (ObjectUtil.isNotNull(specParamEntity.getGroupId()))
            criteria.andEqualTo("groupId", specParamEntity.getGroupId());

        if (ObjectUtil.isNotNull(specParamEntity.getCid()))
            criteria.andEqualTo("cid", specParamEntity.getCid());

        if (ObjectUtil.isNotNull(specParamDTO.getGeneric()))
            criteria.andEqualTo("generic", specParamEntity.getGeneric());

        List<SpecParamEntity> spceParamsEntities = spceParamsMapper.selectByExample(example);
        return this.setResultSuccess(spceParamsEntities);
    }

    //规格参数新增
    @Transactional
    @Override
    public Result<JSONObject> saveParamsList(SpecParamDTO specParamDTO) {
        SpecParamEntity specParamEntity = BaiduBeanUtil.copyProperties(specParamDTO, SpecParamEntity.class);
        spceParamsMapper.insertSelective(specParamEntity);
        return this.setResultSuccess();
    }

    //规格参数修改
    @Transactional
    @Override
    public Result<JSONObject> editParamsList(SpecParamDTO specParamDTO) {
        spceParamsMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specParamDTO, SpecParamEntity.class));
        return this.setResultSuccess();
    }

    //规格参数删除
    @Transactional
    @Override
    public Result<JSONObject> deleteParamsList(Integer id) {
        spceParamsMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }

    //规格组查询
    @Override
    public Result<List<SpecGroupEntity>> spceGroupList(SpecGroupDTO specGroupDTO) {

        Example example = new Example(SpecGroupEntity.class);
        SpecGroupEntity specGroupEntity = BaiduBeanUtil.copyProperties(specGroupDTO, SpecGroupEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if (ObjectUtil.isNotNull(specGroupEntity.getId()))
            criteria.andEqualTo("groupId", specGroupEntity.getId());

        if (ObjectUtil.isNotNull(specGroupEntity.getCid()))
            criteria.andEqualTo("cid", specGroupEntity.getCid());

        List<SpecGroupEntity> spceGroupEntities = spceGroupMapper.selectByExample(example);
        return this.setResultSuccess(spceGroupEntities);
    }

    //规格组新增
    @Transactional
    @Override
    public Result<JSONObject> saveSpceGroup(SpecGroupDTO specGroupDTO) {
        spceGroupMapper.insertSelective(BaiduBeanUtil.copyProperties(specGroupDTO, SpecGroupEntity.class));
        return this.setResultSuccess();
    }

    //规格组修改
    @Transactional
    @Override
    public Result<JSONObject> editSpceGroup(SpecGroupDTO specGroupDTO) {
        spceGroupMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specGroupDTO, SpecGroupEntity.class));
        return this.setResultSuccess();
    }

    //规格组删除
    @Transactional
    @Override
    public Result<JSONObject> deleteSpceGroup(Integer id) {

        Example example = new Example(SpecParamEntity.class);
        example.createCriteria().andEqualTo("groupId", id);
        List<SpecParamEntity> spceParamsEntities = spceParamsMapper.selectByExample(example);
        if (spceParamsEntities.size() != 0) return this.setResultError("该节点下有数据不能删除");

        spceGroupMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess();
    }
}
