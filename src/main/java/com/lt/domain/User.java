package com.lt.domain;

import com.lt.annotation.Email;
import com.lt.annotation.NotNull;
import com.lt.annotation.PassWord;
import com.lt.annotation.Phone;
import lombok.Data;

import java.io.Serializable;

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
