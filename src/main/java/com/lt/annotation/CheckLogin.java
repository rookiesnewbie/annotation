package com.lt.annotation;

import java.lang.annotation.*;


/**
 * 校验登陆
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})  //该注解只能用在方法上
@Documented
public @interface CheckLogin {
    boolean isLogin() default true;

    String message() default "请先登陆！！！";


}
