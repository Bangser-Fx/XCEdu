package com.xuecheng.ucenter;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @program: XCEdu->XcMenuMapperTest
 * @description: XcMenuMapper接口测试
 * @author: Bangser
 * @create: 2019-09-04 11:19
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class XcMenuMapperTest {

    @Autowired
    private XcMenuMapper xcMenuMapper;

    @Test
    public void testFindList() {
        List<XcMenu> menuList = xcMenuMapper.findMenuForUser("49");
        System.out.println(menuList);
    }
}
