package com.xuecheng.learning.client;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @program: XCEdu->CourseSearchClient
 * @description: 远程调用搜索服务
 * @author: Bangser
 * @create: 2019-08-25 21:47
 **/
@FeignClient(value = "XC-SERVICE-SEARCH")
public interface CourseSearchClient {

    //根据课程计划id获取TeachplanMediaPub
    @GetMapping(value="/search/course/getmedia/{teachplanId}")
    TeachplanMediaPub getmedia(@PathVariable("teachplanId") String teachplanId);
}
