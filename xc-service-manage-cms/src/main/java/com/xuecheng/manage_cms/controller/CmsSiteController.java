package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsSiteControllerApi;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_cms.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: XCEdu->CmsSiteController
 * @description: 站点cms_site信息的增删改查
 * @author: Bangser
 * @create: 2019-07-30 10:25
 **/
@RestController
@RequestMapping("/cms/site")
public class CmsSiteController implements CmsSiteControllerApi {

    @Autowired
    private SiteService siteService;

    @Override
    @GetMapping("/findAll")
    public QueryResponseResult findAll() {
        return siteService.findAll();
    }
}
