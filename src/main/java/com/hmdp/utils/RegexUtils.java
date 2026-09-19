package com.hmdp.utils;

import cn.hutool.core.util.StrUtil;

/**
 * 提供手机号、邮箱和验证码格式校验功能。
 * @author wdk
 */
public class RegexUtils {
    /**
     * 判断手机号格式是否无效。
     */
    public static boolean isPhoneInvalid(String phone){
        return mismatch(phone, RegexPatterns.PHONE_REGEX);
    }
    /**
     * 判断邮箱格式是否无效。
     */
    public static boolean isEmailInvalid(String email){
        return mismatch(email, RegexPatterns.EMAIL_REGEX);
    }

    /**
     * 判断验证码是否为空或格式无效。
     */
    public static boolean isCodeInvalid(String code){
        return mismatch(code, RegexPatterns.VERIFY_CODE_REGEX);
    }

    /**
     * 判断字符串是否为空或不符合指定正则表达式。
     */
    private static boolean mismatch(String str, String regex){
        if (StrUtil.isBlank(str)) {
            return true;
        }
        return !str.matches(regex);
    }
}
