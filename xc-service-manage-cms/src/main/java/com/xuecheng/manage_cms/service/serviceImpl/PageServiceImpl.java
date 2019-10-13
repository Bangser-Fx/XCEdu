package com.xuecheng.manage_cms.service.serviceImpl;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.service.PageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

/**
 * @program: XCEdu
 * @description:
 * @author: Bangser
 * @create: 2019-07-22 20:52
 **/
@Service("pageService")
public class PageServiceImpl implements PageService {

    @Autowired
    private CmsPageRepository repository;

    @Override
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        //封装条件参数
        CmsPage cmsPage = new CmsPage();
        if (queryPageRequest != null) {
            if (StringUtils.isNoneBlank(queryPageRequest.getSiteId()))
                cmsPage.setSiteId(queryPageRequest.getSiteId());
            if (StringUtils.isNotBlank(queryPageRequest.getTemplateId()))
                cmsPage.setTemplateId(queryPageRequest.getTemplateId());
            if (StringUtils.isNotBlank(queryPageRequest.getPageAliase()))
                cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //创建条件对象
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        //处理页码参数
        if (page <= 0) {
            page = 1;
        }
        if (size < 0) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        //开始查询
        Page<CmsPage> all = repository.findAll(example, pageable);
        QueryResult<CmsPage> queryResult = new QueryResult<>();
        queryResult.setList(all.getContent());
        queryResult.setTotal(all.getTotalElements());
        QueryResponseResult result = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return result;
    }

    @Override
    public CmsPage findById(String pageId) {
        return repository.findByPageId(pageId);
    }


    @Override
    public CmsPageResult addCmsPage(CmsPage cmsPage) {
        //判斷是否已存在该页
        CmsPage one = repository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        //已存在抛出相关异常
        if (one != null) {
            ExceptionCast.throwCustomException(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        cmsPage.setPageId(null);//主键由spring data生成
        CmsPage save = repository.save(cmsPage);
        CmsPageResult result = new CmsPageResult(CommonCode.SUCCESS, save);
        return result;
    }

    @Override
    public CmsPageResult updateCmsPage(String pageId, CmsPage cmsPage) {
        //检查是否存在该页面
        CmsPage one = findById(pageId);
        if (one == null) {
            ExceptionCast.throwCustomException(CmsCode.CMS_UPDATE_NOTFIND);
        }
        //更新相关数据
        one.setTemplateId(cmsPage.getTemplateId());
        one.setSiteId(cmsPage.getSiteId());
        one.setPageAliase(cmsPage.getPageAliase());
        one.setPageName(cmsPage.getPageName());
        one.setPageWebPath(cmsPage.getPageWebPath());
        one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
        one.setDataUrl(cmsPage.getDataUrl());
        CmsPage save = repository.save(one);
        return new CmsPageResult(CommonCode.SUCCESS,save);
    }

    @Override
    public ResponseResult deleteCmsPage(String pageId) {
        CmsPage one = findById(pageId);
        if(one==null){
            ExceptionCast.throwCustomException(CmsCode.CMS_UPDATE_NOTFIND);
        }
        repository.deleteById(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Override
    public CmsPageResult save(CmsPage cmsPage) {
        if(cmsPage == null){
            ExceptionCast.throwCustomException(CommonCode.INVALIDPARAM);
        }
        CmsPage one = repository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if(one!=null){
            return this.updateCmsPage(one.getPageId(), cmsPage);
        }else {
            return this.addCmsPage(cmsPage);
        }
    }
}
