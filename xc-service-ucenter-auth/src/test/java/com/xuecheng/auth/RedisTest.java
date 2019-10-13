package com.xuecheng.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @program: XCEdu->RedisTest
 * @description: 测试Redis
 * @author: Bangser
 * @create: 2019-08-31 20:24
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testAdd() {
        redisTemplate.boundValueOps("test").set("StringRedisTemplate",1, TimeUnit.MINUTES);
    }

    @Test
    public void testGet() {
        Long expire = redisTemplate.getExpire("test");
        if(expire==-2){
            System.out.println("已过期");
        }else if(expire==-1){
            System.out.println("未设置过期时间");
            String test = redisTemplate.boundValueOps("test").get();
            System.out.println(test);
        }else {
            String test = redisTemplate.boundValueOps("test").get();
            System.out.println(test);
        }
    }
}
