package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.manage_cms_client.service.PageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @program: XCEdu->RabbitMqListener
 * @description: RabbitMq监听类
 * @author: Bangser
 * @create: 2019-08-04 20:51
 **/
@Component
public class RabbitMqListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqListener.class);

    @Autowired
    private PageService pageService;

    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void listenQueueMsg(String msg){
        Map map = JSON.parseObject(msg, Map.class);
        if(map!=null){
            String pageId = (String) map.get("pageId");
            if (StringUtils.isNotBlank(pageId)){
                pageService.savePageToServerPath(pageId);
            }else {
                LOGGER.error("参数非法！无法获取到pageId");
                return;
            }
        }
    }
}
