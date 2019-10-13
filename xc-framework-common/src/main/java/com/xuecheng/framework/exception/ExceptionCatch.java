package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @description 全局异常处理类
 * @author BangSer
 * @date 2019/7/26 19:59
 */
@Slf4j
@RestControllerAdvice
public class ExceptionCatch {

    //使用ImmutableMap存放异常与对应信息，该map的特点是一旦创建不允许改变，而且线程安全
    protected static ImmutableMap<Class<? extends Throwable>,ResultCode> exceptionMap;
    //builder用来构建ImmutableMap的元素
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder = ImmutableMap.builder();
    //将需要捕捉的非自定义以外的异常放入构造器中
    static {
        builder.put(HttpMessageNotReadableException.class, CommonCode.INVALIDPARAM);
    }

    /**
    * @Description: 非自定义异常捕捉
    * @param: Exception
    * @return: ResponseResult （json形式）
    * @auther: Bangser
    * @date: 2019/7/26 20:53
    */
    @ExceptionHandler(Exception.class)
    public ResponseResult catchException(Exception e){
        log.error("exception:"+e.getMessage());
        //如果exceptionMap为空则用builder构建
        if(exceptionMap ==null){
            exceptionMap = builder.build();
        }
        //尝试将该异常对应的信息从excetionMap中取出
        ResultCode resultCode = exceptionMap.get(e.getClass());
        ResponseResult result;
        if(resultCode!=null){
            //不为空说明构造器含有该异常，返回对应错误信息
            result = new ResponseResult(resultCode);
        }else {
            //为空则构造器不含该异常，返回服务器错误的信息
            result = new ResponseResult(CommonCode.SERVER_ERROR);
        }
        return result;
    }

    /**
    * @Description: 自定义异常捕捉
    * @param: CustomException
    * @return: ResponseResult （json形式）
    * @auther: Bangser
    * @date: 2019/7/26 20:43
    */
    @ExceptionHandler(CustomException.class)
    public ResponseResult catchCustomerException(CustomException ce){
        log.error("exception:"+ce.getResultCode().message());
        ResultCode resultCode = ce.getResultCode();
        return new ResponseResult(resultCode);
    }
}
