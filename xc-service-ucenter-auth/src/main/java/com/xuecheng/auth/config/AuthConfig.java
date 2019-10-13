package com.xuecheng.auth.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @program: XCEdu->AuthConfig
 * @description: Yml相关配置参数
 * @author: Bangser
 * @create: 2019-08-31 20:55
 **/
@Component
@ConfigurationProperties(prefix = "auth")
@Data
@ToString
public class AuthConfig {
    private Integer tokenValiditySeconds;//令牌在redis的过期时间
    private String clientId;   //客户端id
    private String clientSecret;    //客户端密码
    private String cookieDomain;    //验证通过后跳转的域名
    private Integer cookieMaxAge;   //cookie的存活时间（-1，当浏览器关闭则消除）
}
