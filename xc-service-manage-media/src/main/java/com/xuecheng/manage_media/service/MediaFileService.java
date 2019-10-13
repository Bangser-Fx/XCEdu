package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

/**
 * @program: XCEdu->MediaFileService
 * @description: 妹子文件管理service
 * @author: Bangser
 * @create: 2019-08-24 17:02
 **/
@Service
public class MediaFileService {

    @Autowired
    private MediaFileRepository mediaFileRepository;

    //分页条件查询
    public QueryResponseResult findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {
        if (queryMediaFileRequest == null) {
            queryMediaFileRequest = new QueryMediaFileRequest();
        }
        //1.封装查询条件参数
        //将条件参数封装到mediaFile对象中（两个对象的属性名要一致）
        MediaFile mediaFile = new MediaFile();
        if(StringUtils.isNotBlank(queryMediaFileRequest.getFileOriginalName())){
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        if(StringUtils.isNotBlank(queryMediaFileRequest.getProcessStatus())){
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }
        if(StringUtils.isNotBlank(queryMediaFileRequest.getTag())){
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }
        //创建条件查询对象(默认为精确查询)，设置文件名与标签为模糊查询
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("fileOriginalName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("tag", ExampleMatcher.GenericPropertyMatchers.contains());
        //创建Example
        Example<MediaFile> example = Example.of(mediaFile, exampleMatcher);
        //2.创建分页参数
        if (page <= 0) {
            page = 1;
        }
        page = page - 1;
        if (size <= 0) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size);
        //开始查询
        Page<MediaFile> all = mediaFileRepository.findAll(example, pageable);
        //封装返回结果
        QueryResult<MediaFile> queryResult = new QueryResult<>();
        queryResult.setTotal(all.getTotalElements());
        queryResult.setList(all.getContent());
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }
}
