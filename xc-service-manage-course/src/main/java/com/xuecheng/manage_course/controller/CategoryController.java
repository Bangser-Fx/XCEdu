package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CategoryControllerApi;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: XCEdu->CategoryController
 * @description: 课程分类Controller
 * @author: Bangser
 * @create: 2019-08-09 15:59
 **/
@RestController
public class CategoryController implements CategoryControllerApi {

    @Autowired
    private CourseService courseService;

    @Override
    @GetMapping("/category/list")
    public CategoryNode findList() {
        return courseService.findCategoryList();
    }
}
