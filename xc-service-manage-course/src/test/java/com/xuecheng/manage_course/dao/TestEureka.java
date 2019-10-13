package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.config.CmsPageConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestEureka {

    @Autowired
    private CmsPageClient cmsPageClient;

    @Autowired
    private CmsPageConfig cmsPageConfig;

    @Autowired
    private CourseBaseRepository courseBaseRepository;

    @Test
    public void testEureka(){
        CmsPage cmsPage = cmsPageClient.findById("5a754adf6abb500ad05688d9");
        System.out.println(cmsPage);
    }

    @Test
    public void testEureka2() {
        CmsPage page = creatCmsPage("402885816240d276016240f7e5000002");
        CmsPostPageResult pageResult = cmsPageClient.postPageQuick(page);
        System.out.println(pageResult);
    }

    //创建CmsPage提供给Cms服务创建页面
    private CmsPage creatCmsPage(String id){
        //获取页面基本信息
        Optional<CourseBase> optional = courseBaseRepository.findById(id);
        if(!optional.isPresent()){
            ExceptionCast.throwCustomException(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        CourseBase courseBase = optional.get();
        //创建CmsPage对象
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(cmsPageConfig.getSiteId());//站点id
        cmsPage.setPageName(id+".html");//页面名称
        cmsPage.setPageAliase(courseBase.getName());//页面别名
        cmsPage.setTemplateId(cmsPageConfig.getTemplateId());//模板id
        cmsPage.setPageWebPath(cmsPageConfig.getPageWebPath());//页面访问路径
        cmsPage.setPagePhysicalPath(cmsPageConfig.getPagePhysicalPath());//页面存储路径
        cmsPage.setDataUrl(cmsPageConfig.getDataUrlPre()+id);//数据url
        return cmsPage;
    }

}
