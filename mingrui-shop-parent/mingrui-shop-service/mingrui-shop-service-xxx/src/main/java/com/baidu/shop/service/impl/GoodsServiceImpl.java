package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.dto.SpuDetailDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private SpuMapper spuMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private SpuDetailMapper spuDetailMapper;

    @Transactional
    @Override
    public Result<JSONObject> goodsXia(SpuDTO spuDTO) {

        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        if(ObjectUtil.isNotNull(spuEntity.getSaleable()) && spuEntity.getSaleable() != 2) {
            if (spuEntity.getSaleable() == 1) {
                spuEntity.setSaleable(0);
                spuMapper.updateByPrimaryKeySelective(spuEntity);
                return this.setResultSuccess("下架成功");
            }

            if (spuEntity.getSaleable() == 0) {
                spuEntity.setSaleable(1);
                spuMapper.updateByPrimaryKeySelective(spuEntity);
                return this.setResultSuccess("上架成功");
            }
        }
        return this.setResultError("下架失败");
    }

    //通过spuId查询Sku中的商品信息
    @Override
    public Result<List<SkuDTO>> getSkusBySpuId(Integer spuId) {
        List<SkuDTO> list = skuMapper.getSkusAndStockBySpuId(spuId);
        return this.setResultSuccess(list);
    }

    //通过spuId查询spuDetail中的商品信息
    @Override
    public Result<SpuDetailEntity> getSpuDetailBySpuId(Integer spuId) {
        SpuDetailEntity spuDetailEntity = spuDetailMapper.selectByPrimaryKey(spuId);
        return this.setResultSuccess(spuDetailEntity);
    }

    //商品查询
    @Override
    public Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO) {

        if (ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows()))
            PageHelper.startPage(spuDTO.getPage(), spuDTO.getRows());

        if (!StringUtils.isEmpty(spuDTO.getSort()) && !StringUtils.isEmpty(spuDTO.getOrder()))
            PageHelper.orderBy(spuDTO.getOrder());

        Example example = new Example(SpuEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if (ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() < 2)
            criteria.andEqualTo("saleable", spuDTO.getSaleable());

        if (!StringUtils.isEmpty(spuDTO.getTitle()))
            criteria.andLike("title", "%" + spuDTO.getTitle() + "%");

        List<SpuEntity> goodsEntities = spuMapper.selectByExample(example);

        List<SpuDTO> collect = goodsEntities.stream().map(goodEntity -> {

            SpuDTO spuDTO1 = BaiduBeanUtil.copyProperties(goodEntity, SpuDTO.class);
            //通过cid查询分类
            List<CategoryEntity> categoryEntities = categoryMapper.selectByIdList(Arrays.asList(goodEntity.getCid1(), goodEntity.getCid2(), goodEntity.getCid3()));
            String collect1 = categoryEntities.stream().map(categoryEntity -> categoryEntity.getName()).collect(Collectors.joining("/"));
            spuDTO1.setCategoryName(collect1);

            //通过brandId查询品牌
            BrandEntity brandEntity = brandMapper.selectByPrimaryKey(goodEntity.getBrandId());
            spuDTO1.setBrandName(brandEntity.getName());
            return spuDTO1;
        }).collect(Collectors.toList());

        PageInfo<SpuEntity> goodsEntityPageInfo = new PageInfo<>(goodsEntities);

        return this.setResult(HTTPStatus.OK, goodsEntityPageInfo.getTotal() + "", collect);
    }

    //商品新增
    @Transactional
    @Override
    public Result<JSONObject> goodsSave(SpuDTO spuDTO) {
        final Date date = new Date();
        //spu新增
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setValid(1);
        spuEntity.setSaleable(1);
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        spuMapper.insertSelective(spuEntity);

        //spuDetail新增
        SpuDetailDTO spuDetailDTO = spuDTO.getSpuDetail();
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDetailDTO, SpuDetailEntity.class);
        spuDetailEntity.setSpuId(spuEntity.getId());
        spuDetailMapper.insertSelective(spuDetailEntity);

        this.addOrPutGoods(spuDTO, spuDTO.getId(), date);
        return this.setResultSuccess();
    }

    //商品修改
    @Transactional
    @Override
    public Result<JSONObject> goodsEdit(SpuDTO spuDTO) {

        final Date date = new Date();
        //修改spu
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setLastUpdateTime(date);
        spuMapper.updateByPrimaryKeySelective(spuEntity);

        //修改spuDetail
        spuDetailMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class));

        this.deleteGoods(spuEntity.getId());

        this.addOrPutGoods(spuDTO, spuEntity.getId(), date);
        return this.setResultSuccess();
    }

    //删除
    @Transactional
    @Override
    public Result<JSONObject> deleteSkusBySpuId(Integer spuId) {

        //通过spuId删除spu表中的数据
        spuMapper.deleteByPrimaryKey(spuId);

        //通过spuId删除spuDetail中的数据
        spuDetailMapper.deleteByPrimaryKey(spuId);

        //批量删除sku和stock中的数据
        this.deleteGoods(spuId);

        return this.setResultSuccess();
    }

    private void deleteGoods(Integer spuId){
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId", spuId);
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);

        List<Long> collect = skuEntities.stream().map(skuEntity -> skuEntity.getId()).collect(Collectors.toList());
        skuMapper.deleteByIdList(collect);
        stockMapper.deleteByIdList(collect);
    }

    private void addOrPutGoods(SpuDTO spuDTO, Integer spuId, Date date) {
        List<SkuDTO> skus = spuDTO.getSkus();
        skus.stream().forEach(skuDTO -> {
            //新增sku
            SkuEntity skuEntity = BaiduBeanUtil.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);
            //新增stock
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            stockMapper.insertSelective(stockEntity);
        });
    }
}
