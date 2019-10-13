package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: XCEdu
 * @description: CmsPageController
 * @author: Bangser
 * @create: 2019-07-22 14:36
 **/
@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {

    @Autowired
    private PageService pageService;

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageRequest queryPageRequest) {
        return pageService.findList(page, size, queryPageRequest);
    }

    @Override
    @GetMapping("/get/{pageId}")
    public CmsPage findById(@PathVariable("pageId") String pageId) {
        return pageService.findById(pageId);
    }

    @Override
    @PostMapping("/add")
    public CmsPageResult addCmsPage(@RequestBody CmsPage cmsPage) {
        return pageService.addCmsPage(cmsPage);
    }

    @Override
    @PutMapping("/update/{pageId}")
    public CmsPageResult updateCmsPage(@PathVariable("pageId")String pageId, @RequestBody CmsPage cmsPage) {
        return pageService.updateCmsPage(pageId,cmsPage);
    }

    @Override
    @DeleteMapping("/delete/{pageId}")
    public ResponseResult deleteCmsPage(@PathVariable("pageId") String pageId) {
        return pageService.deleteCmsPage(pageId);
    }

    @Override
    @PostMapping("/save")
    public CmsPageResult save(@RequestBody CmsPage cmsPage) {
        return pageService.save(cmsPage);
    }

}
