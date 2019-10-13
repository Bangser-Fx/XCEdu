package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsConfigControllerApi;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: XCEdu->CmsConfigController
 * @description: cms配置管理接口，提供数据模型的管理、查询接口
 * @author: Bangser
 * @create: 2019-07-31 13:00
 */
@RestController
@RequestMapping("/cms/config")
public class CmsConfigController implements CmsConfigControllerApi {

    @Autowired
    private ConfigService configService;


    @GetMapping("/get/{id}")
    @Override
    public CmsConfig getById(@PathVariable("id") String id) {
        return configService.getById(id);
    }
}
