package com.lt.domain.vo;

import com.lt.annotation.NotNull;
import com.lt.annotation.PassWord;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoginVo implements Serializable {

    @NotNull(message = "用户名不能为空")
    private String username;

    @NotNull(message = "密码不能为空")
    private String password;
}
