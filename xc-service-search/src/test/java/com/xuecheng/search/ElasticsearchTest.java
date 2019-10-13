package com.xuecheng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: XCEdu->ElasticsearchTest
 * @description: Elasticsearch测试
 * @author: Bangser
 * @create: 2019-08-17 18:19
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchTest {
    
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    
    @Autowired
    private RestClient restClient;

    //创建索引库
    @Test
    public void creatIndex() throws IOException {
        //创建“索引库创建”对象
        CreateIndexRequest request = new CreateIndexRequest("xc_course");
        //设置索引参数 (分片数，副本数)
        request.settings(Settings.builder().put("number_of_shards",1).put("number_of_replicas",0));
        //设置映射(相当于创建字段)
        request.mapping("doc","{\n" +
                "    \"properties\": {\n" +
                "        \"name\": {\n" +
                "            \"type\": \"text\"\n" +
                "        },\n" +
                "        \"description\": {\n" +
                "            \"type\": \"text\"\n" +
                "        },\n" +
                "        \"studymodel\": {\n" +
                "            \"type\": \"keyword\"\n" +
                "        }\n" +
                "    }\n" +
                "}", XContentType.JSON);
        //通过restHighClient获取索引客户端
        IndicesClient indices = restHighLevelClient.indices();
        CreateIndexResponse response = indices.create(request);
        boolean isSuccess = response.isShardsAcknowledged();
        System.out.println(isSuccess);
    }

    //删除索引库
    @Test
    public void deleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("xc_course");
        IndicesClient indices = restHighLevelClient.indices();
        DeleteIndexResponse delete = indices.delete(request);
        boolean acknowledged = delete.isAcknowledged();
        System.out.println(acknowledged);
    }
    
    //添加文档
    @Test
    public void addDocument() throws IOException {
        //准备json数据
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "spring cloud实战");
        jsonMap.put("description", "本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud 基础入门 3.实战Spring Boot 4.注册中心eureka。");
        jsonMap.put("studymodel", "201001");
        SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
        jsonMap.put("timestamp", dateFormat.format(new Date()));
        jsonMap.put("price", 5.6f);
        //创建索引请求对象
        IndexRequest request = new IndexRequest("xc_course","doc");
        //将数据设置到请求对象中
        request.source(jsonMap);
        IndexResponse response = restHighLevelClient.index(request);
        DocWriteResponse.Result result = response.getResult();
        System.out.println(result);
    }

    //根据id获取文档
    @Test
    public void searchDocument() throws IOException {
        GetRequest request = new GetRequest("xc_course","doc","PJeqpGwBbu9-lJTZ5G1x");
        GetResponse response = restHighLevelClient.get(request);
        if(response.isExists()){
            Map<String, Object> source = response.getSourceAsMap();
            System.out.println(source);
        }
    }

    //根据id删除文档
    @Test
    public void deleteDocument() throws IOException {
        DeleteRequest request =new DeleteRequest("xc_course","doc","PJeqpGwBbu9-lJTZ5G1x");
        DeleteResponse response = restHighLevelClient.delete(request);
        DocWriteResponse.Result result = response.getResult();
        System.out.println(result);
    }

}
