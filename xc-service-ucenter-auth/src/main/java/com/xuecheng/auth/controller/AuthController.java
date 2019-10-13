package com.xuecheng.auth.controller;

import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.config.AuthConfig;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @program: XCEdu->AuthController
 * @description: 用户登录管理
 * @author: Bangser
 * @create: 2019-08-31 20:54
 **/
@RestController
public class AuthController implements AuthControllerApi {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthConfig authConfig;

    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginRequest loginRequest, HttpServletResponse response) {
        //申请令牌
        AuthToken token = authService.login(authConfig.getClientId(), authConfig.getClientSecret(), loginRequest.getUsername(), loginRequest.getPassword());
        //存入cookie
        CookieUtil.addCookie(response,authConfig.getCookieDomain(),"/","uid",token.getAccess_token(),authConfig.getCookieMaxAge(),false);
        return new LoginResult(CommonCode.SUCCESS,token.getAccess_token());
    }

    @Override
    @PostMapping("/userlogout")
    public ResponseResult logout(HttpServletRequest request,HttpServletResponse response) {
        //请求servic清除redis中的令牌信息
        Map<String, String> map = CookieUtil.readCookie(request, "uid");
        if(map!=null && StringUtils.isNotBlank(map.get("uid"))){
            authService.logout(map.get("uid"));
        }
        //清除cookie中的用户信息
        CookieUtil.addCookie(response,authConfig.getCookieDomain(),"/","uid",null,0,false);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Override
    @GetMapping("/userjwt")
    public JwtResult userjwt(HttpServletRequest request) {
        Map<String, String> map = CookieUtil.readCookie(request, "uid");
        if(map!=null && StringUtils.isNotBlank(map.get("uid"))){
            String uid = map.get("uid");
            AuthToken userjwt = authService.userjwt(uid);
            if(userjwt!=null){
                return new JwtResult(CommonCode.SUCCESS,userjwt.getJwt_token());
            }
            return new JwtResult(CommonCode.FAIL,null);
        }
        return null;
    }
}
