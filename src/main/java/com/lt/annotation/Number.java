package com.lt.annotation;

import com.lt.enums.VerfyRegexEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.FIELD})  //该注解可以用在参数，字段上
@Documented
public @interface Number {
    /**
     * 最小长度
     */
    int minLength() default  0;
    /**
     *最大长度
     */
    int maxLength() default  Integer.MAX_VALUE;

    /**
     * 是否必须
     */
    boolean required() default true;

    String message() default "只能输入数字";


    /**
     * 匹配规则
     */
    VerfyRegexEnum regex() default VerfyRegexEnum.NUMBER;
}
