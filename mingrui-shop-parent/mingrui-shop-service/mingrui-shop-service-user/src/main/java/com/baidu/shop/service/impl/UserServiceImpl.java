package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.constant.MrConstens;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserMapper;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.UserService;
import com.baidu.shop.utils.BCryptUtil;
import com.baidu.shop.utils.BaiduBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
public class UserServiceImpl extends BaseApiService implements UserService {
    @Resource
    private UserMapper userMapper;

    @Autowired
    private RedisRepository redisRepository;

    @Override
    public Result<List<UserEntity>> checkCode(String phone, String code) {
        String radisCode = redisRepository.get(MrConstens.REDIS_DUANXIN_CODE_PRE + phone);
        if (code.equals(radisCode)) return this.setResultSuccess();

        return setResultError("验证码错误");
    }

    @Override
    public Result<JSONObject> sendValidCode(UserDTO userDTO) {
        //生成随机数
        String code = (int) ((Math.random() + 9 + 1) * 10000) + "";
        //短信验证
        //LuosimaoDuanxinUtil.SendCode(userDTO.getPhone(), code);
        //语音验证
        //LuosimaoDuanxinUtil.sendSpeak(userDTO.getPhone(), code);
        log.info("手机号码为 : {} , 验证为 : {}", userDTO.getPhone(), code);
        redisRepository.set("valid-code-" + userDTO.getPhone(), code);
        redisRepository.expire("valid-code-" + userDTO.getPhone(), 60L);
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
