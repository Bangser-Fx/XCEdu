package com.xuecheng.framework.domain.course.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: XCEdu->CoursePublishResult
 * @description: 课程预览响应类
 * @author: Bangser
 * @create: 2019-08-15 17:03
 **/
@Data
@ToString
@NoArgsConstructor
public class CoursePublishResult extends ResponseResult {

    String previewUrl;

    public CoursePublishResult(ResultCode resultCode, String previewUrl) {
        super(resultCode);
        this.previewUrl = previewUrl;
    }
}

