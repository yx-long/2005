package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserMapper;
import com.baidu.shop.service.UserService;
import com.baidu.shop.utils.BCryptUtil;
import com.baidu.shop.utils.BaiduBeanUtil;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RestController
public class UserServiceImpl extends BaseApiService implements UserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public Result<JSONObject> sendValidCode(UserDTO userDTO) {
        //生成随机数
        String psw = (int) ((Math.random() + 9 + 1) * 10000) + "";

        System.err.println(psw);
        return this.setResultSuccess();
    }

    //用户注册密码加密加盐
    @Override
    public Result<JSONObject> register(UserDTO userDTO) {
        UserEntity userEntity = BaiduBeanUtil.copyProperties(userDTO, UserEntity.class);
        userEntity.setPassword(BCryptUtil.hashpw(userEntity.getPassword(), BCryptUtil.gensalt()));
        userEntity.setCreated(new Date());
        userMapper.insertSelective(userEntity);
        return this.setResultSuccess();
    }

    //验证账号和手机号是否存在
    @Override
    public Result<List<UserEntity>> checkUserNameOrPhone(String value, Integer type) {
        Example example = new Example(UserEntity.class);
        if (type != null && type == 1) {
            example.createCriteria().andEqualTo("username", value);
        } else {
            example.createCriteria().andEqualTo("phone", value);
        }
        List<UserEntity> userEntities = userMapper.selectByExample(example);
        return setResultSuccess(userEntities);
    }
}
