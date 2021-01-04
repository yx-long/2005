package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpceGroupDTO;
import com.baidu.shop.entity.SpceGroupEntity;
import com.baidu.shop.mapper.SpceGroupMapper;
import com.baidu.shop.service.SpceGroupService;
import com.baidu.shop.utils.BaiduBeanUtil;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class SpceGroupImpl extends BaseApiService implements SpceGroupService {

    @Resource
    private SpceGroupMapper spceGroupMapper;

    @Override
    public Result<List<SpceGroupEntity>> spceGroupList(SpceGroupDTO spceGroupDTO) {
        Example example = new Example(SpceGroupEntity.class);
        example.createCriteria().andEqualTo("cid",
                BaiduBeanUtil.copyProperties(spceGroupDTO, SpceGroupEntity.class).getCid());
        List<SpceGroupEntity> spceGroupEntities = spceGroupMapper.selectByExample(example);
        return this.setResultSuccess(spceGroupEntities);
    }

    @Override
    public Result<JSONObject> saveSpceGroup(SpceGroupDTO spceGroupDTO) {
        spceGroupMapper.insertSelective(BaiduBeanUtil.copyProperties(spceGroupDTO,SpceGroupEntity.class));
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> editSpceGroup(SpceGroupDTO spceGroupDTO) {
        spceGroupMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(spceGroupDTO,SpceGroupEntity.class));
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> deleteSpceGroup(Integer id) {
        spceGroupMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }
}
