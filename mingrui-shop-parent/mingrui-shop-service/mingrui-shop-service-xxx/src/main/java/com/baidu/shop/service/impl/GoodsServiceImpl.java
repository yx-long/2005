package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.GoodsDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.GoodsEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.mapper.GoodsMapper;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.Arrays;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private BrandMapper brandMapper;

    @Override
    public Result<List<GoodsDTO>> getSpuInfo(GoodsDTO goodsDTO) {

        if (ObjectUtil.isNotNull(goodsDTO.getPage()) && ObjectUtil.isNotNull(goodsDTO.getRows()))
            PageHelper.startPage(goodsDTO.getPage(), goodsDTO.getRows());

        Example example = new Example(GoodsEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if (ObjectUtil.isNotNull(goodsDTO.getSaleable()) && goodsDTO.getSaleable() < 2)
            criteria.andEqualTo("saleable", goodsDTO.getSaleable());

        if (!StringUtils.isEmpty(goodsDTO.getTitle()))
            criteria.andLike("title", "%" + goodsDTO.getTitle() + "%");

        List<GoodsEntity> goodsEntities = goodsMapper.selectByExample(example);

        List<GoodsDTO> collect = goodsEntities.stream().map(goodEntity -> {

            GoodsDTO goodsDTO1 = BaiduBeanUtil.copyProperties(goodEntity, GoodsDTO.class);

            CategoryEntity categoryEntity1 = categoryMapper.selectByPrimaryKey(goodEntity.getCid1());
            CategoryEntity categoryEntity2 = categoryMapper.selectByPrimaryKey(goodEntity.getCid2());
            CategoryEntity categoryEntity3 = categoryMapper.selectByPrimaryKey(goodEntity.getCid3());
            goodsDTO1.setCategoryName(categoryEntity1.getName() + "/" + categoryEntity2.getName() + "/" + categoryEntity3.getName());

            BrandEntity brandEntity = brandMapper.selectByPrimaryKey(goodEntity.getBrandId());
            goodsDTO1.setBrandName(brandEntity.getName());
            return goodsDTO1;
        }).collect(Collectors.toList());

        PageInfo<GoodsEntity> goodsEntityPageInfo = new PageInfo<>(goodsEntities);

        return this.setResult(HTTPStatus.OK, goodsEntityPageInfo.getTotal() + "", collect);
    }

}
