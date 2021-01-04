package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpceGroupDTO;
import com.baidu.shop.dto.SpceParamsDTO;
import com.baidu.shop.entity.SpceGroupEntity;
import com.baidu.shop.entity.SpceParamsEntity;
import com.baidu.shop.mapper.SpceGroupMapper;
import com.baidu.shop.mapper.SpceParamsMapper;
import com.baidu.shop.service.SpceGroupService;
import com.baidu.shop.utils.BaiduBeanUtil;
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
    public Result<List<SpceParamsEntity>> spceParamsList(SpceParamsDTO spceParamsDTO) {
        SpceParamsEntity spceParamsEntity = BaiduBeanUtil.copyProperties(spceParamsDTO, SpceParamsEntity.class);
        Example example = new Example(SpceParamsEntity.class);
        example.createCriteria().andEqualTo("groupId",spceParamsEntity.getGroupId());
        List<SpceParamsEntity> spceParamsEntities = spceParamsMapper.selectByExample(example);
        return this.setResultSuccess(spceParamsEntities);
    }

    //规格参数新增
    @Transactional
    @Override
    public Result<JSONObject> saveParamsList(SpceParamsDTO spceParamsDTO) {
        SpceParamsEntity spceParamsEntity = BaiduBeanUtil.copyProperties(spceParamsDTO, SpceParamsEntity.class);
        spceParamsMapper.insertSelective(spceParamsEntity);
        return this.setResultSuccess();
    }

    //规格参数修改
    @Transactional
    @Override
    public Result<JSONObject> editParamsList(SpceParamsDTO spceParamsDTO) {
        spceParamsMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(spceParamsDTO,SpceParamsEntity.class));
        return this.setResultSuccess();
    }

    //删除
    @Transactional
    @Override
    public Result<JSONObject> deleteParamsList(Integer id) {
        spceParamsMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }

    //规格组查询
    @Override
    public Result<List<SpceGroupEntity>> spceGroupList(SpceGroupDTO spceGroupDTO) {

        Example example = new Example(SpceGroupEntity.class);
        example.createCriteria().andEqualTo("cid",
                BaiduBeanUtil.copyProperties(spceGroupDTO, SpceGroupEntity.class).getCid());
        List<SpceGroupEntity> spceGroupEntities = spceGroupMapper.selectByExample(example);
        return this.setResultSuccess(spceGroupEntities);
    }

    //规格组修改
    @Transactional
    @Override
    public Result<JSONObject> saveSpceGroup(SpceGroupDTO spceGroupDTO) {
        spceGroupMapper.insertSelective(BaiduBeanUtil.copyProperties(spceGroupDTO,SpceGroupEntity.class));
        return this.setResultSuccess();
    }

    //规格组修改
    @Transactional
    @Override
    public Result<JSONObject> editSpceGroup(SpceGroupDTO spceGroupDTO) {
        spceGroupMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(spceGroupDTO,SpceGroupEntity.class));
        return this.setResultSuccess();
    }

    //规格组删除
    @Transactional
    @Override
    public Result<JSONObject> deleteSpceGroup(Integer id) {

        Example example = new Example(SpceParamsEntity.class);
        example.createCriteria().andEqualTo("groupId", id);
        List<SpceParamsEntity> spceParamsEntities = spceParamsMapper.selectByExample(example);
        if(spceParamsEntities.size() != 0) return this.setResultError("该节点下有数据不能删除");

        spceGroupMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess();
    }
}
