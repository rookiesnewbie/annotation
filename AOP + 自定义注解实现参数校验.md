# AOP + 自定义注解实现参数校验
## 1、导入maven依赖
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>

        <artifactId>spring-boot-starter-web</artifactId>

    </dependency>

    <!--Aop-->
    <dependency>
        <groupId>org.springframework.boot</groupId>

        <artifactId>spring-boot-starter-aop</artifactId>

    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>

        <artifactId>lombok</artifactId>

        <optional>true</optional>

    </dependency>

</dependencies>

```
## 2、自定义参数校验注解
### 2.1、校验是否登陆
```java
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
```

### 2.2、校验邮箱格式是否正确
```java
/**
 * 校验邮箱
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.FIELD})  //该注解可以用在参数，字段上
@Documented
public @interface Email {

    /**
     * 是否必须
     */
    boolean required() default true;

    String message() default "邮箱不能为空";


    /**
     * 匹配规则
     */
    VerfyRegexEnum regex() default VerfyRegexEnum.EMAIL;
}
```

### 2.3、校验手机号格式是否正确
```java
/**
 * 校验手机号
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.FIELD})  //该注解可以用在参数，字段上
@Documented
public @interface Phone {

    /**
     * 是否必须
     */
    boolean required() default true;

    String message() default "手机号不能为空";


    /**
     * 匹配规则
     */
    VerfyRegexEnum regex() default VerfyRegexEnum.PHONE;
}
```

### 2.4、校验方法参数是对象，对象的字段使用到自定义参数注解
```java
/**
 * 校验对象参数
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})  //该注解可以用在参数上
@Documented
public @interface VerifyParam {
    boolean required() default true;

}
```

### 2.5、校验参数或字段不能为空
```java
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
```

### 2.6、校验密码是否满足规则
```java
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
```

## 3、AOP定义切面
通用切点表达式：
`execution([可见性] 返回类型 [声明类型].方法名(参数) [异常类型])`
其中：
execution：切入点表达式关键字；
[可见性]：可选，指定方法的可见性，如 public、private、protected 或 *；
返回类型：指定方法的返回类型，如 void、int、String 等；
[声明类型]：可选，指定方法所属的类、接口、注解等声明类型；
方法名：指定方法的名称，支持通配符 *；
参数：指定方法的参数类型列表，用逗号分隔，支持通配符 *；
[异常类型]：可选，指定方法可能抛出的异常类型。
例:
execution(public * com.example.service.UserService.addUser(…))：指定 com.example.service.UserService 类中的 addUser 方法；
execution(* com.example.service._._(…))：指定 com.example.service 包下的所有方法；
execution(* com.example.service…_._(…))：指定 com.example.service 包及其子包下的所有方法；
execution(* com.example.service.UserService.*(String))：指定 com.example.service.UserService 类中所有参数类型为 String 的方法。
此外，切入点表达式还支持 &&（逻辑与）、||（逻辑或）和 !（逻辑非）等运算符，以及 @annotation、@within、@args 等注解限定符。
```java
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
```

注意：定义切点时**`@annotation`和`@within`用法的区别**：

- `@annotation`用于拦截标注在方法上的注解，
- `@within`用于拦截标注在类上的注解。

这两种方式的使用方法如下：
```java
package com.example.minio.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @Author LiTeng
 * @Date 2023/10/13 10:38
 * Version 1.0
 * @Description 参数校验切面
 */

@Aspect
@Component
@Slf4j
public class OperationAspect {

    //定义切点，拦截方法上的注解
    @Pointcut("@annotation(com.example.minio.annotation.Checking)")

    public void pointCutMethod(){

    }
    
    //定义切点，拦截类上的注解
    @Pointcut("@within(com.example.minio.annotation.Checking)")
    public void pointCutClass(){

    }

    //定义切面的通知类型
    @Before("pointCutMethod()") //在切点执行之前要进行校验
    public void  beforeOperation(JoinPoint joinPoint){
        log.info(joinPoint.getArgs().toString());

    }
    
    //定义切面的通知类型
    @Before("pointCutClass()") //在切点执行之前要进行校验
    public void  beforeOperation(JoinPoint joinPoint){
        log.info(joinPoint.getArgs().toString());

    }

}
```



## 4、使用自定义参数校验注解
本案例相关的实体类
```java
@Data
public class LoginVo implements Serializable {

    @NotNull(message = "用户名不能为空")
    private String username;

    @NotNull(message = "密码不能为空")
    private String password;
}
```
```java
@Data
public class User implements Serializable {

    private Integer id;

    @NotNull(message = "username不能为空")
    private String username;

    @PassWord
    private String password;

    private Integer age;

    private String sex;

    @Phone
    private String phone;

    @Email
    private String email;
}
```
### 4.1、校验是否登陆
#### 4.1.1、代码实现
```java
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @PostMapping("/login")
    public Result login(@RequestBody  @VerifyParam LoginVo loginVo, HttpSession session){
        session.setAttribute("login",loginVo);
        return Result.succeed("登陆成功");
    }

    @PostMapping("/get/id")
    @CheckLogin(message = "请先登陆，才能查询")
    public Result getById(@RequestParam @NotNull Integer id){
        log.info("id = {}",id);
        return Result.succeed("查询成功");
    }


    @PostMapping("/logout")
    public Result logout(HttpSession session){
        //销毁session
        session.invalidate();
        return Result.succeed("退出成功");
    }
}
```
#### 4.1.2、效果
1、未登陆的情况下调用查询接口
![image-20240827121621419.png](https://cdn.nlark.com/yuque/0/2024/png/29648551/1724735810863-f36e2ec0-16a1-45c7-8b1e-7a596a24a617.png#averageHue=%23fcfcfb&clientId=u71a168dd-9f47-4&from=ui&id=u3d8d8c01&originHeight=489&originWidth=710&originalType=binary&ratio=1.100000023841858&rotation=0&showTitle=false&size=39618&status=done&style=none&taskId=u3edb5aaa-e9b9-41f6-9a85-bb7b1d6d352&title=)
2、登陆成功后调用查询接口
![image-20240827121705814.png](https://cdn.nlark.com/yuque/0/2024/png/29648551/1724735831894-14513c33-e0db-4a39-ad9b-a094b4cb10aa.png#averageHue=%23fcfcfb&clientId=u71a168dd-9f47-4&from=ui&id=u8a725158&originHeight=513&originWidth=683&originalType=binary&ratio=1.100000023841858&rotation=0&showTitle=false&size=38261&status=done&style=none&taskId=u87b9b785-f71b-4730-8181-6ca7d984380&title=)

### 4.2、校验邮箱格式
#### 4.2.1、代码实现
```java
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @PostMapping("/login")
    public Result login(@RequestBody  @VerifyParam LoginVo loginVo, HttpSession session){
        session.setAttribute("login",loginVo);
        return Result.succeed("登陆成功");
    }

    @PostMapping("/email")
    @CheckLogin(message = "请先登陆，添加邮箱")
    public Result addEmail(@RequestParam @Email String email){
        log.info("email = {}",email);
        return Result.succeed(email);
    }


    @PostMapping("/logout")
    public Result logout(HttpSession session){
        //销毁session
        session.invalidate();
        return Result.succeed("退出成功");
    }
}
```

#### 4.2.2、效果
1、邮箱格式不正确的情况
![image-20240827130008691.png](https://cdn.nlark.com/yuque/0/2024/png/29648551/1724735859086-60f287bc-9d97-4388-8ccc-cbcb445808c3.png#averageHue=%23fdfdfc&clientId=u71a168dd-9f47-4&from=ui&id=u2def9c3f&originHeight=563&originWidth=792&originalType=binary&ratio=1.100000023841858&rotation=0&showTitle=false&size=39612&status=done&style=none&taskId=ued32cce0-8f16-432b-8fb4-95aed413336&title=)
![image-20240827130108831.png](https://cdn.nlark.com/yuque/0/2024/png/29648551/1724735874599-2608dfcc-1f67-4492-88ac-5c40136ec295.png#averageHue=%23fdfcfc&clientId=u71a168dd-9f47-4&from=ui&id=ue69259a9&originHeight=563&originWidth=823&originalType=binary&ratio=1.100000023841858&rotation=0&showTitle=false&size=41533&status=done&style=none&taskId=u16a1a04c-fe12-4931-8838-d2a07637960&title=)

2、邮箱格式正确的情况
![image-20240827130203052.png](https://cdn.nlark.com/yuque/0/2024/png/29648551/1724735892067-1f4ba070-fe01-4ed8-bde7-bbc1f7fd0aba.png#averageHue=%23fdfdfc&clientId=u71a168dd-9f47-4&from=ui&id=ubfa8764d&originHeight=628&originWidth=839&originalType=binary&ratio=1.100000023841858&rotation=0&showTitle=false&size=44940&status=done&style=none&taskId=ucd096432-d516-44ed-8483-d75134ffc6c&title=)
### 4.3、校验手机号格式
#### 4.3.1、代码实现
```java
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @PostMapping("/login")
    public Result login(@RequestBody  @VerifyParam LoginVo loginVo, HttpSession session){
        session.setAttribute("login",loginVo);
        return Result.succeed("登陆成功");
    }

    @PostMapping("/phone")
    @CheckLogin(message = "请先登陆，添加手机")
    public Result addPhone(@RequestParam @Phone String phone){
        log.info("phone = {}",phone);
        return Result.succeed(phone);
    }

    @PostMapping("/logout")
    public Result logout(HttpSession session){
        //销毁session
        session.invalidate();
        return Result.succeed("退出成功");
    }
}
```

#### 4.3.2、效果
1、手机号格式不正确的情况
![image-20240827125706539.png](https://cdn.nlark.com/yuque/0/2024/png/29648551/1724735915037-49b2e73d-7896-4f3b-aaf1-1f787143e6f4.png#averageHue=%23fdfdfd&clientId=u71a168dd-9f47-4&from=ui&id=u1bc9cc0a&originHeight=561&originWidth=920&originalType=binary&ratio=1.100000023841858&rotation=0&showTitle=false&size=38747&status=done&style=none&taskId=ued2f1597-0c3b-4a0c-95c8-6ece5c44e33&title=)
![image-20240827125800869.png](https://cdn.nlark.com/yuque/0/2024/png/29648551/1724735929043-19bde24c-8a04-47a9-b589-7359dec93d55.png#averageHue=%23fdfdfc&clientId=u71a168dd-9f47-4&from=ui&id=uab54aa09&originHeight=586&originWidth=871&originalType=binary&ratio=1.100000023841858&rotation=0&showTitle=false&size=44207&status=done&style=none&taskId=u2be548c8-b4fb-4795-b037-9e135db9ad7&title=)

2、手机格式正确的情况
![image-20240827125858759.png](https://cdn.nlark.com/yuque/0/2024/png/29648551/1724735946246-7f190532-4d94-41d4-a5a0-89b0f0fdbc29.png#averageHue=%23fdfdfd&clientId=u71a168dd-9f47-4&from=ui&id=uae7f2ebc&originHeight=472&originWidth=786&originalType=binary&ratio=1.100000023841858&rotation=0&showTitle=false&size=30002&status=done&style=none&taskId=ud5eec55b-969e-4d63-a9cb-a7ee89bbd55&title=)
### 4.4、校验方法参数是对象的情况
#### 4.4.1、代码实现
```java
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @PostMapping("/login")
    public Result login(@RequestBody  @VerifyParam LoginVo loginVo, HttpSession session){
        session.setAttribute("login",loginVo);
        return Result.succeed("登陆成功");
    }

    @PostMapping("/email")
    @CheckLogin(message = "请先登陆，添加邮箱")
    public Result addEmail(@RequestParam @Email String email){
        log.info("email = {}",email);
        return Result.succeed(email);
    }


    @PostMapping("/logout")
    public Result logout(HttpSession session){
        //销毁session
        session.invalidate();
        return Result.succeed("退出成功");
    }
}
```

#### 4.4.2、效果
1、对象的字段内容与自定义注解功能不匹配的情况
![image-20240827130338271.png](https://cdn.nlark.com/yuque/0/2024/png/29648551/1724735970464-6504388b-2297-45fa-97a4-e4c0384574c9.png#averageHue=%23fdfcfc&clientId=u71a168dd-9f47-4&from=ui&id=u66dbe4b4&originHeight=576&originWidth=786&originalType=binary&ratio=1.100000023841858&rotation=0&showTitle=false&size=50091&status=done&style=none&taskId=u872c5aa2-a4b6-48cc-bffd-21537e71174&title=)
![image-20240827130445831.png](https://cdn.nlark.com/yuque/0/2024/png/29648551/1724735987673-7e6c44d9-7acf-406e-a26f-6b911ec6d5f6.png#averageHue=%23fdfcfc&clientId=u71a168dd-9f47-4&from=ui&id=ua098ab9f&originHeight=583&originWidth=846&originalType=binary&ratio=1.100000023841858&rotation=0&showTitle=false&size=52814&status=done&style=none&taskId=u42313476-744e-4e21-aa8b-bb54954555a&title=)

![image-20240827130600900.png](https://cdn.nlark.com/yuque/0/2024/png/29648551/1724736005268-58dad08d-3976-472e-add2-a2d278288ad8.png#averageHue=%23fdfdfc&clientId=u71a168dd-9f47-4&from=ui&id=u38bd4fa8&originHeight=566&originWidth=905&originalType=binary&ratio=1.100000023841858&rotation=0&showTitle=false&size=52648&status=done&style=none&taskId=u04c9d072-fae0-4e37-a122-2c197e6ab77&title=)
![image-20240827130732578.png](https://cdn.nlark.com/yuque/0/2024/png/29648551/1724736027917-3976cc66-eebb-4e02-9600-9a9c05d51303.png#averageHue=%23fdfcfc&clientId=u71a168dd-9f47-4&from=ui&id=ue59f1fec&originHeight=601&originWidth=790&originalType=binary&ratio=1.100000023841858&rotation=0&showTitle=false&size=50491&status=done&style=none&taskId=u34b5c6cf-bc43-41e9-9d78-dd20d901649&title=)

2、对象的字段内容与自定义注解功能匹配的情况
![image-20240827130836133.png](https://cdn.nlark.com/yuque/0/2024/png/29648551/1724736045422-67eca172-3010-45a8-9d5a-88b1da6fbefc.png#averageHue=%23fdfdfd&clientId=u71a168dd-9f47-4&from=ui&id=uece098d0&originHeight=748&originWidth=903&originalType=binary&ratio=1.100000023841858&rotation=0&showTitle=false&size=70770&status=done&style=none&taskId=ub4229104-bf2d-480b-a0fe-9647b44b299&title=)
### 4.5、校验参数或字段不能为空
#### 4.5.1、代码实现
```java
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @PostMapping("/login")
    public Result login(@RequestBody  @VerifyParam LoginVo loginVo, HttpSession session){
        session.setAttribute("login",loginVo);
        return Result.succeed("登陆成功");
    }
}
```
#### 4.5.2、效果
1、参数或字段为空的情况
![image-20240827131058124.png](https://cdn.nlark.com/yuque/0/2024/png/29648551/1724736064428-41739ba3-356c-43c9-8b0e-3132c465133c.png#averageHue=%23fcfcfb&clientId=u71a168dd-9f47-4&from=ui&id=u488e694d&originHeight=549&originWidth=1051&originalType=binary&ratio=1.100000023841858&rotation=0&showTitle=false&size=69102&status=done&style=none&taskId=uce0ee75c-b2d2-48bb-83d9-03304bc9ec2&title=)
2、参数或字段的不为空的情况
![image-20240827131213094.png](https://cdn.nlark.com/yuque/0/2024/png/29648551/1724736084597-f26e8ba9-a4f9-440a-91ac-0d05b023291a.png#averageHue=%23fdfdfc&clientId=u71a168dd-9f47-4&from=ui&id=u514dad66&originHeight=615&originWidth=817&originalType=binary&ratio=1.100000023841858&rotation=0&showTitle=false&size=43363&status=done&style=none&taskId=uff936d82-8717-4188-999b-697069e4db6&title=)

### 4.6、校验密码是否满足规则
#### 4.6.1、代码实现
```java
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @PostMapping("/login")
    public Result login(@RequestBody  @VerifyParam LoginVo loginVo, HttpSession session){
        session.setAttribute("login",loginVo);
        return Result.succeed("登陆成功");
    }

   @PostMapping("/password")
    @CheckLogin(message = "请先登陆，添加密码")
    public Result addPassWord(@RequestParam @PassWord String password){
        log.info("password = {}",password);
        return Result.succeed(password);
    }


    @PostMapping("/logout")
    public Result logout(HttpSession session){
        //销毁session
        session.invalidate();
        return Result.succeed("退出成功");
    }
}
```

#### 4.6.2、效果
1、密码不满足规则的情况
![image-20240827124910493.png](https://cdn.nlark.com/yuque/0/2024/png/29648551/1724736105383-afbabe29-2830-475c-b377-00056c9f0ba3.png#averageHue=%23fdfdfd&clientId=u71a168dd-9f47-4&from=ui&id=u09b825ea&originHeight=616&originWidth=1138&originalType=binary&ratio=1.100000023841858&rotation=0&showTitle=false&size=44571&status=done&style=none&taskId=ua4cd393b-4e1e-4834-becf-1e24318afad&title=)
![image-20240827125011393.png](https://cdn.nlark.com/yuque/0/2024/png/29648551/1724736119951-7109f195-ce4a-4543-8247-c5cb4bc9ddb2.png#averageHue=%23fdfdfd&clientId=u71a168dd-9f47-4&from=ui&id=ubfc8faac&originHeight=527&originWidth=1163&originalType=binary&ratio=1.100000023841858&rotation=0&showTitle=false&size=37950&status=done&style=none&taskId=u49cd8247-67c2-4208-9daa-e18fc168aab&title=)

2、密码满足规则的情况
![image-20240827125106378.png](https://cdn.nlark.com/yuque/0/2024/png/29648551/1724736134884-e4687b95-156a-4b19-ace0-f1658f94af16.png#averageHue=%23fdfdfd&clientId=u71a168dd-9f47-4&from=ui&id=u077f49d8&originHeight=589&originWidth=1233&originalType=binary&ratio=1.100000023841858&rotation=0&showTitle=false&size=51279&status=done&style=none&taskId=u3c1a3709-c398-4aca-b21e-77361b3c24b&title=)
