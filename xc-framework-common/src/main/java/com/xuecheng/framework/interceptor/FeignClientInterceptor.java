package com.xuecheng.framework.interceptor;

import com.xuecheng.framework.web.BaseController;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import java.util.Enumeration;

/**
 * @program: XCEdu->FeignClientInterceptor
 * @description: Feign拦截器, 微服务间调用传递头信息
 * @author: Bangser
 * @create: 2019-09-06 21:03
 **/
public class FeignClientInterceptor extends BaseController implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        try {
            //从BaseController可获取request对象，并从该对象中获取Headers信息
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    String values = request.getHeader(name);
                    if (name.equals("authorization")) {
                        //System.out.println("name="+name+"values="+values);
                        requestTemplate.header(name, values);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
