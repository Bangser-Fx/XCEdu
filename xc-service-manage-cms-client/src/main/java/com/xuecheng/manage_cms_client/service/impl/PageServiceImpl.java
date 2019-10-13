package com.xuecheng.manage_cms_client.service.impl;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import com.xuecheng.manage_cms_client.service.PageService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

/**
 * @program: XCEdu->PageServiceImpl
 * @description:
 * @author: Bangser
 * @create: 2019-08-04 20:14
 **/
@Service("pageService")
public class PageServiceImpl implements PageService {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Override
    public void savePageToServerPath(String pageId) {
        //获取文件对象
        CmsPage cmsPage = getCmsPageById(pageId);
        if (cmsPage == null) {
            ExceptionCast.throwCustomException(CmsCode.CMS_UPDATE_NOTFIND);
            return;
        }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        //获取文件物理路径（需要存储到哪里）
        String path = cmsPage.getPagePhysicalPath() + cmsPage.getPageName();
        try {
            //获取对应输出流
            inputStream = getInputStream(cmsPage);
            //创建输出流
            outputStream = new FileOutputStream(new File(path));
            //保存到服务器
            IOUtils.copy(inputStream,outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //关闭流
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //根据CmsPage的htmlFileId获取文件输出流
    private InputStream getInputStream(CmsPage cmsPage) throws IOException {
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(cmsPage.getHtmlFileId())));
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        return gridFsResource.getInputStream();
    }

    //根据id获取页面
    private CmsPage getCmsPageById(String pageId) {
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (optional.isPresent()) {
            CmsPage cmsPage = optional.get();
            return cmsPage;
        }
        return null;
    }

    //根据CmsPage获取对应的站点
    private CmsSite getCmsSite(CmsPage cmsPage) {
        Optional<CmsSite> optional = cmsSiteRepository.findById(cmsPage.getSiteId());
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }
}
