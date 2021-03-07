package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品分类接口")
public interface CategoryService {

    @ApiOperation(value = "通过接口查询商品分类")
    @GetMapping(value = "category/list")
    Result<List<CategoryEntity>> getCategoryByPid(Integer pid);

    @ApiOperation(value = "查询品牌分类")
    @GetMapping(value = "category/brand")
    Result<List<CategoryEntity>> brandCategoryByPid(Integer brandId);

    @ApiModelProperty(value = "通过Id删除分类数据")
    @DeleteMapping(value = "category/delete")
    Result<JsonObject> deleteCategoryById(Integer id);

    @ApiModelProperty(value = "通过Id修改分类数据")
    @PutMapping(value = "category/update")
    Result<JsonObject> updateCategoryById(@Validated({MingruiOperation.Update.class}) @RequestBody CategoryEntity categoryEntity);

    @ApiModelProperty(value = "新增数据")
    @PostMapping(value = "category/save")
    Result<JsonObject> addCategoryById(@Validated({MingruiOperation.Add.class}) @RequestBody CategoryEntity categoryEntity);

    @ApiOperation(value = "通过id集合查询分类信息")
    @GetMapping(value = "category/getCateByIds")
    Result<List<CategoryEntity>> getCateByIds(@RequestParam String cateIds);

}
