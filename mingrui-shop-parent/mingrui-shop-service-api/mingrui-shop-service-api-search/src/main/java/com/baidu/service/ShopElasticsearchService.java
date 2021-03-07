package com.baidu.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.response.GoodsResponse;
import com.baidu.shop.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api(tags = "es接口")
public interface ShopElasticsearchService {

    @ApiOperation(value = "ES商品数据初始化-->索引创建,映射创建,mysql数据同步")
    @GetMapping(value = "es/initGoodsEsData")
    Result<JSONObject> initGoodsEsData();

    @ApiOperation(value = "清空ES中的商品数据")
    @GetMapping(value = "es/clearGoodsEsData")
    Result<JSONObject> clearGoodsEsData();

    @ApiOperation(value = "搜索")
    @GetMapping(value = "es/search")
    GoodsResponse search(@RequestParam String search, @RequestParam Integer page , @RequestParam String filter);

}
