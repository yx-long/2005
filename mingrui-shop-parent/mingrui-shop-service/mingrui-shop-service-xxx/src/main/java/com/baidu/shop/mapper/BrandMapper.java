package com.baidu.shop.mapper;

import com.baidu.shop.entity.BrandEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<BrandEntity>, SelectByIdListMapper<BrandEntity, Integer> {
    //表联查
    @Select(value = "select * from tb_brand where id in ( select brand_id from tb_category_brand where category_id = #{cid})")
    List<BrandEntity> categoryBrandById(Integer cid);
}
