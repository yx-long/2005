package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Api(tags = "用户接口")
public interface UserService {

    @ApiOperation(value = "校👁验证码是否正确")
    @GetMapping(value = "user/checkCode")
    Result<List<UserEntity>> checkCode(String phone, String code);

    @ApiOperation(value = "用户注册")
    @PostMapping(value = "user/register")
    Result<JSONObject> register(@RequestBody UserDTO userDTO);

    @ApiOperation(value = "校验用户名或手机号唯一")
    @GetMapping(value = "user/check")
    Result<List<UserEntity>> checkUserNameOrPhone(String value, Integer type);

    @ApiOperation(value = "给手机号发送验证码")
    @PostMapping(value = "user/sendValidCode")
    Result<JSONObject> sendValidCode(@RequestBody UserDTO userDTO);

}
