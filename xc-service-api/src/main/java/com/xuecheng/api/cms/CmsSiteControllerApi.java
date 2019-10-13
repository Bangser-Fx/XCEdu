package com.xuecheng.api.cms;

import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @program: XCEdu->CmsSiteControllerApi
 * @description: 站点信息的增删改查
 * @author: Bangser
 * @create: 2019-07-30 10:23
 **/
@Api(value = "cms站点管理接口", description = "cms站点管理接口，提供页面的增、删、改、查")
public interface CmsSiteControllerApi {

    @ApiOperation("查找所有站点信息")
    QueryResponseResult findAll();
}
