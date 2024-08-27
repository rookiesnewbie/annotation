package com.lt.annotation;



import java.lang.annotation.*;

/**
 * 校验对象参数
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})  //该注解可以用在参数上
@Documented
public @interface VerifyParam {
    boolean required() default true;

}
