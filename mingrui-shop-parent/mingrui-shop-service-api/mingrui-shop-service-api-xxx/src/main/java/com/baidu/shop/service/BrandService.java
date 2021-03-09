package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "品牌接口")
public interface BrandService {
    @ApiOperation(value = "品牌查询")
    @GetMapping(value = "brand/list")
    Result<PageInfo<BrandEntity>> bandList(@SpringQueryMap BrandDTO brandDTO);

    @ApiOperation(value = "品牌新增")
    @PostMapping(value = "brand/save")
    Result<JSONObject> saveBrand(@RequestBody BrandDTO brandDTO);

    @ApiOperation(value = "品牌修改")
    @PutMapping(value = "brand/save")
    Result<JSONObject> updateBrand(@RequestBody BrandDTO brandDTO);

    @ApiOperation(value = "品牌删除")
    @DeleteMapping(value = "brand/delete")
    Result<JSONObject> deleteBrand(Integer id);

    @ApiOperation(value = "查询所在分类")
    @GetMapping(value = "brand/categoryBrandById")
    Result<List<BrandEntity>> categoryBrandById(Integer cid);

    @ApiOperation(value = "通过品牌id集合获取品牌")
    @GetMapping(value = "brand/getBrandByIds")
    Result<List<BrandEntity>> getBrandByIds(@RequestParam String brandIds);

}
