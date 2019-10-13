package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * @program: XCEdu->MediaUploadService
 * @description: 媒资管理service
 * @author: Bangser
 * @create: 2019-08-23 13:08
 **/
@Service
public class MediaUploadService {

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${xc-service-manage-media.upload-location}")
    private String uploadLocation;

    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    private String rouingKey_video;

    /**
     * 根据文件md5得到文件路径
     * 规则：
     * 一级目录：md5的第一个字符
     * 二级目录：md5的第二个字符
     * 三级目录：md5
     * 文件名：md5+文件扩展名
     */
    //文件夹路径
    private String getFileFolderPath(String fileMD5) {
        String fileFolderPath = uploadLocation + fileMD5.substring(0, 1) + "/" + fileMD5.substring(1, 2) + "/" + fileMD5 + "/";
        return fileFolderPath;
    }

    //文件路径
    private String getFilePath(String fileMD5, String fileExt) {
        String filePath = getFileFolderPath(fileMD5) + fileMD5 + "." + fileExt;
        return filePath;
    }

    //文件相对路径
    private String getFileFolderRelativePath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
    }

    //创建文件夹
    private Boolean creatFileFolder(String fileMD5) {
        File file = new File(getFileFolderPath(fileMD5));
        if (!file.exists()) {
            boolean mkdir = file.mkdirs();
            return mkdir;
        }
        return true;
    }

    //获取分片文件夹
    private String getChunkFolder(String fileMd5) {
        String chunkFolder = getFileFolderPath(fileMd5) + "chunks" + "/";
        return chunkFolder;
    }

    //获取分片文件路径
    private String getChunkFilePath(String fileMd5, Integer chunk) {
        String chunkFilePath = getChunkFolder(fileMd5) + chunk;
        return chunkFilePath;
    }

    //创建分片文件目录
    private Boolean creatChunkFolder(String fileMd5) {
        File file = new File(getChunkFolder(fileMd5));
        if (!file.exists()) {
            boolean mkdir = file.mkdirs();
            return mkdir;
        }
        return true;
    }

    //注册文件
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //查看文件是否存在
        File file = new File(getFilePath(fileMd5, fileExt));
        boolean exists = file.exists();
        //查看数据库是否存在
        Optional<MediaFile> optional = mediaFileRepository.findById(fileMd5);
        if (optional.isPresent() && exists) {
            //文件已存在
            ExceptionCast.throwCustomException(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        //不存在则创建文件夹
        Boolean creatFileFolder = creatFileFolder(fileMd5);
        if (!creatFileFolder) {
            //创建文件夹失败
            ExceptionCast.throwCustomException(MediaCode.UPLOAD_FILE_REGISTER_FAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //检查分片
    public CheckChunkResult checkchunk(String fileMd5, Integer chunk, Integer chunkSize) {
        //获取分片路径
        String chunkFilePath = getChunkFilePath(fileMd5, chunk);
        File file = new File(chunkFilePath);
        boolean exists = file.exists();
        if (exists) {
            //存在
            return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK, true);
        } else {
            //不存在
            return new CheckChunkResult(CommonCode.SUCCESS, false);
        }
    }

    //上传分片
    public ResponseResult uploadchunk(MultipartFile file, Integer chunk, String fileMd5) {
        if (file == null) {
            ExceptionCast.throwCustomException(CommonCode.INVALIDPARAM);
        }
        //创建分片文件目录
        Boolean chunkFolder = creatChunkFolder(fileMd5);
        if (!chunkFolder) {
            ExceptionCast.throwCustomException(MediaCode.UPLOAD_CHUNK_FAIL);
        }
        //分片文件
        File chunkFile = new File(getChunkFilePath(fileMd5, chunk));
        //上传
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = file.getInputStream();
            outputStream = new FileOutputStream(chunkFile);
            IOUtils.copy(inputStream,outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionCast.throwCustomException(MediaCode.UPLOAD_CHUNK_FAIL);
        }finally {
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
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //合并文件
    public ResponseResult mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //获取分片文件列表
        List<File> chunkFileList = getChunkFileList(fileMd5);
        //获取合并文件对象
        File megerFile = creatNewMegerFile(fileMd5, fileExt);
        //合并文件
        megerFile = mergerFile(chunkFileList,megerFile);
        //与前端的fileMd5比较
        Boolean aBoolean = checkFileMD5(fileMd5,megerFile);
        if(!aBoolean){
            ExceptionCast.throwCustomException(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        //保存文件信息到数据库
        //将文件信息保存到数据库
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileName(fileMd5+"."+fileExt);
        mediaFile.setFileOriginalName(fileName);
        //文件路径保存相对路径
        mediaFile.setFilePath(getFileFolderRelativePath(fileMd5));
        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(mimetype);
        mediaFile.setFileType(fileExt);
        //状态为上传成功
        mediaFile.setFileStatus("301002");
        mediaFileRepository.save(mediaFile);
        //发布消息到视屏处理服务
        Map<String,String> map = new HashMap<>();
        map.put("mediaId",mediaFile.getFileId());
        String jsonString = JSON.toJSONString(map);
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK,rouingKey_video,jsonString);
        } catch (AmqpException e) {
            e.printStackTrace();
            ExceptionCast.throwCustomException(MediaCode.RABBIT_MSG_SENDFAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }


    //校验文件
    private Boolean checkFileMD5(String fileMd5, File mergeFile) {
        if(StringUtils.isBlank(fileMd5) || !mergeFile.exists()){
            return false;
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(mergeFile);
            String md5 = DigestUtils.md5DigestAsHex(inputStream);
            if(fileMd5.equalsIgnoreCase(md5)){
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    //合并文件
    private File mergerFile(List<File> fileList,File mergerFile){
        if(fileList == null || !mergerFile.exists()){
            ExceptionCast.throwCustomException(MediaCode.MERGE_FILE_MERGEFAIL);
        }
        try {
            //创建合并文件的写入流
            RandomAccessFile write = new RandomAccessFile(mergerFile,"rw");
            byte[] bytes =new byte[1024];//缓冲区
            //循环分片文件
            for (File file : fileList) {
                RandomAccessFile read = new RandomAccessFile(file,"r");
                int len = -1;
                while ((len =read.read(bytes))!=-1 ){
                    write.write(bytes,0,len);
                }
                read.close();
            }
            write.close();
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionCast.throwCustomException(MediaCode.MERGE_FILE_MERGEFAIL);
        }
        return mergerFile;
    }

    //创建新的合并文件
    private File creatNewMegerFile(String fileMd5,String fileExt){
        //创建合并文件对象，若该文件存在，则先删除
        File margeFile = new File(getFilePath(fileMd5,fileExt));
        if(margeFile.exists()){
            margeFile.delete();
        }
        //创建合并文件
        boolean newFile = false;
        try {
            newFile = margeFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!newFile){
            ExceptionCast.throwCustomException(MediaCode.MERGEFILE_CREAT_FAIL);
        }
        return new File(getFilePath(fileMd5,fileExt));

    }

    //获取分片列表并按照文件名排序
    private List<File> getChunkFileList(String fileMd5){
        //获取分片文件目录
        File chunkFolder = new File(getChunkFolder(fileMd5));
        if(!chunkFolder.exists()){
            ExceptionCast.throwCustomException(MediaCode.CHUNK_NOT_FOUND);
        }
        File[] files = chunkFolder.listFiles();
        List<File> fileList = Arrays.asList(files);
        fileList.sort(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if(Integer.parseInt(o1.getName())>Integer.parseInt(o2.getName())){
                    return 1;
                }else {
                    return -1;
                }
            }
        });
        return fileList;
    }
}
