package com.xuecheng.manage_cms.service.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitMqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import com.xuecheng.manage_cms.service.PagePostService;
import com.xuecheng.manage_cms.service.PageService;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @program: XCEdu->FreemarkerServiceImpl
 * @description:
 * @author: Bangser
 * @create: 2019-08-01 13:57
 **/
@Service("pagePostService")
public class PagePostServiceImpl implements PagePostService {

    @Autowired
    private CmsPageRepository pageRepository;

    @Autowired
    private CmsTemplateRepository templateRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GridFsTemplate fsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PageService pageService;

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    @Override
    public String getHtmlById(String pageId) {
        //获取数据
        Map dataMap = getDataMap(pageId);
        if (dataMap == null) {
            ExceptionCast.throwCustomException(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //获取模板
        String template = getTemplate(pageId);
        if (StringUtils.isBlank(template)) {
            ExceptionCast.throwCustomException(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //静态化
        String html = generateHtml(template, dataMap);
        if (StringUtils.isBlank(html)) {
            ExceptionCast.throwCustomException(CmsCode.CMS_COURSE_PERVIEWISNULL);
        }
        //返回静态化后的html页面字符串
        return html;
    }

    @Override
    public ResponseResult postPage(String pageId) {
        CmsPage cmsPage = getCmsPage(pageId);
        //保存静态化文件
        saveHtml(cmsPage);
        //发送消息
        sendPostMsg(cmsPage);
        ResponseResult result = new ResponseResult(CommonCode.SUCCESS);
        return result;
    }

    //一建发布页面（并返回页面的访问地址）
    @Override
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //保存页面
        CmsPageResult save = pageService.save(cmsPage);
        //获取保存后的页面
        CmsPage saveCmsPage = save.getCmsPage();
        //将页面发布
        ResponseResult result = this.postPage(saveCmsPage.getPageId());
        if(!result.isSuccess()){
            ExceptionCast.throwCustomException(CmsCode.CMS_POST_FAIL);
        }
        //得到页面对应的站点信息
        Optional<CmsSite> optional = cmsSiteRepository.findById(saveCmsPage.getSiteId());
        if(!optional.isPresent()){
            ExceptionCast.throwCustomException(CmsCode.CMS_SITE_NOTFIND);
        }
        CmsSite cmsSite = optional.get();
        //拼装页面返回URL
        //Url= cmsSite.siteDomain+cmsSite.siteWebPath+ cmsPage.pageWebPath + cmsPage.pageName
        String pageUrl = cmsSite.getSiteDomain() + cmsSite.getSiteWebPath() + saveCmsPage.getPageWebPath() +saveCmsPage.getPageName();
        return new CmsPostPageResult(CommonCode.SUCCESS,pageUrl);
    }

    //存储静态化文件(html)
    private void saveHtml(CmsPage cmsPage) {
        //获取静态化后的html数据
        String html = this.getHtmlById(cmsPage.getPageId());
        InputStream inputStream = null;
        try {
            inputStream = IOUtils.toInputStream(html, "utf-8");
            ObjectId objectId = fsTemplate.store(inputStream, cmsPage.getPageName());
            //删除以前的静态化文件
            if (StringUtils.isNotBlank(cmsPage.getHtmlFileId())) {
                fsTemplate.delete(Query.query(Criteria.where("_id").is(cmsPage.getHtmlFileId())));
            }
            //更新htmlFileId
            cmsPage.setHtmlFileId(objectId.toString());
            pageRepository.save(cmsPage);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //通知服务端已完成静态化
    private void sendPostMsg(CmsPage cmsPage) {
        //将消息转为json形式
        Map<String, String> map = new HashMap<>();
        map.put("pageId", cmsPage.getPageId());
        String msg = JSON.toJSONString(map);
        rabbitTemplate.convertAndSend(RabbitMqConfig.EX_ROUTING_CMS_POSTPAGE, cmsPage.getSiteId(), msg);
    }

    //静态化
    private String generateHtml(String templateStr, Map dataMap) {
        try {
            Configuration configuration = new Configuration(Configuration.getVersion());
            StringTemplateLoader templateLoader = new StringTemplateLoader();
            templateLoader.putTemplate("template", templateStr);
            configuration.setTemplateLoader(templateLoader);
            configuration.setDefaultEncoding("UTF-8");
            Template template = configuration.getTemplate("template");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, dataMap);
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取模板
    private String getTemplate(String pageId) {
        CmsPage cmsPage = getCmsPage(pageId);
        String templateId = cmsPage.getTemplateId();
        Optional<CmsTemplate> optional = templateRepository.findById(templateId);
        if (optional.isPresent()) {
            CmsTemplate cmsTemplate = optional.get();
            String templateFileId = cmsTemplate.getTemplateFileId();
            //获取文件
            GridFSFile gridFSFile = fsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开下载流
            GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            GridFsResource resource = new GridFsResource(gridFSFile, downloadStream);
            String template = null;
            try {
                template = IOUtils.toString(resource.getInputStream(), "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return template;
        } else {
            ExceptionCast.throwCustomException(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        return null;
    }

    //获取数据
    private Map getDataMap(String pageId) {
        CmsPage cmsPage = getCmsPage(pageId);
        String dataUrl = cmsPage.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)) {
            ExceptionCast.throwCustomException(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map data = forEntity.getBody();
        return data;
    }

    //根据pageId获取页面信息
    private CmsPage getCmsPage(String pageId) {
        CmsPage cmsPage = pageRepository.findByPageId(pageId);
        if (cmsPage == null) {
            ExceptionCast.throwCustomException(CmsCode.CMS_UPDATE_NOTFIND);
        }
        return cmsPage;
    }
}
