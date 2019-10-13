package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: XCEdu->FileSystemControllerApi
 * @description: 文件上传api
 * @author: Bangser
 * @create: 2019-08-13 15:04
 **/
@Api(value="文件上传管理接口",description = "文件上传接口，提供文件的管理")
public interface FileSystemControllerApi {

    @ApiOperation("上传文件")
    UploadFileResult upload(MultipartFile multipartFile,
                            String filetag,
                            String businesskey,
                            String metadata);



}
