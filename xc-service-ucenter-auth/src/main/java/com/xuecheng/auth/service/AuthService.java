package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.auth.config.AuthConfig;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: XCEdu->AuthService
 * @description: 登录认证service
 * @author: Bangser
 * @create: 2019-08-31 22:26
 **/
@Service
public class AuthService {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AuthConfig authConfig;

    /**
    * 登录操作
    * @params: [clientId, clientSecret, username, password]
    * @return: com.xuecheng.framework.domain.ucenter.ext.AuthToken
    */
    public AuthToken login(String clientId, String clientSecret, String username, String password) {
        AuthToken authToken = getAuthToken(clientId, clientSecret, username, password);
        if(authToken==null){
            ExceptionCast.throwCustomException(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }
        Boolean aBoolean = saveTokenToRedis(authToken);
        if(!aBoolean){
            ExceptionCast.throwCustomException(AuthCode.AUTH_SAVE_TOKEN_FAIL);
        }
        return authToken;
    }

    /**
    * 退出登录
    * @params: [uid]
    * @return: void
    */
    public void logout(String uid) {
        String key = "user_token:" + uid;
        redisTemplate.delete(key);
    }

    /**
    * 从redis中获取令牌
    * @param: [uid]
    * @return: com.xuecheng.framework.domain.ucenter.response.JwtResult
    */
    public AuthToken userjwt(String uid) {
        String key = "user_token:" + uid;
        String s = redisTemplate.opsForValue().get(key);
        AuthToken authToken = JSON.parseObject(s, AuthToken.class);
        return authToken;
    }

    //将令牌存储到redis
    private Boolean saveTokenToRedis(AuthToken authToken) {
        String key = "user_token:"+ authToken.getAccess_token();
        String val = JSON.toJSONString(authToken);
        redisTemplate.boundValueOps(key).set(val,authConfig.getTokenValiditySeconds(), TimeUnit.SECONDS);
        return redisTemplate.getExpire(key)>0;
    }

    //远程调用spring security的UserDetailsService，申请令牌
    private AuthToken getAuthToken(String clientId, String clientSecret, String username, String password){
        //获取eureka中认证服务实例
        ServiceInstance auth = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        //获取服务地址  http：//host:port
        URI uri = auth.getUri();
        //拼接申请令牌地址
        String authUrl = uri + "/auth/oauth/token";
        //定义请求体
        MultiValueMap<String,String> body = new LinkedMultiValueMap();
        body.add("grant_type","password");
        body.add("username",username);
        body.add("password",password);
        //定义请求头
        MultiValueMap<String,String> headers = new LinkedMultiValueMap();
        String basic = httpBasic(clientId, clientSecret);
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
        Map map = null;
        try {
            //开始远程调用,即调用UserDetailsServiceImpl.loadUserByUsername()
            ResponseEntity<Map> entity = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);
            map = entity.getBody();
        } catch (RestClientException e) {
            e.printStackTrace();
            ExceptionCast.throwCustomException(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }
        if(map == null || map.get("access_token") == null ||
                map.get("refresh_token") == null || map.get("jti") == null){
            //获取spring security返回的错误信息
            String error_description = (String) map.get("error_description");
            if(StringUtils.isNotEmpty(error_description)){
                if(error_description.equals("坏的凭证")){//账号或密码错误
                    ExceptionCast.throwCustomException(AuthCode.AUTH_CREDENTIAL_ERROR);
                }else if(error_description.indexOf("UserDetailsService returned null")>=0){//用户不存在
                    ExceptionCast.throwCustomException(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
                }
            }
            ExceptionCast.throwCustomException(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }
        AuthToken authToken = new AuthToken();
        //访问令牌(jwt)
        String jwt_token = (String) map.get("access_token");
        //刷新令牌(jwt)
        String refresh_token = (String) map.get("refresh_token");
        //jti，作为用户的身份标识
        String access_token = (String) map.get("jti");
        authToken.setJwt_token(jwt_token);
        authToken.setAccess_token(access_token);
        authToken.setRefresh_token(refresh_token);
        return authToken;
    }

    //对客户端id与密码进行Base64编码
    private String httpBasic(String clientId,String clientSecret){
        String s = clientId+":"+clientSecret;
        byte[] encode = Base64Utils.encode(s.getBytes());
        return "Basic "+new String(encode);
    }


}
