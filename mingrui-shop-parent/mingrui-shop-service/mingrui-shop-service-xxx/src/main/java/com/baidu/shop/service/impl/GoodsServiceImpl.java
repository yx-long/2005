package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.GoodsDTO;
import com.baidu.shop.entity.GoodsEntity;
import com.baidu.shop.mapper.GoodsMapper;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.utils.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public Result<PageInfo<GoodsEntity>> getSpuInfo(GoodsDTO goodsDTO) {

        if (ObjectUtil.isNotNull(goodsDTO.getPage()) && ObjectUtil.isNotNull(goodsDTO.getRows()))
            PageHelper.startPage(goodsDTO.getPage(), goodsDTO.getRows());

        Example example = new Example(GoodsEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if (ObjectUtil.isNotNull(goodsDTO.getSaleable()) && goodsDTO.getSaleable() != 2)
            criteria.andEqualTo("saleable", goodsDTO.getSaleable());

        if (!StringUtils.isEmpty(goodsDTO.getTitle()))
            criteria.andLike("title", "%" + goodsDTO.getTitle() + "%");

        List<GoodsEntity> goodsEntities = goodsMapper.selectByExample(example);

        PageInfo<GoodsEntity> goodsEntityPageInfo = new PageInfo<>(goodsEntities);

        return this.setResultSuccess(goodsEntityPageInfo);
    }
}
