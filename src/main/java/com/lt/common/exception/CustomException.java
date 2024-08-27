package com.lt.common.exception;

import com.lt.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * 自定义异常
 */

@Data
public class CustomException extends RuntimeException{
    //异常状态码
    private Integer code;
    private String message;

    public CustomException(String message) {
        this.code = 5002;
        this.message = message;
    }

    /**
     * 通过状态码和错误消息创建异常对象
     * @param message
     * @param code
     */
    public CustomException(String message, Integer code) {
        //super(message);
        this.code = code;
        this.message = message;
    }


    /**
     * 接收枚举类型对象
     * @param resultCodeEnum
     */
    public CustomException(ResultCodeEnum resultCodeEnum) {
        //super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
    }

}
