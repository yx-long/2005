package com.baidu.shop.business.impl;

import com.baidu.shop.business.UserOauthService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserOauthMapper;
import com.baidu.shop.utils.BCryptUtil;
import com.baidu.shop.utils.JwtUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserOauthServiceImpl implements UserOauthService {

    @Resource
    private UserOauthMapper userOauthMapper;

    @Override
    public String login(UserEntity userEntity, JwtConfig jwtConfig) {
        String token = null;
        Example example = new Example(UserEntity.class);
        example.createCriteria().andEqualTo("username", userEntity.getUsername());
        List<UserEntity> userEntities = userOauthMapper.selectByExample(example);
        //等于1就是有值
        if (userEntities.size() == 1) {
            //有值后验证密码是否正确
            if (BCryptUtil.checkpw(userEntity.getPassword(), userEntities.get(0).getPassword())) {
                UserInfo userInfo = new UserInfo(userEntities.get(0).getId(), userEntities.get(0).getUsername());
                try {
                    token = JwtUtils.generateToken(userInfo, jwtConfig.getPrivateKey(), jwtConfig.getExpire());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return token;
    }
}
