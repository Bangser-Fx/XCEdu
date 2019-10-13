package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @program: XCEdu->CmsTemplateRepository
 * @description: 操作MongoDB,查询 cms_template
 * @author: Bangser
 * @create: 2019-08-01 14:10
 **/
public interface CmsTemplateRepository extends MongoRepository<CmsTemplate,String> {
}
