package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.ResponseResult;

/**
 * @program: XCEdu->FreemarkerService
 * @description: 页面静态化与页面发布
 * @author: Bangser
 * @create: 2019-08-01 13:55
 **/
public interface PagePostService {

    /**
     * 根据页面id获取Html数据(以字符串形式返回)
     *
     * @params: [pageId]
     * @return: java.lang.String
     */
    String getHtmlById(String pageId);

    /**
     * 发布页面（静态化页面保存到GridFs中，并通知服务器CmsClient下载）
     *
     * @params: [pageId]
     * @return: com.xuecheng.framework.model.response.ResponseResult
     */
    ResponseResult postPage(String pageId);

    CmsPostPageResult postPageQuick(CmsPage cmsPage);
}
