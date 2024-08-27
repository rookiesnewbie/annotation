package com.lt.annotation;

import com.lt.enums.VerfyRegexEnum;

import java.lang.annotation.*;


/**
 * 校验密码
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.FIELD})  //该注解可以用在参数，字段上
@Documented
public @interface PassWord {
    /**
     * 最小长度
     */
    int minLength() default  8;
    /**
     *最大长度
     */
    int maxLength() default  18;

    /**
     * 是否必须
     */
    boolean required() default true;


    /**
     * 匹配规则
     */
    VerfyRegexEnum regex() default VerfyRegexEnum.PASSWORD;
}
