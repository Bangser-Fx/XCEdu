package com.xuecheng.manage_cms_client.dao;


import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
* @description: 操作MongoDB,查询 cms_page
* @author: Bangser
* @date: 2019/7/22 20:48
*/
public interface CmsPageRepository extends MongoRepository<CmsPage,String> {

    CmsPage findByPageName(String pageName);

    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName, String siteId, String pageWebPath);
}
