package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * @program: XCEdu->MqListener
 * @description: 视屏处理消息监听
 * @author: Bangser
 * @create: 2019-08-24 12:58
 **/
@Component
public class MqListener {

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Value("${xc-service-manage-media.video-location}")
    private String videoLocation;//视屏路径前缀

    @Value("${xc-service-manage-media.ffmpeg-path}")
    private String ffmepegPath;//视频处理程序路径

    //监听视频处理队列
    @RabbitListener(queues = "${xc-service-manage-media.mq.queue-media-video-processor}",containerFactory = "customContainerFactory")
    public void videoListener(String msg){
        //获取消息中的id
        Map map = JSON.parseObject(msg, Map.class);
        String mediaId = (String) map.get("mediaId");
        //查看数据库是否存在该条记录
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if(!optional.isPresent()){
            return;
        }
        //获取视频记录
        MediaFile mediaFile = optional.get();
        //判断视屏类型(只处理avi类型)
        String fileType = mediaFile.getFileType();
        if(!fileType.equals("avi") || StringUtils.isBlank(fileType)){
            mediaFile.setProcessStatus("303004");//设置处理状态为无需处理
            mediaFileRepository.save(mediaFile);
            return;
        }else {
            mediaFile.setProcessStatus("303001");//未处理
            mediaFileRepository.save(mediaFile);
        }
        //将avi转为MP4
        String toMp4 = aviToMp4(mediaFile);
        if(!toMp4.equals("success")){
            mediaFile.setProcessStatus("303003");//处理失败
            MediaFileProcess_m3u8 m3u8 = new MediaFileProcess_m3u8();
            m3u8.setErrormsg(toMp4);//记录错误信息
            mediaFile.setMediaFileProcess_m3u8(m3u8);
            mediaFileRepository.save(mediaFile);
            return;
        }
        //将MP4转为hls
        String video_path = videoLocation + mediaFile.getFilePath() + mediaFile.getFileId() + ".mp4";
        String m3u8_name = mediaFile.getFileId() + ".m3u8";
        String m3u8folder_path = videoLocation + mediaFile.getFilePath() + "hls/";
        HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(ffmepegPath,video_path,m3u8_name,m3u8folder_path);
        String toHls = hlsVideoUtil.generateM3u8();
        if(!toHls.equals("success")){
            mediaFile.setProcessStatus("303003");
            MediaFileProcess_m3u8 m3u8 = new MediaFileProcess_m3u8();
            m3u8.setErrormsg(toHls);//记录错误信息
            mediaFile.setMediaFileProcess_m3u8(m3u8);
            return;
        }
        //处理完成，记录信息到数据库
        mediaFile.setProcessStatus("303002");//处理成功
        MediaFileProcess_m3u8 m3u8 = new MediaFileProcess_m3u8();
        m3u8.setTslist(hlsVideoUtil.get_ts_list());
        mediaFile.setMediaFileProcess_m3u8(m3u8);//hls文件列表
        mediaFile.setFileUrl(mediaFile.getFilePath() + "hls/"+m3u8_name);//m3u8文件的路径
        mediaFileRepository.save(mediaFile);


    }

    private String aviToMp4(MediaFile mediaFile){
        //String ffmpeg_path, String video_path, String mp4_name, String mp4folder_path
        String video_path = videoLocation + mediaFile.getFilePath() +mediaFile.getFileName();
        String mp4_name = mediaFile.getFileId() + ".mp4";
        String mp4folder_path = videoLocation + mediaFile.getFilePath();//转换后的MP4文件存放位置
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmepegPath,video_path,mp4_name,mp4folder_path);
        String result = mp4VideoUtil.generateMp4();
        return result;
    }

}
