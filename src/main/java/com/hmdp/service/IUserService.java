package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;

import javax.servlet.http.HttpSession;

/**
 * 定义用户相关业务服务接口。
 * @author wdk
 */
public interface IUserService extends IService<User> {

    /**
     * 生成并发送手机验证码，同时保存验证码状态。
     */
    Result sendCode(String phone, HttpSession session);

    /**
     * 校验登录信息并建立用户登录状态。
     */
    Result login(LoginFormDTO loginForm, HttpSession session);
}
