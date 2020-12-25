package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

@Api(tags = "品牌接口")
public interface BrandService {
    @ApiOperation(value = "品牌查询")
    @GetMapping(value = "brand/list")
    Result<PageInfo<BrandEntity>> bandList(BrandDTO brandDTO);
}
