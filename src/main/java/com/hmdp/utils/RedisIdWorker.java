package com.hmdp.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 用于生成全局唯一id
 *
 * @author wdk
 */
@Component
public class RedisIdWorker {
    /**
     * 起始时间戳
     */
    private static final long BEGIN_TIMESTAMP = 1704067200L;
    /**
     * 序列号位数
     */
    private static final int COUNT_BITS = 32;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public long nextId(String ketPrefix) {
        LocalDateTime now = LocalDateTime.now();
        long timestamp = Instant.now().getEpochSecond() - BEGIN_TIMESTAMP;
        String date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = stringRedisTemplate.opsForValue().increment("icr:" + ketPrefix + ":" + date);
        return timestamp << COUNT_BITS | count;

    }
}
