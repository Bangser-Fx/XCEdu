package com.xuecheng.search;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

/**
 * @program: XCEdu->ElasticsearchTest2
 * @description: Elasticsearch的查询测试
 * @author: Bangser
 * @create: 2019-08-18 19:52
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchTest2 {
    @Autowired
    private RestHighLevelClient client;

    //搜索全部并设置分页
    @Test
    public void searchAllPage() throws IOException {
        //创建查询请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //设置类型
        searchRequest.types("doc");
        //创建搜素参数构造器
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //源字段过滤：（需要搜索的字段，不搜索的字段）
        builder.fetchSource(new String[]{"name", "studymodel"}, null);
        //设置分页信息(from开始索引，从零开始; size每页记录数)
        builder.from(0);
        builder.size(2);
        //将参数放到请求对象中
        searchRequest.source(builder);
        //开始搜索
        SearchResponse response = client.search(searchRequest);
        //获取搜索结果对象
        SearchHits hits = response.getHits();
        long totalHits = hits.totalHits;//总记录数
        //获取结果集
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String id = searchHit.getId();//文档id
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();//获取文档项
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            System.out.println("id:" + id + ",name:" + name + ",studymodel:" + studymodel);
        }
    }

    //Term Query(精确搜索,不会将搜索值分词)
    @Test
    public void termQuery() throws IOException {
        SearchRequest request = new SearchRequest("xc_course");
        request.types("doc");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.fetchSource(new String[]{"name", "studymodel"}, null);
        //设置精确查询的字段，与值
        builder.query(QueryBuilders.termQuery("name", "spring"));
        request.source(builder);
        SearchResponse response = client.search(request);
        SearchHits hits = response.getHits();
        System.out.println(hits);
    }


    //根据id精确查询
    @Test
    public void termsQuery() throws IOException {
        SearchRequest request = new SearchRequest("xc_course");
        request.types("doc");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.fetchSource(new String[]{"name", "studymodel"}, null);
        //查询id为1,2的文档
        //String[] ids = new String[]{"1","2"};
        //List<String> list = Arrays.asList(ids);
        builder.query(QueryBuilders.termsQuery("_id", "1", "2"));
        request.source(builder);
        SearchResponse response = client.search(request);
        SearchHits hits = response.getHits();
        System.out.println(hits);
    }

    //MatchQuery(会将搜索值分词进行搜索)
    @Test
    public void matchQuery() throws IOException {
        //设置查询参数
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.fetchSource(new String[]{"name", "studymodel"}, null);
        //Operator.OR表示只要有一个词出现就符合
        builder.query(QueryBuilders.matchQuery("name", "spring开发").operator(Operator.OR));
        //minimumShouldMatch("80%")表示字段中出现80%个搜索分词就符合
        //builder.query(QueryBuilders.matchQuery("name","spring开发基础").minimumShouldMatch("80%"));
        //创建请求对象
        SearchRequest request = new SearchRequest("xc_course");
        request.types("doc");
        request.source(builder);
        SearchResponse response = client.search(request);
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }
        System.out.println(hits);
    }

    //multi Query可同时对多个字段进行搜索
    @Test
    public void multiQuery() throws IOException {
        //设置查询参数
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //在name和description字段中搜索java
        MultiMatchQueryBuilder matchQuery = QueryBuilders.multiMatchQuery("java", "name", "description").minimumShouldMatch("100%");
        //设置name字段10倍权重
        matchQuery.field("name", 10);
        builder.query(matchQuery);
        SearchRequest request = new SearchRequest("xc_course");
        request.types("doc");
        request.source(builder);
        SearchResponse response = client.search(request);
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }
    }

    //布尔查询（可拼装多个查询条件）
    @Test
    public void boolQuery() throws IOException {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        MultiMatchQueryBuilder matchQuery = QueryBuilders.multiMatchQuery("java开发", "name", "description");
        TermQueryBuilder termQuery = QueryBuilders.termQuery("studymodel", "201002");
        //创建布尔查询对象，并拼接查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        /*
         * must：表示必须，多个查询条件必须都满足。（通常使用must）
         * should：表示或者，多个查询条件只要有一个满足即可。
         * must_not：表示非。
         */
        boolQueryBuilder.must(matchQuery);
        boolQueryBuilder.must(termQuery);
        //将布尔查询对象设置到SearchSourceBuilder中
        builder.query(boolQueryBuilder);
        SearchRequest request = new SearchRequest("xc_course");
        request.types("doc");
        request.source(builder);
        SearchResponse search = client.search(request);
        SearchHit[] hits = search.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }
    }

    //过滤器以及排序
    @Test
    public void filter() throws IOException {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //设置过滤器，是对搜索结果进行过滤（比设置搜索条件效率高）
        //学习模式为201001
        boolQuery.filter(QueryBuilders.termQuery("studymodel","201001"));
        //价格在60到100区间
        boolQuery.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));
        //设置排序
        //价格降序排列
        builder.sort(new FieldSortBuilder("price").order(SortOrder.DESC));
        builder.query(boolQuery);
        SearchRequest request = new SearchRequest("xc_course");
        request.types("doc");
        request.source(builder);
        SearchResponse response = client.search(request);
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }
    }
    
    //高亮显示
    @Test
    public void highLight() throws IOException {
        //设置查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.multiMatchQuery("java","name","description"));
        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span class='red'>");
        highlightBuilder.postTags("</span>");
        highlightBuilder.field("name");//设置高亮字段
        builder.highlighter(highlightBuilder);

        SearchRequest request = new SearchRequest("xc_course");
        request.types("doc");
        request.source(builder);
        SearchResponse response = client.search(request);
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            String name = null;
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if(highlightFields!=null){
                HighlightField nameField = highlightFields.get("name");
                if(nameField!=null){
                    Text[] fragments = nameField.getFragments();
                    StringBuffer buffer = new StringBuffer();
                    for (Text text : fragments) {
                        buffer.append(text.toString());
                    }
                    name = buffer.toString();
                }
            }
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if(name==null){
                name = (String) sourceAsMap.get("name");
            }
            String description = (String) sourceAsMap.get("description");
            System.out.println("name:"+name+",description:"+description);
        }
    }

}
