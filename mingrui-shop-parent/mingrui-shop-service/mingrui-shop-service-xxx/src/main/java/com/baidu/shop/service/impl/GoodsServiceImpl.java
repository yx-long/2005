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
        //判断saleable是否为空和是否等于2和是否为空
        if (ObjectUtil.isNotNull(spuEntity.getSaleable()) && spuEntity.getSaleable() != 2) {
            //如果saleble等于1的话就修改为0
            if (spuEntity.getSaleable() == 1) {
                spuEntity.setSaleable(0);
                spuMapper.updateByPrimaryKeySelective(spuEntity);
                return this.setResultSuccess("下架成功");
            }
            //如果前台穿过来的saleable等于0的话就修改为1
            if (spuEntity.getSaleable() == 0) {
                spuEntity.setSaleable(1);
                spuMapper.updateByPrimaryKeySelective(spuEntity);
                return this.setResultSuccess("上架成功");
            }
        }
        //否则就是失败
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
        //分页插件
        if (ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows()))
            PageHelper.startPage(spuDTO.getPage(), spuDTO.getRows());
        //排序
        if (!StringUtils.isEmpty(spuDTO.getSort()) && !StringUtils.isEmpty(spuDTO.getOrder()))
            //通过插件来进行排序
            PageHelper.orderBy(spuDTO.getOrder());
        //通过Example进行拼接
        Example example = new Example(SpuEntity.class);
        Example.Criteria criteria = example.createCriteria();
        //判断saleable是否为空和是否小于2 如果小于2这个值是上架或者未上架状态 大于2可以查询所有
        if (ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() < 2)
            criteria.andEqualTo("saleable", spuDTO.getSaleable());
        //判断标题是否为空 不为空就进行模糊查询
        if (!StringUtils.isEmpty(spuDTO.getTitle()))
            criteria.andLike("title", "%" + spuDTO.getTitle() + "%");
        if (ObjectUtil.isNotNull(spuDTO.getId()))
            criteria.andEqualTo("id", spuDTO.getId());
        //通过example查询分页和排序返回一个集合
        List<SpuEntity> goodsEntities = spuMapper.selectByExample(example);
        //通过lamdba表达式遍历查询返回一个集合
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
        //获得分页的总条数
        PageInfo<SpuEntity> goodsEntityPageInfo = new PageInfo<>(goodsEntities);
        //返回时需要吧查询结果和查询到的分页返回分页是数字类型需要转换为字符串
        return this.setResult(HTTPStatus.OK, goodsEntityPageInfo.getTotal() + "", collect);
    }

    //商品新增
    @Transactional
    @Override
    public Result<JSONObject> goodsSave(SpuDTO spuDTO) {
        //保证时间一致性不会发生改变
        final Date date = new Date();
        //spu新增
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        //给valid赋默认值
        spuEntity.setValid(1);
        //给saleable赋默认值
        spuEntity.setSaleable(1);
        //给开始时间赋默认值
        spuEntity.setCreateTime(date);
        //给修改时间赋默认值
        spuEntity.setLastUpdateTime(date);
        //执行新增
        spuMapper.insertSelective(spuEntity);
        //spuDetail新增
        SpuDetailDTO spuDetailDTO = spuDTO.getSpuDetail();
        //需要spuDetail返回的主键
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDetailDTO, SpuDetailEntity.class);
        //获得spu表中的主键
        spuDetailEntity.setSpuId(spuEntity.getId());
        //执行新增
        spuDetailMapper.insertSelective(spuDetailEntity);
        //新增代码重复提取到外面进行了封装
        this.addOrPutGoods(spuDTO, spuDTO.getId(), date);
        //成功返回
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
        //使用批量删除来删除中间表中的数据
        this.deleteGoods(spuEntity.getId());
        //执行新增
        this.addOrPutGoods(spuDTO, spuEntity.getId(), date);
        return this.setResultSuccess();
    }

    //商品删除
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

    //删除封装
    private void deleteGoods(Integer spuId) {
        //通过Examble拼接
        Example example = new Example(SkuEntity.class);
        //通过spuId去查询
        example.createCriteria().andEqualTo("spuId", spuId);
        //通过selectByExamble查询返回一个集合
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);
        //使用lamdba表达式便利集合返回一个新的集合
        List<Long> collect = skuEntities.stream().map(skuEntity -> skuEntity.getId()).collect(Collectors.toList());
        //批量删除sku表中的数据
        skuMapper.deleteByIdList(collect);
        //批量删除stock表中的数据
        stockMapper.deleteByIdList(collect);
    }

    //新增封装
    private void addOrPutGoods(SpuDTO spuDTO, Integer spuId, Date date) {
        //获得前台的skus集合
        List<SkuDTO> skus = spuDTO.getSkus();
        //遍历集合后新增
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
