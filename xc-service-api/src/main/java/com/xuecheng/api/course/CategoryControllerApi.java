package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @program: XCEdu->CategoryControllerApi
 * @description: 课程分类Api
 * @author: Bangser
 * @create: 2019-08-09 13:29
 **/
@Api(value = "课程分类管理",description = "课程分类管理",tags = {"课程分类管理"})
public interface CategoryControllerApi {

    @ApiOperation("查询分类")
    CategoryNode findList();

}
