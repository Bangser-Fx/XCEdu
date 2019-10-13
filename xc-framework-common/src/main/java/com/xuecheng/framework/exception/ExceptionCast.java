package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * @Description 抛出异常的工具类
 * @author BangSer
 * @date 2019/7/26 19:55
 */
public class ExceptionCast {

    public static void throwCustomException(ResultCode resultCode){
        throw new CustomException(resultCode);
    }
}
