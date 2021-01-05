package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.GoodsDTO;
import com.baidu.shop.entity.GoodsEntity;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

@Api(tags = "商品接口")
public interface GoodsService {

    @ApiOperation(value = "商品查询接口")
    @GetMapping(value = "goods/getSpuInfo")
    Result<PageInfo<GoodsEntity>> getSpuInfo(GoodsDTO goodsDTO);

}
