package com.mr.test;

import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTokenTest {

    //公钥位置
    private static final String pubKeyPath = "D:\\rea.pub";
    //私钥位置
    private static final String priKeyPath = "D:\\rea.pri";
    //公钥对象
    private PublicKey publicKey;
    //私钥对象
    private PrivateKey privateKey;

    /**
     * 生成公钥私钥 根据密文
     *
     * @throws Exception
     */
    @Test
    public void genRsaKey() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "mingrui");
    }

    /**
     * 从文件中读取公钥私钥
     *
     * @throws Exception
     */
    @Before
    public void getKeyByRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    /**
     * 根据用户信息结合私钥生成token
     *
     * @throws Exception
     */
    @Test
    public void genToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(1, "mingrui"), privateKey, 2);
        System.out.println("user-token = " + token);
    }

    /**
     * 结合公钥解析token
     *
     * @throws Exception
     */
    @Test
    public void parseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJtaW5ncnVpIiwiZXhwIjoxNjE1NTM1MDAyfQ.cKCFvgwRWoXFkHvhT5giCjJoMN2Z-q473_7LGf0pBNB-d0yk4p0p8w9CzGirjkjtD0_RBv7_50vppd0H_5bO0gBj0pWx6Ku_gSFM8ZuJhXB9Muq93w8aCvN3Ph_zr7nXYCKZh4g4oMVYn4_B90qVTqJ6PZ0Aznnp93NUJm8-GKQ";

        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
