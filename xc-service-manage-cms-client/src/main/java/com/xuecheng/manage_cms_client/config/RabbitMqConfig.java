package com.xuecheng.manage_cms_client.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: XCEdu->RabbitMqConfig
 * @description: RabbitMq配置类
 * @author: Bangser
 * @create: 2019-08-04 19:07
 **/
@Configuration
public class RabbitMqConfig {

    //队列Bean的名称
    public static final String QUEUE_CMS_POSTPAGE = "queue_cms_postpage";
    //交换机的名称
    public static final String EX_ROUTING_CMS_POSTPAGE="ex_routing_cms_postpage";
    //队列名称
    @Value("${xuecheng.mq.queue}")
    public String queue_cms_postpage;
    //routingKey
    @Value("${xuecheng.mq.routingKey}")
    public String routingKey;

    /**
    * 配置队列
    * @return: org.springframework.amqp.core.Queue
    */
    @Bean(QUEUE_CMS_POSTPAGE)
    public Queue queue() {
        Queue queue = new Queue(queue_cms_postpage);
        return queue;
    }

    /**
    * 配置交换机
    * @return: org.springframework.amqp.core.Exchange
    */
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange exchange(){
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }

    @Bean
    public Binding binding(@Qualifier(QUEUE_CMS_POSTPAGE) Queue queue,
                           @Qualifier(EX_ROUTING_CMS_POSTPAGE) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
    }

}
