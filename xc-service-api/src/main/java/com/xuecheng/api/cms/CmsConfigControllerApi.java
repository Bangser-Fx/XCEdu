package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @program: XCEdu->CmsConfigControllerApi
 * @description: cms配置管理接口，提供数据模型的管理、查询接口
 * @author: Bangser
 * @create: 2019-07-31 12:52
 */
@Api(value="cms配置管理接口",description = "cms配置管理接口，提供数据模型的管理、查询接口")
public interface CmsConfigControllerApi {

    @ApiOperation("根据id查询CMS配置信息")
    CmsConfig getById(String id);
}
