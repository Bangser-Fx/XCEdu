package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import io.swagger.annotations.Api;

/**
 * @program: XCEdu->UcenterControllerApi
 * @description: 用户中心Api
 * @author: Bangser
 * @create: 2019-09-02 17:37
 **/
@Api(value = "用户中心",description = "用户中心管理")
public interface UcenterControllerApi {
    XcUserExt getUserext(String username);
}

