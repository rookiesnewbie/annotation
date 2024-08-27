package com.lt.aspect;

import com.lt.annotation.Number;
import com.lt.annotation.*;
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


/**
 * @author LiTeng
 * @create 2023/5/4
 */

@Aspect
//@Component
@Slf4j
public class GlobalOperationAspect1 {


    //@Pointcut("@annotation(com.lt.annotation.PassWord)")
    @Pointcut("execution(* com.lt.controller.*.*(..))") // 修改为拦截整个controller层的方法调用
    private void requestInterceptor(){

    }


    @Before("requestInterceptor()")
    public void interceptorDo(JoinPoint point) throws CustomException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Object[] args = point.getArgs();

        // 获取方法参数
        Parameter[] parameters = method.getParameters();

        //获取方法参数的注解
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        parseAnnotation(parameterAnnotations, args, method);

    }


    @Before("requestInterceptor()")
    public void interceptorMethod(JoinPoint point) throws CustomException, NoSuchMethodException {
        Object target = point.getTarget();
        Object[] args = point.getArgs();
        String methodName = point.getSignature().getName();
        Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
        Method method = target.getClass().getMethod(methodName, parameterTypes);

        CheckLogin checkLogin = method.getAnnotation(CheckLogin.class);

        //入方法上没有使用注解，直接放行
        if (checkLogin == null){
            return;
        }

        /**
         * 校验登录
         */
        if (checkLogin.isLogin()){
            checkLogin(checkLogin.isLogin(),checkLogin.message());
        }


    }


    /**
     * 校验密码规则
     * @param value
     * @param passWord
     * @return
     */
    private boolean isValidPassword(String value, PassWord passWord) {
        if (value == null) {
            throw new CustomException(ResultCodeEnum.PASSWORD_ONT_BLANK);
            //return !passWord.required(); // 如果是非必填字段且值为空，返回true
        }
        int length = value.length();
        if (length < passWord.minLength() || length > passWord.maxLength()) {
            throw new CustomException(passWord.regex().getDesc());
        }
        log.error("regex={}",passWord.regex().getDesc());
        // 校验规则使用正则表达式
        return value.matches(passWord.regex().getRegex());
    }

    /**
     * 校验数字规则
     * @param value
     * @param number
     * @return
     */
    private boolean isValidNumber(Object value, Number number) {
        if (value == null) {
            throw new CustomException(ResultCodeEnum.AGE_ONT_BLANK);
            //return !passWord.required(); // 如果是非必填字段且值为空，返回true
        }
        if (!String.valueOf(value).matches(number.regex().getRegex())){
            throw new CustomException(number.message());
        }
        // 校验规则使用正则表达式
        return String.valueOf(value).matches(number.regex().getRegex());
    }

    /**
     * 校验字段不能为空
     * @param value
     * @param notNull
     * @return
     */
    private boolean isValidNotNull(Object value, NotNull notNull) {
        if (value == null) {
            throw new CustomException(notNull.message());
            //return !passWord.required(); // 如果是非必填字段且值为空，返回true
        }

        // 校验规则使用正则表达式
        return String.valueOf(value).trim().matches(notNull.regex().getRegex());
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


    private void parseAnnotation( Annotation[][] annotations, Object[] obj, Method method) throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException {

        for (int i = 0; i < annotations.length; i++) {
            for (Annotation annotation : annotations[i]) {

                if (annotation instanceof PassWord) {
                    PassWord passWord = (PassWord) annotation;
                    String value = (String) obj[i];  // 假设参数是String类型
                    if (!isValidPassword(value, passWord)) {
                        throw new CustomException(passWord.regex().getDesc());
                    }
                }

                if (annotation instanceof Number) {
                    Number number = (Number) annotation;
                    Integer value = (Integer) obj[i];  // 假设参数是Integer类型
                    if (!isValidNumber(value, number)) {
                        throw new CustomException(number.regex().getDesc());
                    }
                }

                if (annotation instanceof NotNull) {
                    NotNull notNull = (NotNull) annotation;
                    Integer value = (Integer) obj[i];  // 假设参数是Integer类型
                    if (!isValidNotNull(value, notNull)) {
                        throw new CustomException(notNull.message());
                    }
                }

                if (annotation instanceof VerifyParam) {
                    //VerifyParam verifyParam = (VerifyParam) annotation;
                    String name = method.getParameterTypes()[i].getName();
                    Class<?> aClass = Class.forName(name);

                    Field[] declaredFields = aClass.getDeclaredFields();
                    for (Field declaredField : declaredFields) {
                        declaredField.setAccessible(true); // 使字段可访问

                        log.info("obj ={}",obj[i]);

                        //获取字段的值
                        Object value = declaredField.get(obj[i]);

                        //判断字段上是否存在PassWord注解
                        if (declaredField.isAnnotationPresent(PassWord.class)) {
                            if (value == null) {
                                throw new CustomException(ResultCodeEnum.PASSWORD_ONT_BLANK);
                                //return !passWord.required(); // 如果是非必填字段且值为空，返回true
                            }
                            PassWord passWord = declaredField.getAnnotation(PassWord.class);
                            int length = ((String) value).length();
                            if (length < passWord.minLength() || length > passWord.maxLength()) {
                                throw new CustomException(passWord.regex().getDesc());
                            }
                            // 校验规则使用正则表达式
                            boolean matches = ((String) value).matches(passWord.regex().getRegex());
                            if (!matches) {
                                throw new CustomException(passWord.regex().getDesc());
                            }
                        }

                        if (declaredField.isAnnotationPresent(NotNull.class)) {
                            NotNull notNull = declaredField.getAnnotation(NotNull.class);
                            if (value == null) {
                                throw new CustomException(notNull.message());
                                //return !passWord.required(); // 如果是非必填字段且值为空，返回true
                            }

                            // 校验逻辑
                            if (notNull.required() && (((String) value) == null || ((String) value).toString().trim().isEmpty())) {
                                throw new CustomException(notNull.message());
                            }

                        }
                    }
                }
            }
        }
    }
}
