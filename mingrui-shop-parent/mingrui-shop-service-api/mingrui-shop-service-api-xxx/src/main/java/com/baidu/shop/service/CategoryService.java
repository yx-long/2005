package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品分类接口")
public interface CategoryService {

    @ApiOperation(value = "通过接口查询商品分类")
    @GetMapping(value = "category/list")
    Result<List<CategoryEntity>> getCategoryByPid(Integer pid);

    @ApiModelProperty(value = "通过Id删除分类数据")
    @DeleteMapping(value = "category/delete")
    Result<JsonObject> deleteCategoryById(Integer id);

    @ApiModelProperty(value = "通过Id修改分类数据")
    @PutMapping(value = "category/update")
    Result<JsonObject> updateCategoryById(@RequestBody CategoryEntity categoryEntity);

    @ApiModelProperty(value = "新增数据")
    @PostMapping(value = "category/save")
    Result<JsonObject> addCategoryById(@RequestBody CategoryEntity categoryEntity);

}
