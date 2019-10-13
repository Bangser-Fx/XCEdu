package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: XCEdu->UcenterService
 * @description: 用户中心管理service
 * @author: Bangser
 * @create: 2019-09-02 18:01
 **/
@Service
public class UcenterService {

    @Autowired
    private XcUserRepository xcUserRepository;

    @Autowired
    private XcCompanyUserRepository xcCompanyUserRepository;

    @Autowired
    private XcMenuMapper xcMenuMapper;

    //根据用户名获取用户信息
    public XcUserExt getUserext(String username) {
        //查询用户基本信息
        XcUser xcUser = xcUserRepository.findByUsername(username);
        if(xcUser==null){
           return null;
        }
        //查询用户公司信息
        XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findByUserId(xcUser.getId());
        //查询用户权限
        List<XcMenu> xcMenus = xcMenuMapper.findMenuForUser(xcUser.getId());
        //构建XcUserExt对象
        XcUserExt ext = new XcUserExt();
        BeanUtils.copyProperties(xcUser,ext);
        if(xcCompanyUser!=null){
            ext.setCompanyId(xcCompanyUser.getCompanyId());
        }
        ext.setPermissions(xcMenus);
        return ext;
    }
}
