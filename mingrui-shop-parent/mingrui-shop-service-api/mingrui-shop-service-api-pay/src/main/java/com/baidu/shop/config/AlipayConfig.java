package com.baidu.shop.config;

import java.io.FileWriter;
import java.io.IOException;

public class AlipayConfig {

    // ↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2021000117622307";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC7frxyXhjsmXbEwfjjLQWbWGBFEpa+a5A+cNvAVcPpdUNk5omTVord5zqWwq3mQFVZ7E18dUM5zjxa70S2ReQW7tNTAIS56wGlE4ZUDr5bNBrK88VhKZAqmnS1oYn9a3+JGGsqlwEGdFYod+tXJy9XkiyGHnWZnopSl/wmDpQV7pr8c29AUfEd7wF9mPT2/+tzWQH8Vj9topHJc/hKwxqYIODjPtE0aWD/M4cXs+9WJoRHSszT/hUjKGnglR/E9nM1JSyWNue7+fBBT0nP/rX87dPb91rGJFNSAKQ26ZQJ4pGFaSX4IHKAYPKUK/K/+Lhxea3X+ev5qsM4WqsgTCl7AgMBAAECggEBAI3uq6L7/mwk5BagK9N1QnhNV1sSblCpxmCuOHwJsKuq3CCmr+B8M9U1dFGp/9B8ROA0lJexgPliWKgR1ncQw1Bk7E455z+CWhVniVlaa9lIwTt/e5xIxVyq3cydA4N2e+4BMSbldp43NP2fgeTQwfLNsay5pBZtQJKzgOoiQRGz6MBuCbY15FrFsmTMN+WH0LNuar+ycgLdxw+rOb7dN4PkKkldrjivGn4s6OA3a5dbh9YOw+3+Pw/AJDJMVfIfwPxHK32RN+k0BmNpP8UH75AlBWuEHwY0MS8AUjyauWHVTRm/3AdniQPjUTk7+7cs4uzUnmiDRSguKVgP9vmG/rECgYEA69KBANo6/P3pBIwFXk7adypKsIxVWOXZURcLb/AvlMdZnKvl8qF5bB1vvIAJYfH0TsYDbJ++1VMRKYILDGWkCTtFP4peVEZjBY9bxLMwhwtkbr90S4wEzh9GxTddHcLMThHPOuCgVHk5zcvIl/XbUVey73gAqSyA0B0KDN5jN+MCgYEAy4mp46GAHqAzfpmfs3EAVguvinDpI7kVBP8zPIuvgRVH7bq605yoSVDgS7bRBa5CUrlPSqF54437r6vfKPm7A8TWgogqbc8JK2/t1nSjRL6k8n3PEiAYH7Jz2hIYLZ61N4qsJnfeUZn2Zn6PkntuCjGXv7yLDrJ1XhyKm5Opi4kCgYAykv+VljfGYBHkJr9T/Wc+3TQNbtsy6IBVsrKCPCmIJ8ShJAB5q4Wl2R5Id9ELT1+MsUy0tAGyMFFi7H/AFeA7FpoASYV3X20HGTqeEavbmmbclXS0av/w0KZVz0zKACfeqlUeFGRuGV17NDP92v1BEaTPVJYXsfKiL0gjgGIY7wKBgQCqxQPfUr818t+Bls8hNOw9RWyw0O8q47y5Bo0sfV5xBTPmR8c8iLHBYbPukqJpC73lvkjCqxyjSZsj1yZTMMQx81KvDiPbIdvlr/Fy/0WFBhdKxTeZU4gqAAWnr4rNq90LjrPFSski2fScJg+7rQe0UcuqOTzKxfwnS/cXdRrJUQKBgQCGvEi9GmCHQOwQfyO71NSLvG7US7MWk8oK0OQt+K2sFcvlj0EtYXYW9KeAw9yn9Jl9rg+/a8unIHZ7xgnb0gqhWh3Jq3PZTrOrmRm0gCSDS6DVBxYOPkrKwZFIl68iBaD6IDsLEU6DTxarNyG1Sb+wsAUIrfoNxInI5QYZiPwTOQ==";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhHUufH7gTgpJEh4MiNat7eMUANqzElzeqqR03ZImORnGknSWxnMxF/djA2ljYm/iZMMp7T3tqDbkkAZtqwfXJBP2L9X80lW1AbMKBYtvGXKYb8izvlnN4vmy0s9K7RxKiMJJj0e3LaA72saD56JDNNnXgD9vE6ZnLSAfWyrbch2iMIIyf98UjWi5PDiflzJ/lmVGkXHdwTfPAMiyp5ARpsH6JJvpzlOAODPBXMyvpCEJvSin0MdeL9YB8Yvc3kXOBw2g8ZCgIReR0V6Vov52xGhrFNJIpTmXAEe+kjj0cj5YKk1wf6ry5wMfVnMeXw2ZeXiStXN601I1gyIryWa+PwIDAQAB";

    // 服务器异步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8900/pay/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8900/pay/returnUrl";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "D:\\";

    // ↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     *
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis() + ".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
