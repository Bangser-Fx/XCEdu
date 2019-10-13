package com.xuecheng;

import com.xuecheng.freemarker_test.model.Student;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.*;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestFreemarkerApplicationTests {


    @Test
    public void testCreatHtmlByFtl() throws IOException, TemplateException {
        //创建配置文件对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //获取模板文件位置
        String path = this.getClass().getResource("/").getPath();
        //设置模板文件位置
        configuration.setDirectoryForTemplateLoading(new File(path + "/templates/"));
        //设置模板文件位置
        configuration.setDefaultEncoding("utf-8");
        //获取模板
        Template template = configuration.getTemplate("test01.ftl");
        //设置数据
        Map<String, Object> dataMap = getDataMap();
        //静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, dataMap);
        System.out.println(content);
        InputStream in = IOUtils.toInputStream(content);
        FileOutputStream out = new FileOutputStream(new File("D:/test.html"));
        IOUtils.copy(in, out);
        in.close();
        out.close();
    }

    @Test
    public void testCreatHtmlByStr() throws IOException, TemplateException {
        //创建配置文件对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //设置模板
        String templateString = "" +
                "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                " 名称：${name}\n" +
                " </body>\n" +
                "</html>";
        StringTemplateLoader loader = new StringTemplateLoader();
        loader.putTemplate("test",templateString);
        configuration.setTemplateLoader(loader);
        Template template = configuration.getTemplate("test");
        //设置数据
        Map map = new HashMap();
        map.put("name","方兴");
        //静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        InputStream in = IOUtils.toInputStream(content);
        OutputStream out = new FileOutputStream(new File("D:/test.html"));
        IOUtils.copy(in,out);
        in.close();
        out.close();
    }

    private Map<String, Object> getDataMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "方兴");
        //学生小明
        Student stu1 = new Student();
        stu1.setName("小明");
        stu1.setAge(18);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());
        //学生小红
        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMoney(200.1f);
        stu2.setAge(19);
        stu2.setBirthday(new Date());
        //小红的朋友列表中有小明(最好的朋友)
        List<Student> friends = new ArrayList<>();
        friends.add(stu1);
        stu2.setFriends(friends);
        stu2.setBestFriend(stu1);
        //学生列表(小明和小红)
        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);
        //向数据模型放数据
        map.put("stus", stus);
        //准备map数据
        HashMap<String, Student> stuMap = new HashMap<>();
        stuMap.put("stu1", stu1);
        stuMap.put("stu2", stu2);
        //向数据模型放数据
        map.put("stu1", stu1);
        //向数据模型放数据
        map.put("stuMap", stuMap);
        return map;
    }





}
