package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @program: XCEdu
 * @description: cms页面管理接口，提供页面的增、删、改、查
 * @author: Bangser
 * @create: 2019-07-22 14:18
 **/
@Api(value = "cms页面管理接口", description = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {

    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="int")
    })
    QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    @ApiOperation("通过id朝招页面")
    CmsPage findById(String pageId);

    @ApiOperation("添加页面")
    CmsPageResult addCmsPage(CmsPage cmsPage);

    @ApiOperation("修改页面")
    CmsPageResult updateCmsPage(String pageId,CmsPage cmsPage);

    @ApiOperation("删除页面")
    ResponseResult deleteCmsPage(String pageId);

    @ApiOperation("保存页面")
    CmsPageResult save(CmsPage cmsPage);

}
