package com.lt.common.result;

import lombok.Getter;

/**
 * @author LiTeng
 * @create 2023/7/18
 */
@Getter
public enum ResultCodeEnum {
    UNAUTHORIZED_EXCEPTION(4002, "认证异常"),
    SUCCESS(200,"成功"),
    FAIL(201, "失败"),
    SERVICE_ERROR(2012, "服务异常"),
    DATA_ERROR(204, "数据异常"),
    ILLEGAL_REQUEST(205, "非法请求"),
    REPEAT_SUBMIT(206, "重复提交"),

    LOGIN_AUTH(208, "未登陆"),
    PERMISSION(209, "没有权限"),

    ORDER_PRICE_ERROR(210, "订单商品价格变化"),
    ORDER_STOCK_FALL(204, "订单库存锁定失败"),
    CREATE_ORDER_FAIL(210, "创建订单失败"),

    COUPON_GET(220, "优惠券已经领取"),
    COUPON_LIMIT_GET(221, "优惠券已发放完毕"),

    URL_ENCODE_ERROR( 216, "URL编码失败"),
    ILLEGAL_CALLBACK_REQUEST_ERROR( 217, "非法回调请求"),
    FETCH_ACCESSTOKEN_FAILD( 218, "获取accessToken失败"),
    FETCH_USERINFO_ERROR( 219, "获取用户信息失败"),



    FILE_UPLOAD_FAIL(400,"图片上传失败"),
    AVATAR_UPLOAD_FAIL(400,"头像上传失败"),
    FILE_UPLOAD_NOT_BLANK(400,"上传文件不能为空"),
    EMAIL_NOT_BLANK(400,"邮箱不能为空"),
    EMAIL_NOT_LEGAL(400,"请输入有效的邮箱地址"),
    EMAIL_NOT_EXIST(400,"邮箱不存在"),
    EMAIL_EXIST(400,"该邮箱已存在，请重新注册"),
    EMAIL_SEAD_CODE_FAIL(400,"邮箱验证码发送失败"),
    CODE_INPUT_ERROR(400,"验证码错误,请重新输入"),
    CODE_NOT_BLANK(400,"验证码不能为空"),
    EMAIL_OR_PASSWORD_ERROR(400,"邮箱或密码错误，请输入正确的邮箱和密码"),
    EMAIL_OR_PASSWORD_NOT_BALK(400,"昵称、邮箱或密码不能为空"),
    ACCOUNT_EXIST(400,"此账号已经存在，请勿重新注册"),
    ACCOUNT_NOT_EXIST(400,"该账户不存在"),
    PASSWORD_NOT_STRENGTH(400,"密码长度至少为8个字符,且必须包含大写字母、小写字母、数字和特殊字符中的至少三种"),
    PASSWORD_LENGTH_LE_8(400,"密码长度至少为8个字符"),
    PASSWORD_MUST_CONTAIN(400,"密码必须包含大写字母、小写字母、数字和特殊字符中的至少三种"),


    GET_QQ_TOKEN_FAIL(400,"获取qqToken失败"),
    GET_QQ_OPENID_FAIL(400,"调qq接口获取openID失败"),
    GET_QQ_USERINFO_FAIL(400,"调用qq接口获取用户信息失败"),


    USERNAME_ONT_BLANK(400,"用户名不能为空"),
    USER_STOP_USE(400,"此用户已经被停用"),
    USERNAME_ONT_EXIST(400,"用户不存在"),
    USERNAME_EXIST(400,"用户名已存在"),
    PASSWORD_ONT_BLANK(400,"密码不能为空"),
    AGE_ONT_BLANK(400,"年龄不能为空"),
    PASSWORD_ONT_RIGHT(400,"密码不正确"),
    QUESTION_GROUP_NAME_NOT_BLANK(400,"题目分组不能为空"),
    QUESTION_GROUP_NAME_EXIST(400,"题目分组名已存在"),
    QUESTION_GROUP_HAVE_QUESTION(400,"该题目分组已经分组，不能删除"),
    BOX_ALREADY_TAKE_OUT(400,"该盲盒已经被占用，请选择其他的盲盒"),
    PLAYER_ALREADY_SELECT_BOX(400,"你已经选择盲盒了，无需在重复选择"),
    BOX_NOT_FOUND(400,"盲盒不存在"),

    TOKEN_NOT_INVALID(400,"token无效,请重新登录"),
    LOGIN_PAST(403,"登陆过期，请重新登陆"),
    QUESTION_ID_GROUP_EXIST(400,"该分组下的题目编号已经存在，请修改成其他的编号"),
    QUESTION_DOWNLOAD_EXCEPTION(400,"请选择分组进行导出"),
    QUESTION_DOWNLOAD_EXCEPTION_BY_TITLE(400,"只能按分组进行导出"),
    QUESTION_ID_EXIST(400,"该题目编号已经存在，填写其他的编号"),
    QUESTION_IMPORT_LIST_IS_EMPTY(400, "导入题目数据不能为空！"),
    PLEASE_SELECT_PLAYER(400, "请选择选手"),
    PLEASE_SELECT_ITEM(400, "请选择加分或者减分的选项"),
    PLEASE_SELECT_UPDATE_SOURCE(400, "请选择加分或者减分的选项"),
    PLEASE_UPLOAD_QUESTION_GROUP(400,"上传前请选择分组"),
    UPLOAD_QUESTION_HAVE_GROUP(400,"上传该题目编号已经被占用"),
    UPLOAD_QUESTION_HAVE_COMMON_QUESTION_ID(400,"上传的题目编号有重复，请修改之后在重新上传"),
    UPLOAD_QUESTION_FILE_NOT_BLANK(400,"文件不能为空"),
    DELETED_QUESTION_NOT_EXIST(400,"要删除的题目不存在"),
    PLEASE_SELECT_PLAYER_ID(400,"请选择需要重置的选手"),

    EMAIL_OR_PASSWORD_NOT_BLANK(400,"邮箱或密码不能为空")        ;

    private Integer code;

    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
