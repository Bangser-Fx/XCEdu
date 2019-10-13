package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: XCEdu->LoginFilterTest
 * @description: 测试过滤器（验证头信息中是否包含 Authorization，有则转发到微服务，否则不放行）
 * @author: Bangser
 * @create: 2019-09-03 10:26
 **/
@Component
public class LoginFilterTest extends ZuulFilter {

    //过滤器的执行时机,四种类型：pre、routing、post、error
    @Override
    public String filterType() {
        return "pre";
    }

    //过滤器的优先权，越小越高
    @Override
    public int filterOrder() {
        return 0;
    }

    //过滤器是否执行(false表示不执行)
    @Override
    public boolean shouldFilter() {
        return false;
    }

    //过滤器的执行逻辑方法
    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        //取出头信息
        String authorization = request.getHeader("Authorization");
        //不包含Authorization
        if(StringUtils.isBlank(authorization)){
            requestContext.setSendZuulResponse(false);//拒绝访问
            requestContext.setResponseStatusCode(200);//设置响应码
            //设置相应体
            ResponseResult result = new ResponseResult(CommonCode.UNAUTHORISE);
            String jsonString = JSON.toJSONString(result);
            requestContext.setResponseBody(jsonString);
            requestContext.getResponse().setContentType("application/json;charset=UTF-8");
            return null;
        }
        return null;
    }
}
