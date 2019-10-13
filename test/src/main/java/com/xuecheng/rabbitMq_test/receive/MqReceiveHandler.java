package com.xuecheng.rabbitMq_test.receive;

import com.xuecheng.rabbitMq_test.config.RabbitmqConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @program: XCEdu->MqReceiveHandler
 * @description: RabbitMq接受消息监听类
 * @author: Bangser
 * @create: 2019-08-03 18:04
 **/
@Component
public class MqReceiveHandler {

    /**
    * 监听邮件消息队列
    * @params: [msg]
    * @return: void
    */
    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_EMAIL})
    public void receiveEmailQueue(String msg){
        System.out.println("Email-接收到消息："+msg);
    }


    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_SMS})
    public void receiveSmsQueue(String msg){
        System.out.println("Sms-接收到消息"+msg);
    }
}
