package com.xuecheng.auth.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @program: XCEdu->UcenterClient
 * @description: 远程调用用户中心服务
 * @author: Bangser
 * @create: 2019-09-02 18:37
 **/
@FeignClient(value = XcServiceList.XC_SERVICE_UCENTER)
public interface UcenterClient {

    @GetMapping("/ucenter/getuserext")
    XcUserExt getUserext(@RequestParam("username") String username);
}
