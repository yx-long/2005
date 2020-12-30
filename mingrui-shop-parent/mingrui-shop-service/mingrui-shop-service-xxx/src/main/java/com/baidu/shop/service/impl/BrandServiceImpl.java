package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.PinyinUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Transactional
    @Override
    public Result<JSONObject> updateBrand(BrandDTO brandDTO) {
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().toCharArray()[0]), false).toCharArray()[0] + "");
        brandMapper.updateByPrimaryKeySelective(brandEntity);

        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId", brandEntity.getId());
        categoryBrandMapper.deleteByExample(example);

        String categories = brandDTO.getCategories();
        if (StringUtils.isEmpty(brandDTO.getCategories())) return this.setResultError("没有获取到分类");
        List<CategoryBrandEntity> categoryBrandEntities = new ArrayList<>();

        if (categories.contains(",")) {
            /*String[] split = categories.split(",");
            for(String s : split){
                CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
                categoryBrandEntity.setCategoryId(brandEntity.getId());
                categoryBrandEntity.setBrandId(Integer.parseInt(s));
                categoryBrandEntities.add(categoryBrandEntity);
            }*/
            categoryBrandMapper.insertList(Arrays.asList(categories.split(","))
                    .stream()
                    .map(categoryIdStr ->
                            new CategoryBrandEntity(Integer.parseInt(categoryIdStr)
                                    , brandEntity.getId()))
                    .collect(Collectors.toList()));
        } else {
            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
            categoryBrandEntity.setCategoryId(brandEntity.getId());
            categoryBrandEntity.setBrandId(Integer.parseInt(categories));
            categoryBrandMapper.insertSelective(categoryBrandEntity);
        }

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> saveBrand(BrandDTO brandDTO) {

        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        brandEntity.setLetter(String.valueOf(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().toCharArray()[0]), false).toCharArray()[0]));
        brandMapper.insertSelective(brandEntity);

        String categories = brandDTO.getCategories();
        if (StringUtils.isEmpty(brandDTO.getCategories())) return this.setResultError("没有获得集合");

        List<CategoryBrandEntity> categoryBrandEntities = new ArrayList<>();
        if (categories.contains(",")) {
            //多个分类 --> 批量新增
            categoryBrandMapper.insertList(
                    Arrays.asList(categories.split(","))
                            .stream()
                            .map(categoryIdStr -> new CategoryBrandEntity(Integer.valueOf(categoryIdStr)
                                    , brandEntity.getId()))
                            .collect(Collectors.toList())
            );
        /*if(categories.contains(",")){
            String[] split = categories.split(",");
            for (String s : split) {
                CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
                categoryBrandEntity.setBrandId(brandEntity.getId());
                categoryBrandEntity.setCategoryId(Integer.parseInt(s));
                categoryBrandEntities.add(categoryBrandEntity);
            }*/

        } else {
            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
            categoryBrandEntity.setBrandId(brandEntity.getId());
            categoryBrandEntity.setCategoryId(Integer.parseInt(categories));
            categoryBrandMapper.insertSelective(categoryBrandEntity);
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<PageInfo<BrandEntity>> bandList(BrandDTO brandDTO) {

        if (!StringUtils.isEmpty(brandDTO.getSort())) PageHelper.orderBy(brandDTO.getOrder());

        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);

        PageHelper.startPage(brandDTO.getPage(), brandDTO.getRows());
        Example example = new Example(BrandEntity.class);
        example.createCriteria().andLike("name", "%" + brandEntity.getName() + "%");

        List<BrandEntity> brandEntities = brandMapper.selectByExample(example);
        PageInfo<BrandEntity> pageInfo = new PageInfo<>(brandEntities);

        return this.setResultSuccess(pageInfo);
    }
}
