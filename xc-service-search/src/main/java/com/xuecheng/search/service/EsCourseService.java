package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @program: XCEdu->EsCourseService
 * @description: 课程搜索service
 * @author: Bangser
 * @create: 2019-08-20 21:20
 **/
@Service
public class EsCourseService {

    @Value("${xuecheng.course.index}")
    private String index;

    @Value("${xuecheng.course.type}")
    private String type;

    @Value("${xuecheng.course.source_field}")
    private String source_field;

    @Value("${xuecheng.media.index}")
    private String media_index;

    @Value("${xuecheng.media.type}")
    private String media_type;

    @Value("${xuecheng.media.source_field}")
    private String media_source_field;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //课程搜索
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) throws IOException {
        //拼装查询条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //设置搜索域
        sourceBuilder.fetchSource(source_field.split(","), null);
        //创建布尔查询对象（拼装多个条件）
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (courseSearchParam != null) {
            //关键字
            if (StringUtils.isNotBlank(courseSearchParam.getKeyword())) {
                //设置关键字搜索域
                MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(),
                        "name", "teachplan", "description");
                //设置关键字占比
                multiMatchQueryBuilder.minimumShouldMatch("70%");
                //设置权重
                multiMatchQueryBuilder.field("name", 10);
                boolQuery.must(multiMatchQueryBuilder);
            }
            //一级分类
            if (StringUtils.isNotBlank(courseSearchParam.getMt())) {
                TermQueryBuilder mt = QueryBuilders.termQuery("mt", courseSearchParam.getMt());
                boolQuery.filter(mt);
            }
            //二级分类
            if (StringUtils.isNotBlank(courseSearchParam.getSt())) {
                TermQueryBuilder st = QueryBuilders.termQuery("st", courseSearchParam.getSt());
                boolQuery.filter(st);
            }
            //难度等级
            if (StringUtils.isNotBlank(courseSearchParam.getGrade())) {
                TermQueryBuilder grade = QueryBuilders.termQuery("grade", courseSearchParam.getGrade());
                boolQuery.filter(grade);
            }
            //价格区间
            if (courseSearchParam.getPrice_min() != null) {
                RangeQueryBuilder price_min = QueryBuilders.rangeQuery("price_min").gte(courseSearchParam.getPrice_min());
                boolQuery.filter(price_min);
            }
            if (courseSearchParam.getPrice_max() != null) {
                RangeQueryBuilder price_max = QueryBuilders.rangeQuery("price_max").lte(courseSearchParam.getPrice_max());
                boolQuery.must(price_max);
            }
            //排序，过滤
        }
        sourceBuilder.query(boolQuery);
        //高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.preTags("<span class='esLight'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);
        //分页
        if(page<=0){
            page=1;
        }
        if(size<=0){
            size=10;
        }
        sourceBuilder.from((page-1)*size);
        sourceBuilder.size(size);
        //请求搜索
        SearchRequest request = new SearchRequest(index);
        request.types(type);
        request.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(request);
        //解析结果
        QueryResult<CoursePub> result = new QueryResult<>();
        SearchHits searchHits = response.getHits();
        result.setTotal(searchHits.getTotalHits());
        SearchHit[] hits = searchHits.getHits();
        List<CoursePub> coursePubList = new ArrayList<>();
        for (SearchHit hit : hits) {
            CoursePub coursePub = new CoursePub();
            String name = null;
            //取出高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields != null) {
                HighlightField highlightField = highlightFields.get("name");
                if(highlightField!=null){
                    Text[] fragments =  highlightField.getFragments();
                    StringBuffer buffer = new StringBuffer();
                    for (Text fragment : fragments) {
                        buffer.append(fragment.string());
                    }
                    name = buffer.toString();
                }
            }
            //取出原结果
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if(StringUtils.isBlank(name)){
                name = (String) sourceAsMap.get("name");
            }
            coursePub.setName(name);
            coursePub.setId((String) sourceAsMap.get("id"));
            coursePub.setPic((String) sourceAsMap.get("pic"));
            coursePub.setPrice((Double) sourceAsMap.get("price"));
            coursePub.setPrice_old((Double) sourceAsMap.get("price_old"));
            coursePubList.add(coursePub);
        }
        result.setList(coursePubList);
        return new QueryResponseResult<>(CommonCode.SUCCESS,result);
    }

    //根据课程id查询课程信息
    public Map<String, CoursePub> getAll(String id) {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.termQuery("id",id));
            SearchRequest request = new SearchRequest(index);
            request.types(type);
            request.source(sourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(request);
            SearchHit[] hits = searchResponse.getHits().getHits();
            Map<String,CoursePub> map = new HashMap<>();
            for (SearchHit hit : hits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                String courseId = (String) sourceAsMap.get("id");
                String name = (String) sourceAsMap.get("name");
                String grade = (String) sourceAsMap.get("grade");
                String charge = (String) sourceAsMap.get("charge");
                String pic = (String) sourceAsMap.get("pic");
                String description = (String) sourceAsMap.get("description");
                String teachplan = (String) sourceAsMap.get("teachplan");
                CoursePub coursePub = new CoursePub();
                coursePub.setId(courseId);
                coursePub.setName(name);
                coursePub.setCharge(charge);
                coursePub.setPic(pic);
                coursePub.setGrade(grade);
                coursePub.setTeachplan(teachplan);
                coursePub.setDescription(description);
                map.put(courseId,coursePub);
            }
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //根据课程计划id获取课程计划于媒资管理对应关系信息
    public QueryResponseResult<TeachplanMediaPub> getmedia(String[] teachplanIds) {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            //设置搜索域
            String[] strings = media_source_field.split(",");
            sourceBuilder.fetchSource(strings,null);
            sourceBuilder.query(QueryBuilders.termsQuery("teachplan_id",teachplanIds));
            SearchRequest request = new SearchRequest(media_index);
            request.types(media_type);
            request.source(sourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(request);
            SearchHits responseHits = searchResponse.getHits();
            SearchHit[] hits = responseHits.getHits();
            List<TeachplanMediaPub> teachplanMediaPubList = new ArrayList<>();
            for (SearchHit hit : hits) {
                TeachplanMediaPub teachplanMediaPub =new TeachplanMediaPub();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //取出课程计划媒资信息
                String courseid = (String) sourceAsMap.get("courseid");
                String media_id = (String) sourceAsMap.get("media_id");
                String media_url = (String) sourceAsMap.get("media_url");
                String teachplan_id = (String) sourceAsMap.get("teachplan_id");
                String media_fileoriginalname = (String) sourceAsMap.get("media_fileoriginalname");
                teachplanMediaPub.setCourseId(courseid);
                teachplanMediaPub.setMediaUrl(media_url);
                teachplanMediaPub.setMediaFileOriginalName(media_fileoriginalname);
                teachplanMediaPub.setMediaId(media_id);
                teachplanMediaPub.setTeachplanId(teachplan_id);
                //将数据加入列表
                teachplanMediaPubList.add(teachplanMediaPub);
            }
            QueryResult<TeachplanMediaPub> queryResult = new QueryResult<>();
            queryResult.setTotal(responseHits.getTotalHits());
            queryResult.setList(teachplanMediaPubList);
            return new QueryResponseResult<>(CommonCode.SUCCESS,queryResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
