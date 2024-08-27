package com.lt.aspect;

import com.lt.annotation.*;
import com.lt.annotation.Number;
import com.lt.common.exception.CustomException;
import com.lt.common.result.ResultCodeEnum;
import com.lt.domain.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;


/**
 * @author LiTeng
 * @create 2023/5/4
 */

@Aspect
@Component
@Slf4j
public class GlobalOperationAspect {


    //@Pointcut("@annotation(com.lt.annotation.PassWord)")
    @Pointcut("execution(* com.lt.controller.*.*(..))") // 修改为拦截整个controller层的方法调用
    private void requestInterceptor(){

    }


    @Before("requestInterceptor()")
    public void interceptorDo(JoinPoint point) throws CustomException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Object[] args = point.getArgs();


        /**
         * 1、先拦截方法
         * 2、再拦截参数
         */

        //获取方法上是否有指定的注解
        CheckLogin checkLogin = method.getAnnotation(CheckLogin.class);

        /**
         * 校验登录
         */
        if (checkLogin != null && checkLogin.isLogin()){
            checkLogin(checkLogin.isLogin(),checkLogin.message());
        }

        // 获取方法参数
        Parameter[] parameters = method.getParameters();

        parseAnnotation(args, parameters);

    }


    /*校验登录*/
    private void checkLogin(boolean isLogin,String message){
        HttpServletRequest request =  ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        LoginVo loginVo = (LoginVo) session.getAttribute("login");
        if (null == loginVo && isLogin){
            throw new CustomException(message);
        }

    }


    private void parseAnnotation(Object[] obj, Parameter[] parameters) {
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object value = obj[i];

            // 检查参数上是否存在 @ValidateParam 注解
            if (parameter.isAnnotationPresent(PassWord.class)) {
                PassWord annotation = parameter.getAnnotation(PassWord.class);
                validateArgument(value, annotation,null);
            }

            if (parameter.isAnnotationPresent(NotNull.class)){
                NotNull annotation = parameter.getAnnotation(NotNull.class);
                validateArgument(value, annotation,null);
            }

            if (parameter.isAnnotationPresent(Number.class)){
                Number annotation = parameter.getAnnotation(Number.class);
                validateArgument(value, annotation,null);
            }

            if (parameter.isAnnotationPresent(Email.class)){
                Email annotation = parameter.getAnnotation(Email.class);
                validateArgument(value, annotation,null);
            }
            if (parameter.isAnnotationPresent(Phone.class)){
                Phone annotation = parameter.getAnnotation(Phone.class);
                validateArgument(value, annotation,null);
            }

            if (parameter.isAnnotationPresent(VerifyParam.class)){
                VerifyParam annotation = parameter.getAnnotation(VerifyParam.class);
                validateArgument(value, annotation,parameter);
            }
        }

    }

    private void validateArgument(Object value, Annotation annotation,Parameter parameter) {
        if (annotation instanceof PassWord){
            PassWord passWord = (PassWord) annotation;
            if (value == null || value.toString().trim().isEmpty()) {
                throw new CustomException(ResultCodeEnum.PASSWORD_ONT_BLANK);
                //return !passWord.required(); // 如果是非必填字段且值为空，返回true
            }
            int length = value.toString().length();
            if (length < passWord.minLength() || length > passWord.maxLength()) {
                throw new CustomException(passWord.regex().getDesc());
            }

            // 校验规则使用正则表达式
            if (!value.toString().matches(passWord.regex().getRegex())){
                throw new CustomException(passWord.regex().getDesc());
            }
        }

        if (annotation instanceof NotNull){
            NotNull notNull = (NotNull) annotation;
            if (value == null || value.toString().trim().isEmpty()) {
                throw new CustomException(notNull.message());
                //return !passWord.required(); // 如果是非必填字段且值为空，返回true
            }
            // 校验规则使用正则表达式
            if (notNull.required() && (value == null || value.toString().trim().isEmpty())){
                throw new CustomException(notNull.message());
            }
        }

        if (annotation instanceof Number){
            Number number = (Number) annotation;
            if (value == null || value.toString().trim().isEmpty()) {
                throw new CustomException(number.message());
                //return !passWord.required(); // 如果是非必填字段且值为空，返回true
            }
            // 校验规则使用正则表达式
            if (number.required() && (value == null || value.toString().trim().isEmpty())){
                throw new CustomException(number.message());
            }
        }

        if (annotation instanceof Phone) {
            Phone phone = (Phone) annotation;
            if (value == null || value.toString().trim().isEmpty()) {
                throw new CustomException(phone.message());
                //return !passWord.required(); // 如果是非必填字段且值为空，返回true
            }

            // 校验逻辑
            if (phone.required() && (value == null || !value.toString().trim().matches(phone.regex().getRegex()))) {
                throw new CustomException("手机号格式不正确");
            }

        }

        if (annotation instanceof Email) {
            Email email = (Email) annotation;
            if (value == null || value.toString().trim().isEmpty()) {
                throw new CustomException(email.message());
                //return !passWord.required(); // 如果是非必填字段且值为空，返回true
            }

            // 校验逻辑
            if (email.required() && (value == null || !value.toString().trim().matches(email.regex().getRegex()))) {
                throw new CustomException("邮箱格式不正确");
            }

        }

        //校验参数是对象
        if (annotation instanceof VerifyParam){
            String name = parameter.getParameterizedType().getTypeName();
            Class<?> aClass = null;
            try {
                aClass = Class.forName(name);
                Field[] declaredFields = aClass.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    declaredField.setAccessible(true); // 使字段可访问

                    //获取字段的值
                    Object obj = declaredField.get(value);

                    //判断字段上是否存在PassWord注解
                    if (declaredField.isAnnotationPresent(PassWord.class)) {
                        if (obj == null) {
                            throw new CustomException(ResultCodeEnum.PASSWORD_ONT_BLANK);
                        }
                        PassWord passWord = declaredField.getAnnotation(PassWord.class);
                        int length = obj.toString().length();
                        if (length < passWord.minLength() || length > passWord.maxLength()) {
                            throw new CustomException(passWord.regex().getDesc());
                        }
                        // 校验规则使用正则表达式
                        boolean matches = obj.toString().matches(passWord.regex().getRegex());
                        if (!matches) {
                            throw new CustomException(passWord.regex().getDesc());
                        }
                    }

                    if (declaredField.isAnnotationPresent(NotNull.class)) {
                        NotNull notNull = declaredField.getAnnotation(NotNull.class);
                        if (obj == null) {
                            throw new CustomException(notNull.message());
                        }

                        // 校验逻辑
                        if (notNull.required() && (obj == null || obj.toString().trim().isEmpty())) {
                            throw new CustomException(notNull.message());
                        }

                    }

                    if (declaredField.isAnnotationPresent(Phone.class)) {
                        Phone phone = declaredField.getAnnotation(Phone.class);
                        if (obj == null) {
                            throw new CustomException(phone.message());
                        }

                        // 校验逻辑
                        if (phone.required() && (obj == null || !obj.toString().trim().matches(phone.regex().getRegex()))) {
                            throw new CustomException("手机号格式不正确");
                        }

                    }

                    if (declaredField.isAnnotationPresent(Email.class)) {
                        Email email = declaredField.getAnnotation(Email.class);
                        if (obj == null) {
                            throw new CustomException(email.message());
                        }

                        // 校验逻辑
                        if (email.required() && (obj == null || !obj.toString().trim().matches(email.regex().getRegex()))) {
                            throw new CustomException("邮箱格式不正确");
                        }

                    }
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
