package com.baidu.shop.business;

import com.baidu.shop.dto.PayInfoDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Api(tags = "支付接口")
public interface PayService {

    //请求支付的接口
    @GetMapping(value = "pay/requestPay")
    void requestPay(PayInfoDTO payInfoDTO, @CookieValue(value = "MRSHOP_TOKEN") String token, HttpServletResponse response);
    //通知的接口，这个接口调不到 这个接口必须得暴漏在公网上
    @GetMapping(value = "pay/notify")
    void notify(HttpServletRequest request);
    //支付业务最复杂的接口
    //可以通知任何
    //现在支付是什么状态
    //支付是否成功
    //支付订单是否刷新过
    //支付是否延迟

    //正常返回接口
    @GetMapping(value = "pay/returnUrl")
    void returnUrl(HttpServletRequest request , HttpServletResponse response);
}
