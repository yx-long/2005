package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpceGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpceGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "规格接口")
public interface SpceGroupService {

    @GetMapping(value = "specparams/list")
    @ApiOperation(value = "查询规格参数")
    Result<List<SpecParamEntity>> getSpceParamsList(@SpringQueryMap SpecParamDTO specParamDTO);

    @PostMapping(value = "specparams/save")
    @ApiOperation(value = "新增规格参数")
    Result<JSONObject> saveParamsList(@RequestBody SpecParamDTO specParamDTO);

    @PutMapping(value = "specparams/save")
    @ApiOperation(value = "新增规格参数")
    Result<JSONObject> editParamsList(@RequestBody SpecParamDTO specParamDTO);

    @DeleteMapping(value = "specparams/delete/{id}")
    @ApiOperation(value = "新增规格参数")
    Result<JSONObject> deleteParamsList(@PathVariable Integer id);

    @GetMapping(value = "specGroup/list")
    @ApiOperation(value = "通过条件查询规格组")
    Result<List<SpceGroupEntity>> spceGroupList(SpceGroupDTO spceGroupDTO);

    @PostMapping(value = "specGroup/save")
    @ApiOperation(value = "规格新增")
    Result<JSONObject> saveSpceGroup(@RequestBody SpceGroupDTO spceGroupDTO);

    @PutMapping(value = "specGroup/save")
    @ApiOperation(value = "规格修改")
    Result<JSONObject> editSpceGroup(@RequestBody SpceGroupDTO spceGroupDTO);

    @DeleteMapping(value = "specGroup/delete/{id}")
    @ApiOperation(value = "通过id删除规格")
    Result<JSONObject> deleteSpceGroup(@PathVariable Integer id);

}
