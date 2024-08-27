package com.lt.annotation;


import com.lt.enums.VerfyRegexEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.FIELD})  //该注解可以用在参数，字段上
@Documented
public @interface NotNull {
    /**
     * 是否必须
     */
    boolean required() default true;

    String message() default "该字段不能为空";

    /**
     * 匹配规则
     */
    VerfyRegexEnum regex() default VerfyRegexEnum.NOTNULL;
}
