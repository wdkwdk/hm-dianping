package com.hmdp.interceptor;

import com.hmdp.dto.UserDTO;
import com.hmdp.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 检查请求上下文中的当前用户是否已登录。
 * @author wdk
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {



    /**
     * 检查当前请求是否存在已登录用户。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UserDTO userDTO = UserHolder.getUser();

        if (userDTO == null) {
            response.setStatus(401);
            log.info("未登录");
            return false;
        }


        return true;


    }

}
