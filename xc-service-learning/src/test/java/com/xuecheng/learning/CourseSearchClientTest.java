package com.xuecheng.learning;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.learning.client.CourseSearchClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: XCEdu->CourseSearchClientTest
 * @description: 测试远程调用搜索服务
 * @author: Bangser
 * @create: 2019-08-29 21:24
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class CourseSearchClientTest {

    @Autowired
    private CourseSearchClient courseSearchClient;

    @Test
    public void test1() {
        TeachplanMediaPub pub = courseSearchClient.getmedia("40288581632b593e01632bd53ff10001");
        System.out.println(pub);
    }
}
