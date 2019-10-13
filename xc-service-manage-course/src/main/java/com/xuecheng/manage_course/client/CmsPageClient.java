package com.xuecheng.manage_course.client;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @program: XCEdu->CmsPageClient
 * @description: Cmspage远程调用接口
 * @author: Bangser
 * @create: 2019-08-14 17:56
 **/
@FeignClient(value = "XC-SERVICE-MANAGE-CMS")
public interface CmsPageClient {

    @GetMapping("/cms/page/get/{pageId}")
    CmsPage findById(@PathVariable("pageId") String pageId);

    //保存页面
    @PostMapping("/cms/page/save")
    CmsPageResult save(@RequestBody CmsPage cmsPage);

    //一键发布页面
    @PostMapping("/cms/page/postPageQuick")
    @ResponseBody
    CmsPostPageResult postPageQuick(CmsPage cmsPage);

}
