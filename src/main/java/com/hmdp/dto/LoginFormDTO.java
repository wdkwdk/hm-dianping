package com.hmdp.dto;

import lombok.Data;

/**
 * 封装用户登录请求中的手机号和验证码。
 * @author wdk
 */
@Data
public class LoginFormDTO {
    private String phone;
    private String code;
    private String password;
}
