package com.xuecheng.manage_course.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @program: XCEdu->CmsPageConfig
 * @description: 读取yml配置文件中的  保存课程的相关字段
 * @author: Bangser
 * @create: 2019-08-15 17:20
 **/
@Component
@ConfigurationProperties(prefix = "course-publish")
@Data
@ToString
public class CmsPageConfig {
    private String siteId;//站点id
    private String templateId;//模板id
    private String previewUrl;//页面预览url的前缀
    private String pageWebPath;//页面访问路径
    private String pagePhysicalPath;//页面存储路径
    private String dataUrlPre;//页面数据获取的路径前缀
}
