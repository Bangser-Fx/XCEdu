package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @program: XCEdu->XcMenuMapper
 * @description: 用户权限查询接口
 * @author: Bangser
 * @create: 2019-09-04 11:07
 **/
@Mapper
public interface XcMenuMapper {

    /**
    * 根据用户id查询用户的权限列表
    * @params: [uid]
    * @return: java.util.List<com.xuecheng.framework.domain.ucenter.XcMenu>
    */
    List<XcMenu> findMenuForUser(String uid);
}
