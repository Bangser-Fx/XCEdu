package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: XCEdu->LoginFilter
 * @description: 登录校验过滤器
 * @author: Bangser
 * @create: 2019-09-03 11:18
 **/
@Component
public class LoginFilter extends ZuulFilter {

    @Autowired
    AuthService authService;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //获取上下文对象
        RequestContext requestContext = RequestContext.getCurrentContext();
        //从cookie中获取uid（jwt身份令牌）
        String token = authService.getTokenFromCookie(requestContext.getRequest());
        if(StringUtils.isBlank(token)){
            //拒绝访问
            refuseAccess(requestContext);
            return null;
        }
        //查看redis中令牌是否过期
        long time = authService.getJwtTimeFromRedis("user_token:"+token);
        if(time<=0){
            refuseAccess(requestContext);
            return null;
        }
        //从headers中获取jwt令牌
        String jwt = authService.getJwtFromHeaders(requestContext.getRequest());
        if(StringUtils.isBlank(jwt)){
            refuseAccess(requestContext);
            return null;
        }
        return null;
    }

    //拒绝访问
    private void refuseAccess(RequestContext requestContext){
        requestContext.setSendZuulResponse(false);//拒绝访问
        requestContext.setResponseStatusCode(200);//设置响应码
        //设置相应体
        ResponseResult result = new ResponseResult(CommonCode.UNAUTHORISE);
        String jsonString = JSON.toJSONString(result);
        requestContext.setResponseBody(jsonString);
        requestContext.getResponse().setContentType("application/json;charset=UTF-8");
    }
}
