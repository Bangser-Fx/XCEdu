package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @program: XCEdu->CmsConfigRepository
 * @description: 操作mongodb, 对Cmsconfig增删改查
 * @author: Bangser
 * @create: 2019-07-31 12:55
 **/
public interface CmsConfigRepository extends MongoRepository<CmsConfig,String> {
}
