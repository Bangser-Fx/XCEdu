package com.xuecheng.api.learning;

import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @program: XCEdu->CourseLearningControllerApi
 * @description: 录播课程学习管理接口
 * @author: Bangser
 * @create: 2019-08-25 21:45
 **/
@Api(value = "录播课程学习管理",description = "录播课程学习管理")
public interface CourseLearningControllerApi {
    @ApiOperation("获取课程学习地址")
    GetMediaResult getmedia(String courseId, String teachplanId);
}
