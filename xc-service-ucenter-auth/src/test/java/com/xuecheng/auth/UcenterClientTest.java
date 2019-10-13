package com.xuecheng.auth;

import com.xuecheng.auth.client.UcenterClient;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: XCEdu->UcenterClientTest
 * @description: 测试远程调用接口
 * @author: Bangser
 * @create: 2019-09-02 18:42
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class UcenterClientTest {

    @Autowired
    private UcenterClient ucenterClient;

    @Test
    public void testClient() {
        XcUserExt ext = ucenterClient.getUserext("itcast");
        System.out.println(ext);
    }
}
