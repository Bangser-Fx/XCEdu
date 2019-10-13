package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @program: XCEdu->XcUserRepository
 * @description: xc_user表的jpa查询接口
 * @author: Bangser
 * @create: 2019-09-02 17:40
 **/
public interface XcUserRepository extends JpaRepository<XcUser,String> {

    XcUser findByUsername(String userName);
}
