package com.hmdp.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


/**
 * 封装 Redis 缓存读写、空值缓存和逻辑过期重建操作。
 *
 * @author wdk
 */
@Component
public class CacheClient {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 将对象序列化后按给定有效期写入 Redis。
     */
    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }

    /**
     * 封装数据和逻辑过期时间后写入 Redis。
     */
    public void setWithLogicalExpire(String Key, Object value, Long time, TimeUnit unit) {
        RedisData redisData = new RedisData();
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        redisData.setData(value);
        stringRedisTemplate.opsForValue().set(Key, JSONUtil.toJsonStr(redisData));
    }

    /**
     * 查询缓存和数据库并缓存空结果以防止缓存穿透。
     */
    public <T, ID> T queryWithPassThrough(String keyPrefix, ID id, Class<T> type, Function<ID, T> dbFallBack, Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        //先查缓存
        String s = stringRedisTemplate.opsForValue().get(key);
        //缓存中有数据且不为空
        if (StrUtil.isNotBlank(s)) {
            return JSONUtil.toBean(s, type);
        }
        //缓存中有数据，但为空
        if (s != null) {
            return null;
        }
        //缓存中无数据，查数据库重建缓存
        T t = dbFallBack.apply(id);
        //数据库没查到
        if (t == null) {
            //将空数据写入缓存，防止缓存穿透
            this.set(key, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        //查到数据，将数据写入缓存
        String jsonStr = JSONUtil.toJsonStr(t);
        this.set(key, t, time, unit);
        return t;
    }


    //创建线程池
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    /**
     * 返回缓存数据并在逻辑过期时异步重建缓存。
     */
    public <T, ID> T queryWithLogicalExpire(String lockKeyPrefix, String keyPrefix, ID id, Class<T> type, Function<ID, T> dbFallBack, Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        //先查缓存
        String s = stringRedisTemplate.opsForValue().get(key);
        //缓存为空直接返回null
        if (StrUtil.isBlank(s)) {
            return null;
        }
        //缓存命中，判断有没有过期
        RedisData redisData = JSONUtil.toBean(s, RedisData.class);
        LocalDateTime expireTime = redisData.getExpireTime();
        JSONObject jsonObject = (JSONObject) redisData.getData();
        T t = JSONUtil.toBean(jsonObject, type);
        //如果没过期，直接返回商铺信息
        if (expireTime.isAfter(LocalDateTime.now())) {
            return t;
        }
        //已过期，缓存重建
        String lockKey = lockKeyPrefix + id;
        boolean isLock = tryLock(lockKey);
        if (isLock) {
            //重新判断是否过期
            String s1 = stringRedisTemplate.opsForValue().get(key);
            RedisData redisData1 = JSONUtil.toBean(s1, RedisData.class);
            if (redisData1.getExpireTime().isAfter(LocalDateTime.now())) {
                return t;
            }
            //开启独立线程进行缓存重建
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    T t2 = dbFallBack.apply(id);
                    setWithLogicalExpire(key, t2, time , unit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    //释放锁
                    unlock(lockKey);
                }
            });
        }

        return t;
    }


    /**
     * 尝试获取指定的 Redis 互斥锁。
     */
    private boolean tryLock(String key) {
        Boolean b = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 20, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(b);
    }

    /**
     * 删除指定的 Redis 互斥锁。
     */
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }


}
