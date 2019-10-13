package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @program: XCEdu->MediaFileControllerApi
 * @description: 媒资管理接口
 * @author: Bangser
 * @create: 2019-08-24 17:00
 **/
@Api(value = "媒体文件管理",description = "媒体文件管理接口",tags = {"媒体文件管理接口"})
public interface MediaFileControllerApi {
    @ApiOperation("查询文件列表")
    QueryResponseResult findList(int page, int size, QueryMediaFileRequest
            queryMediaFileRequest) ;
}

