package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.manage_cms.service.PagePostService;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GridFSTest {

    @Autowired
    private GridFsTemplate fsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private PagePostService pagePostService;

    @Test
    public void testUpload() throws FileNotFoundException {
        File file = new File("D:/course.ftl");
        InputStream in = new FileInputStream(file);
        ObjectId objectId = fsTemplate.store(in, "courseDetailTemplate", "ftl");
        System.out.println("模板Id："+objectId.toString());
       //course.ftl: 5d550c9ea022ac07a49e9823
    }
    
    @Test
    public void testDownload() throws IOException {
        String objectId = "5d550c9ea022ac07a49e9823";
        //获取文件
        GridFSFile fsFile = fsTemplate.findOne(Query.query(Criteria.where("_id").is(objectId)));
        //根据文件id打开下载流
        GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(fsFile.getObjectId());
        //创建gridFsResource，用于获取流对象
        GridFsResource resource = new GridFsResource(fsFile,downloadStream);
        //获取流数据
        OutputStream out = new FileOutputStream(new File("D:/course2.ftl"));
        IOUtils.copy(resource.getInputStream(),out);
        out.close();
    }

    @Test
    public void testDelete() {
       fsTemplate.delete(Query.query(Criteria.where("_id").is("5abf3d515b05aa0444d79840")));
    }

    @Test
    public void freemarker() {
        //测试轮播图test静态化
        String html = pagePostService.getHtmlById("5d428f58bd1bed41b4caa41b");
        System.out.println(html);
    }
}
