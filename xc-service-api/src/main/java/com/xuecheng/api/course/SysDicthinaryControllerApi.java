package com.xuecheng.api.course;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @program: XCEdu->SysDicthinaryControllerApi
 * @description: 数据字典Api
 * @author: Bangser
 * @create: 2019-08-09 16:19
 **/
@Api(value = "数据字典接口",description = "提供数据字典接口的管理、查询功能")
public interface SysDicthinaryControllerApi {

    @ApiOperation(value="数据字典查询接口")
    SysDictionary getByType(String type);
}
