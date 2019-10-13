package com.xuecheng.api.auth;

import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: XCEdu->AuthControllerApi
 * @description: 用户登录管理接口
 * @author: Bangser
 * @create: 2019-08-31 20:53
 **/
@Api(value = "用户认证",description = "用户认证接口")
public interface AuthControllerApi {
    @ApiOperation("登录")
    LoginResult login(LoginRequest loginRequest, HttpServletResponse response);

    @ApiOperation("退出")
    ResponseResult logout(HttpServletRequest request,HttpServletResponse response);

    @ApiOperation("查询userjwt令牌")
    JwtResult userjwt(HttpServletRequest request);

}

