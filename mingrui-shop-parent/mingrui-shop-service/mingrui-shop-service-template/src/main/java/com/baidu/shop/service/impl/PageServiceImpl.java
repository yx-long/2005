package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecParamFeign;
import com.baidu.shop.service.PageService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private SpecParamFeign specParamFeign;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Override
    public Map<String, Object> getGoodsInfo(Integer spuId) {

        Map<String, Object> map = new HashMap<>();

        //spu
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        SpuDTO spuResultData = null;
        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(spuDTO);
        if (spuInfo.isSuccess()) {
            spuResultData = spuInfo.getData().get(0);
            map.put("spuInfo", spuResultData);
        }

        // spuDetail
        Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailBySpuId(spuId);
        if (spuDetailResult.isSuccess())
            map.put("spuDetail", spuDetailResult.getData());

        //分类
        Result<List<CategoryEntity>> categoryResult = categoryFeign.getCateByIds(String.join(",", Arrays.asList(spuResultData.getCid1() + "", spuResultData.getCid2() + "", spuResultData.getCid3() + "")));
        if (categoryResult.isSuccess())
            map.put("categoryInfo", categoryResult.getData());

        //品牌
        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setId(spuResultData.getId());
        Result<PageInfo<BrandEntity>> brandResult = brandFeign.bandList(brandDTO);
        if (brandResult.isSuccess())
            map.put("brandInfo", brandResult.getData().getList().get(0));

        //sku
        Result<List<SkuDTO>> skusResult = goodsFeign.getSkusBySpuId(spuId);
        if (skusResult.isSuccess())
            map.put("skus", skusResult.getData());

        //规格组/规格参数
        SpecGroupDTO specGropDTO = new SpecGroupDTO();
        specGropDTO.setCid(spuResultData.getCid3());
        Result<List<SpecGroupEntity>> specGroupResult = specParamFeign.spceGroupList(specGropDTO);

        if (specGroupResult.isSuccess()) {

            List<SpecGroupEntity> specGroupList = specGroupResult.getData();

            List<SpecGroupDTO> specGroupAndParam = specGroupList.stream().map(specGroup -> {

                SpecGroupDTO specGroupDTO1 = BaiduBeanUtil.copyProperties(specGroup, SpecGroupDTO.class);

                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setGroupId(specGroupDTO1.getId());
                specParamDTO.setGeneric(true);

                Result<List<SpecParamEntity>> specParamResult = specParamFeign.getSpceParamsList(specParamDTO);

                if (specParamResult.isSuccess()) {
                    specGroupDTO1.setSpecList(specParamResult.getData());
                }
                return specGroupDTO1;

            }).collect(Collectors.toList());
            map.put("specGroupParam", specGroupAndParam);
        }
        //特殊规格
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(spuResultData.getCid3());
        specParamDTO.setGeneric(false);
        Result<List<SpecParamEntity>> spceParamsResult = specParamFeign.getSpceParamsList(specParamDTO);
        if (spceParamsResult.isSuccess()) {
            List<SpecParamEntity> specParamEntityList = spceParamsResult.getData();
            HashMap<Integer, String> specParamMap = new HashMap<>();
            specParamEntityList.stream().forEach(specParam -> specParamMap.put(specParam.getId(), specParam.getName()));
            map.put("specParamMap", specParamMap);
        }
        return map;
    }
}
