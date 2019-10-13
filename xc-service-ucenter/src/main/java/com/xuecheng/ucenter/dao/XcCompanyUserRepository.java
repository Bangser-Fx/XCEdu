package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @program: XCEdu->XcCompanyUserRepository
 * @description: xc_company_user表的jpa查询接口
 * @author: Bangser
 * @create: 2019-09-02 17:59
 **/
public interface XcCompanyUserRepository extends JpaRepository<XcCompanyUser,String> {

    XcCompanyUser findByUserId(String userId);
}
