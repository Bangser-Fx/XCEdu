package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FilesystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

/**
 * @program: XCEdu->FileSystemService
 * @description: 文件上传service
 * @author: Bangser
 * @create: 2019-08-13 15:17
 **/
@Service
public class FileSystemService {

    @Autowired
    private FilesystemRepository filesystemRepository;


    public UploadFileResult upload(MultipartFile multipartFile, String filetag, String businesskey, String metadata) {
        if(multipartFile==null){
            ExceptionCast.throwCustomException(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        //上传文件
        String fileId = uploadPic(multipartFile);
        if (StringUtils.isBlank(fileId)){
            ExceptionCast.throwCustomException(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        }
        //将文件信息存入MongoDB数据库
        FileSystem fileSystem;
        Optional<FileSystem> optional = filesystemRepository.findById(fileId);
        if(optional.isPresent()){
            fileSystem = optional.get();
        }else {
            fileSystem = new FileSystem();
        }
        fileSystem.setFileId(fileId);
        fileSystem.setFilePath(fileId);
        fileSystem.setFileName(multipartFile.getOriginalFilename());
        fileSystem.setFileSize(multipartFile.getSize());
        fileSystem.setFileType(multipartFile.getContentType());
        fileSystem.setFiletag(filetag);
        fileSystem.setBusinesskey(businesskey);
        if(StringUtils.isNotBlank(metadata)){
            Map map = JSON.parseObject(metadata, Map.class);
            fileSystem.setMetadata(map);
        }
        filesystemRepository.save(fileSystem);
        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);
    }


    //上传图片文件
    private String uploadPic(MultipartFile file) {
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            StorageClient storageClient = new StorageClient1(trackerServer, storageServer);
            String filename = file.getOriginalFilename();
            String extName = filename.substring(filename.lastIndexOf(".") + 1);
            String[] strings = storageClient.upload_file(file.getBytes(), extName, null);
            if (strings != null && strings.length == 2) {
                String fileId = strings[0] + "/" + strings[1];
                return fileId;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
