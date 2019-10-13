package com.xuecheng.auth;

import com.xuecheng.auth.config.AuthConfig;
import com.xuecheng.framework.client.XcServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @program: XCEdu->SecurityTest
 * @description: 远程申请令牌
 * @author: Bangser
 * @create: 2019-08-31 21:19
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class SecurityTest {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthConfig authConfig;

    @Test
    public void testSecurity() {
        //获取eureka中认证服务实例
        ServiceInstance auth = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        //获取服务地址  http：//host:port
        URI uri = auth.getUri();
        //拼接申请令牌地址
        String authUrl = uri + "/auth/oauth/token";
        //定义请求体
        MultiValueMap<String,String> body = new LinkedMultiValueMap();
        body.add("grant_type","password");
        body.add("username","itcast");
        body.add("password","123");
        //定义请求头
        MultiValueMap<String,String> headers = new LinkedMultiValueMap();
        String basic = httpBasic(authConfig.getClientId(), authConfig.getClientSecret());
        headers.add("Authorization",basic);
        //创建httpEntity
        HttpEntity<Map> httpEntity = new HttpEntity<>(body,headers);
        //指定 restTemplate当遇到400或401响应时候也不要抛出异常，也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if(response.getRawStatusCode()!=400 && response.getRawStatusCode()!=401){
                    super.handleError(response);
                }
            }
        });
        //远程调用令牌
        ResponseEntity<Map> entity = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);
        Map map = entity.getBody();
        System.out.println(map);
    }

    //对客户端id与密码进行Base64编码
    private String httpBasic(String clientId,String clientSecret){
        String s = clientId+":"+clientSecret;
        byte[] encode = Base64Utils.encode(s.getBytes());
        return "Basic "+new String(encode);
    }
}
