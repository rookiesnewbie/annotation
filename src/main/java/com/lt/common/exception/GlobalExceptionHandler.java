package com.lt.common.exception;

import com.lt.common.result.Result;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理
 */


//AOP 面向切面
@ControllerAdvice
public class GlobalExceptionHandler extends Throwable {

    @ExceptionHandler(Exception.class) //异常处理器
    @ResponseBody  //返回json数据
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.fail(null);
    }

    //自定义异常处理
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Result error(CustomException exception) {
        return Result.build(null,exception.getCode(),exception.getMessage());
    }

    //请求方式异常
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public Result handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String errorMessage = "Request method '" + e.getMethod() + "' not supported";
        return Result.fail(errorMessage);
    }

    //请求参数异常
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Result handleMissingServletRequestParameterException(MissingServletRequestParameterException e){
        String errorMessage = "Required " + e.getParameterType() + " parameter " + e.getParameterName() + " is not present";
        return Result.fail(errorMessage);
    }
}
