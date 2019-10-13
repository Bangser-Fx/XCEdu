package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @program: XCEdu->CmsSiteControllerApi
 * @description: 页面预览与发布
 * @author: Bangser
 * @create: 2019-07-30 10:23
 **/
@Api(value = "页面预览", description = "页面预览")
public interface CmsPreviewControllerApi {

    @ApiOperation("页面预览")
    void getHtmlByPageId(String pageId);

    @ApiOperation("发布页面")
    ResponseResult post(String pageId);

    @ApiOperation("一键发布页面")
    CmsPostPageResult postPageQuick(CmsPage cmsPage);


}
