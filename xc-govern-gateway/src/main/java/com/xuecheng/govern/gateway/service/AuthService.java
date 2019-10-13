package com.xuecheng.govern.gateway.service;

import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: XCEdu->AuthService
 * @description: 身份校验service
 * @author: Bangser
 * @create: 2019-09-03 11:16
 **/
@Service
public class AuthService {

    @Autowired
    StringRedisTemplate redisTemplate;

    //从cookie中获取uid
    public String getTokenFromCookie(HttpServletRequest request) {
        Map<String, String> map = CookieUtil.readCookie(request, "uid");
        if(map!=null && StringUtils.isNotBlank(map.get("uid"))){
            return map.get("uid");
        }
        return null;
    }

    //从headers中获取jwt令牌
    public String getJwtFromHeaders(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        //jwt令牌以 "Bearer " 开头
        if(StringUtils.isNotBlank(authorization) && authorization.startsWith("Bearer ")){
            return authorization;
        }
        return null;
    }

    public long getJwtTimeFromRedis(String s) {
        Long expire = redisTemplate.getExpire(s, TimeUnit.SECONDS);
        return expire;
    }
}
