package com.hmdp.utils;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 封装缓存数据及其逻辑过期时间。
 * @author wdk
 */
@Data
public class RedisData {
    private LocalDateTime expireTime;
    private Object data;
}
