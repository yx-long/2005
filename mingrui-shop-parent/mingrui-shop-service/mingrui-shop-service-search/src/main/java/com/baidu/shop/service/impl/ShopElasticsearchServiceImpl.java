package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.service.ShopElasticsearchService;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpceParamsDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpceParamsEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecGroupFeign;
import com.baidu.shop.utils.JSONUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ShopElasticsearchServiceImpl extends BaseApiService implements ShopElasticsearchService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private SpecGroupFeign specGroupFeign;

    @Override
    public Result<JSONObject> esGoodsTest() {
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setPage(1);
        spuDTO.setRows(5);
        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(spuDTO);
        System.out.println(spuInfo);
        if (spuInfo.isSuccess()) {
            List<SpuDTO> spuList = spuInfo.getData();
            List<GoodsDoc> goodsDocList = spuList.stream().map(spu -> {
                GoodsDoc goodsDoc = new GoodsDoc();
                goodsDoc.setId(spu.getId().longValue());
                goodsDoc.setTitle(spu.getTitle());
                goodsDoc.setBrandName(spu.getBrandName());
                goodsDoc.setCategoryName(spu.getCategoryName());
                goodsDoc.setBrandId(spu.getBrandId().longValue());
                goodsDoc.setCreateTime(spu.getCreateTime());
                goodsDoc.setCreateTime(spu.getCreateTime());
                goodsDoc.setCid1(spu.getCid1().longValue());
                goodsDoc.setCid2(spu.getCid2().longValue());
                goodsDoc.setCid3(spu.getCid3().longValue());


                Result<List<SkuDTO>> skusInfo = goodsFeign.getSkusBySpuId(spu.getId());
                if (skusInfo.isSuccess()) {
                    List<SkuDTO> skuList = skusInfo.getData();
                    List<Long> priceList = new ArrayList<>();
                    List<Map<String, Object>> mapSku = skuList.stream().map(sku -> {
                        Map<String, Object> skuMap = new HashMap<>();
                        skuMap.put("id", sku.getId());
                        skuMap.put("title", sku.getTitle());
                        skuMap.put("images", sku.getImages());
                        skuMap.put("price", sku.getPrice());
                        priceList.add(sku.getPrice().longValue());
                        return skuMap;
                    }).collect(Collectors.toList());
                    goodsDoc.setPrice(priceList);
                    goodsDoc.setSkus(JSONUtil.toJsonString(mapSku));
                }
                SpceParamsDTO specParamDTO = new SpceParamsDTO();
                specParamDTO.setCid(spu.getCid3());
                specParamDTO.setSearching(true);
                Result<List<SpceParamsEntity>> specParamInfo = specGroupFeign.spceParamsList(specParamDTO);
                if (specParamInfo.isSuccess()) {
                    List<SpceParamsEntity> specParamList = specParamInfo.getData();
                    Result<SpuDetailEntity> spuDetailInfo = goodsFeign.getSpuDetailBySpuId(spu.getId());
                    if (specParamInfo.isSuccess()) {
                        SpuDetailEntity spuDetailEntity = spuDetailInfo.getData();
                        Map<String, String> genericSpec = JSONUtil.toMapValueString(spuDetailEntity.getGenericSpec());
                        Map<String, List<String>> specialSpec = JSONUtil.toMapValueStrList(spuDetailEntity.getSpecialSpec());
                        Map<String, Object> specMap = new HashMap<>();
                        specParamList.stream().forEach(specParam -> {
                            if (specParam.getGeneric()) {//判断从那个map集合中获取数据
                                if (specParam.getNumeric() && !Strings.isEmpty(specParam.getSegments())) {
                                    specMap.put(specParam.getName(), chooseSegment(genericSpec.get(specParam.getId() + ""), specParam.getSegments(), specParam.getUnit()));
                                } else {
                                    specMap.put(specParam.getName(), genericSpec.get(specParam.getId() + ""));
                                }
                            } else {
                                specMap.put(specParam.getName(), specialSpec.get(specParam.getId()) + "");
                            }
                        });
                        goodsDoc.setSpecs(specMap);
                    }
                }
                return goodsDoc;
            }).collect(Collectors.toList());
            System.out.println(goodsDocList);
        }
        return null;
    }

    // 将具体值转换为 区间值
    private String chooseSegment(String value, String segments, String unit) {//800 -> 5000-1000
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : segments.split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + unit + "以上";
                } else if (begin == 0) {
                    result = segs[1] + unit + "以下";
                } else {
                    result = segment + unit;
                }
                break;
            }
        }
        return result;
    }
}
