package com.xuecheng.manage_media_process;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-07-12 9:11
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestProcessBuilder {

   @Test
   public void testProcessBuilder() throws IOException {
       ProcessBuilder processBuilder = new ProcessBuilder("ping","127.0.01");
       //合并输出流（正确输出流和异常输出流）
       processBuilder.redirectErrorStream(true);
       //开始执行
       Process process = processBuilder.start();
       InputStream inputStream = process.getInputStream();
       InputStreamReader reader = new InputStreamReader(inputStream,"gbk");
       StringBuffer buffer = new StringBuffer();
       char[] chars = new char[1024];
       int len;
       while ((len=reader.read(chars))!=-1){
           String s = new String(chars,0,len);
           buffer.append(s);
       }
       System.out.println(buffer.toString());
       inputStream.close();
       reader.close();

   }
}
