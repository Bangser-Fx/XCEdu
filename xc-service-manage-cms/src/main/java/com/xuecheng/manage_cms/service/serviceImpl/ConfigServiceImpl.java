package com.xuecheng.manage_cms.service.serviceImpl;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @program: XCEdu->ConfigServiceImpl
 * @description:
 * @author: Bangser
 * @create: 2019-07-31 12:58
 **/
@Service("configService")
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private CmsConfigRepository repository;

    @Override
    public CmsConfig getById(String id) {
        Optional<CmsConfig> optional = repository.findById(id);
        if(optional.isPresent()){
            CmsConfig cmsConfig = optional.get();
            return cmsConfig;
        }else {
            ExceptionCast.throwCustomException(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        return null;
    }
}
