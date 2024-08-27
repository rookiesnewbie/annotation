package com.lt.enums;

import lombok.Getter;

@Getter
public enum VerfyRegexEnum {
    NO("","不校验"),
    IP("（[1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}","IP地址"),
    POSITIVE_INTEGER("^[0-9]*[1-9][0-9]*$","整数"),
    NUMBER_LETTER_UNDER_LINE("^\\w+$","不校验"),
    EMAIL("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$","由数字、26个字母或下划线组成的字符串"),
    PHONE("(1[3-9])\\d{9}$","手机号"),
    NOTNULL("\\S+","不能包含空格或空字符"),
    COMMON("^[a-zA-Z0-9_\\u4e00\\u9fa5]+$","数字、字母中文、下划线"),
    PASSWORD("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,18}$","密码只能是数字，字母，特殊字符组成，且长度要在8-18个字符之间"),
    ACCOUNT("^[0-9a-zA-Z_]{1,}$","字母开头，由数字、英文字母或下划线组成"),
    MONEY("^[0-9]+(.[0-9]{1,2})?$","金额"),

    NUMBER("\\d+","只能输入数字")
    ;
    private final String regex;
    private final String desc;

    VerfyRegexEnum(String regex, String desc) {
        this.regex = regex;
        this.desc = desc;
    }

}

