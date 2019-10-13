package com.xuecheng;

import com.xuecheng.rabbitMq_test.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestRabbitMqApplicationTests {


    @Autowired
    private RabbitTemplate rabbitTemplate;
    //inform.#.sms.#

    /**
    * 因为监听程序也在本项目中，当本方法运行后，本项目也会启动，消息会立即被监听程序消费
    * @params: []
    * @return: void
    */
    @Test
    public void testSendToEmail() {
        for (int i= 0;i<5;i++){
            String message = "Hello rabbitMq";
            rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM, "inform.sms.email", message);
        }
    }

}
