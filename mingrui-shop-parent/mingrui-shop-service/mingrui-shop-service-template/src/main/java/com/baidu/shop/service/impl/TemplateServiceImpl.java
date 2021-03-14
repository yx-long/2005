package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecParamFeign;
import com.baidu.shop.service.TemplateService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class TemplateServiceImpl extends BaseApiService implements TemplateService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private SpecParamFeign specParamFeign;

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private TemplateEngine templateEngine;

    @Value(value = "${mrshop.static.html.path}")
    private String htmlPath;

    private final Integer CREATE_STATIC_HTML = 1;

    private final Integer DELETE_STATIC_HTML = 2;

    @Override
    public Result<JSONObject> initStaticHTMLTemplate() {
        this.operationStaticHTML(CREATE_STATIC_HTML);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> clearStaticHTMLTemplate() {
        this.operationStaticHTML(DELETE_STATIC_HTML);
        return this.setResultSuccess();
    }

    private Boolean operationStaticHTML(Integer operation) {
        try {
            Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(new SpuDTO());
            if (spuInfo.isSuccess()) {
                spuInfo.getData().stream().forEach(spuDTO -> {
                    if (operation == 1) {
                        this.createStaticHTMLTemplate(spuDTO.getId());
                    } else {
                        this.deleteStaticHTMLTemplate(spuDTO.getId());
                    }
                });
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Result<JSONObject> deleteStaticHTMLTemplate(Integer spuId) {
        File file = new File(htmlPath, spuId + ".html");
        if (file.exists()) {
            file.delete();
        }
        return this.setResultSuccess();
    }

    // 通过spuId创建html文件
    @Override
    public Result<JSONObject> createStaticHTMLTemplate(Integer spuId) {

        Map<String, Object> goodsMap = getGoodsInfo(spuId);

        Context context = new Context();
        context.setVariables(goodsMap);

        File file = new File(htmlPath, spuId + ".html");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file, "UTF-8");
            templateEngine.process("item", context, writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            if (ObjectUtil.isNotNull(writer))
                writer.close();
        }
        return this.setResultSuccess();
    }

    private Map<String, Object> getGoodsInfo(Integer spuId) {
        Map<String, Object> goodsMap = new HashMap<>();
        //spu
        SpuDTO spuResultData = getSpuInfo(spuId);
        goodsMap.put("spuInfo", spuResultData);
        // spuDetail中的信息
        goodsMap.put("spuDetail", getSpuDetail(spuId));
        //分类中的信息
        goodsMap.put("categoryInfo", getCategoryInfo(spuResultData.getCid1(), spuResultData.getCid2(), spuResultData.getCid3()));
        //品牌信息
        goodsMap.put("brandInfo", getBrandInfo(spuResultData.getId()));
        //sku信息
        goodsMap.put("skus", getSkus(spuId));
        //规格组/规格参数
        goodsMap.put("specGroupParam", getSpecGroupParam(spuResultData.getCid3()));
        //特殊规格信息
        goodsMap.put("specParamMap", getSpecParamMap(spuResultData.getCid3()));
        return goodsMap;
    }

    private SpuDTO getSpuInfo(Integer spuId) {
        Map<String, Object> map = new HashMap<>();
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        SpuDTO spuResultData = null;
        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(spuDTO);
        if (spuInfo.isSuccess()) {
            spuResultData = spuInfo.getData().get(0);
        }
        return spuResultData;
    }

    private SpuDetailEntity getSpuDetail(Integer spuId) {
        SpuDetailEntity spuDetail = null;
        Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailBySpuId(spuId);
        if (spuDetailResult.isSuccess()) {
            spuDetail = spuDetailResult.getData();
        }
        return spuDetail;
    }

    private List<CategoryEntity> getCategoryInfo(Integer cid1, Integer cid2, Integer cid3) {
        Result<List<CategoryEntity>> categoryResult = categoryFeign.getCateByIds(String.join(",", Arrays.asList(cid1 + "", cid2 + "", cid3 + "")));
        List<CategoryEntity> categoryData = null;
        if (categoryResult.isSuccess()) {
            categoryData = categoryResult.getData();
        }
        return categoryData;
    }

    private BrandEntity getBrandInfo(Integer brandId) {
        BrandEntity brandEntity = null;
        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setId(brandId);
        Result<PageInfo<BrandEntity>> brandResult = brandFeign.bandList(brandDTO);
        if (brandResult.isSuccess()) {
            brandEntity = brandResult.getData().getList().get(0);
        }
        return brandEntity;
    }

    private List<SkuDTO> getSkus(Integer spuId) {
        List<SkuDTO> skuList = null;

        Result<List<SkuDTO>> skusResult = goodsFeign.getSkusBySpuId(spuId);
        if (skusResult.isSuccess()) {
            skuList = skusResult.getData();
        }
        return skuList;
    }

    private List<SpecGroupDTO> getSpecGroupParam(Integer cid3) {
        SpecGroupDTO specGropDTO = new SpecGroupDTO();
        specGropDTO.setCid(cid3);
        Result<List<SpecGroupEntity>> specGroupResult = specParamFeign.spceGroupList(specGropDTO);
        List<SpecGroupDTO> specGroupAndParam = null;
        if (specGroupResult.isSuccess()) {

            List<SpecGroupEntity> specGroupList = specGroupResult.getData();

            specGroupAndParam = specGroupList.stream().map(specGroup -> {

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
        }
        return specGroupAndParam;
    }

    private Map<Integer, String> getSpecParamMap(Integer cid3) {
        Map<Integer, String> specParamMap = new HashMap<>();
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(cid3);
        specParamDTO.setGeneric(false);
        Result<List<SpecParamEntity>> spceParamsResult = specParamFeign.getSpceParamsList(specParamDTO);
        if (spceParamsResult.isSuccess()) {
            List<SpecParamEntity> specParamEntityList = spceParamsResult.getData();
            specParamEntityList.stream().forEach(specParam -> specParamMap.put(specParam.getId(), specParam.getName()));
        }
        return specParamMap;
    }

}
