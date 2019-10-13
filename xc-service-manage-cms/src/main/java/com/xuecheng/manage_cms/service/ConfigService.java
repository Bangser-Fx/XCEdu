package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;

/**
 * @program: XCEdu->ConfigService
 * @description: CmsconfigService
 * @author: Bangser
 * @create: 2019-07-31 12:57
 **/
public interface ConfigService {

    CmsConfig getById(String id);
}
