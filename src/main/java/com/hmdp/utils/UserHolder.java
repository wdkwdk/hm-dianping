package com.hmdp.utils;

import com.hmdp.dto.UserDTO;

/**
 * 使用 ThreadLocal 保存和清理当前线程的用户信息。
 * @author wdk
 */
public class UserHolder {
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    /**
     * 将用户信息保存到当前线程上下文。
     */
    public static void saveUser(UserDTO user){
        tl.set(user);
    }

    /**
     * 获取当前线程上下文中的用户信息。
     */
    public static UserDTO getUser(){
        return tl.get();
    }

    /**
     * 清理当前线程上下文中的用户信息。
     */
    public static void removeUser(){
        tl.remove();
    }
}
