package com.xuecheng.manage_cms.service.serviceImpl;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: XCEdu->SiteServiceImpl
 * @description:
 * @author: Bangser
 * @create: 2019-07-30 10:17
 **/
@Service("siteService")
public class SiteServiceImpl implements SiteService {

    @Autowired
    private CmsSiteRepository repository;

    @Override
    public QueryResponseResult findAll() {
        List<CmsSite> all = repository.findAll();
        QueryResult<CmsSite> queryResult = new QueryResult<>();
        queryResult.setList(all);
        queryResult.setTotal(all.size());
        QueryResponseResult result = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return result;
    }
}
