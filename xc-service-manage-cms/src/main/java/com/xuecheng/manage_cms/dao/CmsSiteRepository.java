package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @program: XCEdu->CmsSiteController
 * @description:
 *
 * @author: Bangser
 * @create: 2019-07-30 10:11
 **/
public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {
}
