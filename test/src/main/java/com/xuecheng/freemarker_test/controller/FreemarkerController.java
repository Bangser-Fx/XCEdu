package com.xuecheng.freemarker_test.controller;

import com.xuecheng.freemarker_test.model.Student;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * @program: XCEdu->FreemarkerController
 * @description: FreeMarker模板测试Controller
 * @author: Bangser
 * @create: 2019-07-31 17:30
 **/
@RequestMapping("/freemarker")
@Controller
public class FreemarkerController {

    @GetMapping("/test")
    public String testFreemarker(Map<String, Object> map) {
        map.put("name", "方兴");
        //学生小明
        Student stu1 = new Student();
        stu1.setName("小明");
        stu1.setAge(18);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());
        //学生小红
        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMoney(200.1f);
        stu2.setAge(19);
        stu2.setBirthday(new Date());
        //小红的朋友列表中有小明(最好的朋友)
        List<Student> friends = new ArrayList<>();
        friends.add(stu1);
        stu2.setFriends(friends);
        stu2.setBestFriend(stu1);
        //学生列表(小明和小红)
        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);
        //向数据模型放数据
        map.put("stus", stus);
        //准备map数据
        HashMap<String, Student> stuMap = new HashMap<>();
        stuMap.put("stu1", stu1);
        stuMap.put("stu2", stu2);
        //向数据模型放数据
        map.put("stu1", stu1);
        //向数据模型放数据
        map.put("stuMap", stuMap);
        return "test01";
    }
}
