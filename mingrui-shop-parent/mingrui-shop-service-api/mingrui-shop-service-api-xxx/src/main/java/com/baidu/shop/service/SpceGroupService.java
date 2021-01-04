package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpceGroupDTO;
import com.baidu.shop.dto.SpceParamsDTO;
import com.baidu.shop.entity.SpceGroupEntity;
import com.baidu.shop.entity.SpceParamsEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "规格接口")
public interface SpceGroupService {

    @GetMapping(value = "specparams/list")
    @ApiOperation(value = "查询规格参数")
    Result<List<SpceParamsEntity>> spceParamsList(SpceParamsDTO spceParamsDTO);

    @PostMapping(value = "specparams/save")
    @ApiOperation(value = "新增规格参数")
    Result<JSONObject> saveParamsList(@RequestBody SpceParamsDTO spceParamsDTO);

    @PutMapping(value = "specparams/save")
    @ApiOperation(value = "新增规格参数")
    Result<JSONObject> editParamsList(@RequestBody SpceParamsDTO spceParamsDTO);

    @DeleteMapping(value = "specparams/delete/{id}")
    @ApiOperation(value = "新增规格参数")
    Result<JSONObject> deleteParamsList(@PathVariable Integer id);

    @GetMapping(value = "specgroup/list")
    @ApiOperation(value = "通过条件查询规格组")
    Result<List<SpceGroupEntity>> spceGroupList(SpceGroupDTO spceGroupDTO);

    @PostMapping(value = "specgroup/save")
    @ApiOperation(value = "规格新增")
    Result<JSONObject> saveSpceGroup(@RequestBody SpceGroupDTO spceGroupDTO);

    @PutMapping(value = "specgroup/save")
    @ApiOperation(value = "规格修改")
    Result<JSONObject> editSpceGroup(@RequestBody SpceGroupDTO spceGroupDTO);

        @DeleteMapping(value = "specgroup/delete/{id}")
    @ApiOperation(value = "通过id删除规格")
    Result<JSONObject> deleteSpceGroup(@PathVariable Integer id);
}
