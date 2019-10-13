package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

/**
 * @program: XCEdu
 * @description: Cms分页查询
 * @author: Bangser
 * @create: 2019-07-22 20:44
 **/
public interface PageService {

    /**
    * 分页，条件 查询
    * @params: [page, size, queryPageRequest]
    * @return: QueryResponseResult
    */
    QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    /**
    * 通过id查找页面
    * @params: [pageId]
    * @return: com.xuecheng.framework.domain.cms.CmsPage
    */
    CmsPage findById(String pageId);

    /**
    * 添加页面
    * @params: [cmsPage]
    * @return: com.xuecheng.framework.domain.cms.response.CmsPageResult
    */
    CmsPageResult addCmsPage(CmsPage cmsPage);

    /**
    * 更新页面
    * @params: [pageId, cmsPage]
    * @return: com.xuecheng.framework.domain.cms.response.CmsPageResult
    */
    CmsPageResult updateCmsPage(String pageId, CmsPage cmsPage);

   /**
   * 删除页面
   * @params: [pageId]
   * @return: com.xuecheng.framework.model.response.ResponseResult
   */
    ResponseResult deleteCmsPage(String pageId);

    CmsPageResult save(CmsPage cmsPage);
}
