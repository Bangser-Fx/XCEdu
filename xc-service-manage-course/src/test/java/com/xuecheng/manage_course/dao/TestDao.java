package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.system.SysDictionary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDao {
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    TeachplanRepository teachplanRepository;
    @Autowired
    SysDictionaryRepository sysDictionaryRepository;

    @Test
    public void testCourseBaseRepository(){
        Optional<CourseBase> optional = courseBaseRepository.findById("402885816240d276016240f7e5000002");
        if(optional.isPresent()){
            CourseBase courseBase = optional.get();
            System.out.println(courseBase);
        }

    }

    @Test
    public void testCourseMapper(){
        PageHelper.startPage(1,5);
        CourseListRequest request = new CourseListRequest();
        request.setCompanyId("2");
        Page<CourseInfo> pageList = courseMapper.findCourseInfoPageList(request);
        List<CourseInfo> result = pageList.getResult();
        long total = pageList.getTotal();
        int pages = pageList.getPages();
        System.out.println(result);
    }

    @Test
    public void categoryTest() {
        CategoryNode categoryList = courseMapper.findCategoryList();
        System.out.println(categoryList);
    }

    @Test
    public void sysDictionaryTest() {
        SysDictionary type = sysDictionaryRepository.findByDType("200");
        System.out.println(type);
    }
}
