package com.lt.controller;


import com.lt.annotation.*;
import com.lt.annotation.Number;
import com.lt.common.result.Result;
import com.lt.domain.User;
import com.lt.domain.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @PostMapping("/add")
    @CheckLogin(message = "必须先登陆，才能添加用户")
    public Result addUser(@RequestBody @VerifyParam User user){
        return Result.succeed(user);
    }


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


    @PostMapping("/email")
    @CheckLogin(message = "请先登陆，添加邮箱")
    public Result addEmail(@RequestParam @Email String email){
        log.info("email = {}",email);
        return Result.succeed(email);
    }

    @PostMapping("/phone")
    @CheckLogin(message = "请先登陆，添加手机")
    public Result addPhone(@RequestParam @Phone String phone){
        log.info("phone = {}",phone);
        return Result.succeed(phone);
    }


    @PostMapping("/password")
    @CheckLogin(message = "请先登陆，添加密码")
    public Result addPassWord(@RequestParam @PassWord String password){
        log.info("password = {}",password);
        return Result.succeed(password);
    }


    @CheckLogin(message = "必须先登陆，才能查看密码")
    @GetMapping("/get")
    public Result getPassword(@PassWord @RequestParam(defaultValue = "") String  password, @Number @RequestParam Integer age){
        User user = new User();
        user.setPassword(password);
        user.setAge(age);
        return Result.succeed(user);
    }


    @PostMapping("/logout")
    public Result logout(HttpSession session){
        //销毁session
        session.invalidate();
        return Result.succeed("退出成功");
    }
}
