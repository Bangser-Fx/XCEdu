package com.xuecheng.freemarker_test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @program: XCEdu->BannerController
 * @description: 轮播图静态化测试
 * @author: Bangser
 * @create: 2019-07-31 20:47
 **/
@Controller
@RequestMapping("/freemarker")
public class BannerController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/banner")
    public String toBannerHtml(Map<String,Object> map){
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/get/5a791725dd573c3574ee333f", Map.class);
        Map data = forEntity.getBody();
        map.putAll(data);
        return "index_banner";
    }
}
