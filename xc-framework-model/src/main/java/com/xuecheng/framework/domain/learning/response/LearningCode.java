package com.xuecheng.framework.domain.learning.response;

import com.xuecheng.framework.model.response.ResultCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: XCEdu->LearningCode
 * @description:
 * @author: Bangser
 * @create: 2019-08-25 21:53
 **/
@ToString
@NoArgsConstructor
public enum LearningCode implements ResultCode {
    LEARNING_GETMEDIA_ERROR(false,61001,"获取视频播放地址出错！");
    //操作代码
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;
    private LearningCode(boolean success, int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
