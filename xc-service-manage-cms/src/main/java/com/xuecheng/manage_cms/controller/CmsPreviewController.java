package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPreviewControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PagePostService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * @program: XCEdu->CmsPreviewControll
 * @description: 页面预览
 * @author: Bangser
 * @create: 2019-08-01 15:20
 **/
@Controller
public class CmsPreviewController extends BaseController implements CmsPreviewControllerApi {

    @Autowired
    private PagePostService pagePostService;

    @Override
    @GetMapping("/cms/preview/{pageId}")
    public void getHtmlByPageId(@PathVariable("pageId") String pageId) {
        String html = pagePostService.getHtmlById(pageId);
        if(StringUtils.isNotBlank(html)){
            try {
                ServletOutputStream outputStream = response.getOutputStream();
                response.setHeader("Content-type","text/html;charset=utf-8");
                outputStream.write(html.getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @PostMapping("/cms/postPage/{pageId}")
    @ResponseBody
    public ResponseResult post(@PathVariable("pageId") String pageId) {
        ResponseResult result = pagePostService.postPage(pageId);
        return result;
    }


    @Override
    @PostMapping("/cms/page/postPageQuick")
    @ResponseBody
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage) {
        CmsPostPageResult cmsPostPageResult = pagePostService.postPageQuick(cmsPage);
        return cmsPostPageResult;
    }

}
