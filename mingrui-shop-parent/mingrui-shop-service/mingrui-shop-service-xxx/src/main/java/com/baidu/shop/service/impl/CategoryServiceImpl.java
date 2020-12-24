package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.service.CategoryService;
import com.baidu.shop.utils.ObjectUtil;
import com.google.gson.JsonObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Transactional
    @Override
    public Result<JsonObject> UpdateCategoryById(CategoryEntity categoryEntity) {
        categoryMapper.updateByPrimaryKeySelective(categoryEntity);
        return this.setResultSuccess();
    }

    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setParentId(pid);
        List<CategoryEntity> list = categoryMapper.select(categoryEntity);
        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<JsonObject> deleteCategoryById(Integer id) {
        //查询id是否合法
        if(ObjectUtil.isNull(id) || id <= 0 ) return this.setResultError("id不合法");

        //查询当前节点是否存在
        CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(id);
        if(ObjectUtil.isNull(categoryEntity)) return this.setResultError("数据不存在");

        //判断当前节点是否为父节点
        if (categoryEntity.getParentId() == 1 ) return this.setResultError("当前节点为父节点");

        //对数据进行and拼接sql语句  select * from 表名 where 1=1 and parentId = ?
        Example example = new Example(CategoryEntity.class);
        example.createCriteria().andEqualTo("parentId",categoryEntity.getParentId());
        List<CategoryEntity> exampleList = categoryMapper.selectByExample(example);

        if(exampleList.size() <= 1){
            CategoryEntity categoryEntity1 = new CategoryEntity();
            categoryEntity1.setParentId(0);
            categoryEntity1.setId(categoryEntity1.getParentId());

            categoryMapper.updateByPrimaryKeySelective(categoryEntity1);
        }
        categoryMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }
}
