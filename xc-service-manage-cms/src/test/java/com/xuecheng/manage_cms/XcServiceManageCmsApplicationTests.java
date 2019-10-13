package com.xuecheng.manage_cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XcServiceManageCmsApplicationTests {

    @Autowired
    private CmsPageRepository repository;

    @Autowired
    private PageService pageService;

   @Test
   public void testFindAll() {
       List<CmsPage> cmsPages = repository.findAll();
       System.out.println(cmsPages);
   }

   @Test
   public void testFindPage() {
       Pageable pageable = PageRequest.of(0,10);
       Page<CmsPage> all = repository.findAll(pageable);
       System.out.println(all);
   }

   @Test
   public void testFindById() {
       CmsPage cmsPage = repository.findByPageId("5a754adf6abb500ad05688d9");
       System.out.println(cmsPage);
   }

   @Test
   public void testFindByPageName() {
       CmsPage cmsPage = repository.findByPageName("index_category.html");
       System.out.println(cmsPage);
   }

   @Test
   public void testService() {
       QueryResponseResult list = pageService.findList(1, 10, null);
       System.out.println(list);
   }

   @Test
   public void testFindByExample() {
       CmsPage cmsPage = new CmsPage();
       cmsPage.setPageAliase("课程");
       ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
       Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);
       Pageable pageable = PageRequest.of(0,10);
       Page<CmsPage> all = repository.findAll(example, pageable);
       List<CmsPage> content = all.getContent();
       System.out.println(content);
   }

   @Test
   public void findByPageNameAndSiteIdAndPageWebPath() {
       CmsPage cmsPage = repository.findByPageNameAndSiteIdAndPageWebPath("index.html", "5a751fab6abb5044e0d19ea1", "/index.html");
       System.out.println(cmsPage);
   }
}
