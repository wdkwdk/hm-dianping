package com.hmdp.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.hmdp.utils.CacheClient;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.RedisData;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 实现商铺相关业务服务。
 *
 * @author wdk
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheClient cacheClient;

    /**
     * 根据商铺编号查询缓存或数据库并返回详情。
     */
    @Override
    public Result queryById(Long id) {
//        缓存穿透
//        Shop shop = cacheClient
//                .queryWithPassThrough(RedisConstants.CACHE_SHOP_KEY, id, Shop.class, this::getById, RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);

//        互斥锁解决缓存击穿
//        Shop shop = queryWithMutex(id);

//        逻辑过期解决缓存击穿
        Shop shop = cacheClient
                .queryWithLogicalExpire(RedisConstants.LOCK_SHOP_KEY, RedisConstants.CACHE_SHOP_KEY, id, Shop.class, this::getById, RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);


        if (shop == null) {
            return Result.fail("商户不存在!");
        }
        return Result.ok(shop);


    }


    /**
     * 更新商铺信息并处理对应缓存。
     */
    @Override
    public Result updateShop(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            return Result.fail("id不能为空");
        }
        updateById(shop);

        stringRedisTemplate.delete(RedisConstants.CACHE_SHOP_KEY + id);

        return Result.ok();
    }


    /**
     * 使用互斥锁重建商铺缓存并处理缓存穿透。
     */
    public Shop queryWithMutex(Long id) {
        String key = RedisConstants.CACHE_SHOP_KEY + id;
        //先查缓存
        String s = stringRedisTemplate.opsForValue().get(key);
        //缓存中有数据且不为空
        if (StrUtil.isNotBlank(s)) {
            return JSONUtil.toBean(s, Shop.class);
        }
        //缓存中有数据，但为空
        if (s != null) {
            return null;
        }

        String lockKey = RedisConstants.LOCK_SHOP_KEY + id;
        Shop shop = null;
        try {
            //获取锁失败
            if (!tryLock(lockKey)) {
                Thread.sleep(50);
                return queryWithMutex(id);
            }
            //获取锁成功，再次检测redis缓存是否存在,如果存在则无需中间缓存
            String s1 = stringRedisTemplate.opsForValue().get(key);
            if (StrUtil.isNotBlank(s1)) {
                return JSONUtil.toBean(s1, Shop.class);
            }

            //缓存中无数据，查数据库
            shop = getById(id);

            //模拟重建延时
            Thread.sleep(200);

            //数据库没查到
            if (shop == null) {
                //将空数据写入缓存，防止缓存穿透
                stringRedisTemplate.opsForValue().set(key, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
                return null;
            }
            //查到数据，将数据写入缓存
            String jsonStr = JSONUtil.toJsonStr(shop);
            stringRedisTemplate.opsForValue().set(key, jsonStr, RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            unlock(lockKey);
        }


        return shop;
    }

    /**
     * 将商铺数据和逻辑过期时间写入 Redis。
     */
    public void saveShopRedis(Long id, Long expireSeconds) throws InterruptedException {
        //模拟创建缓存延迟
        Thread.sleep(200);
        Shop shop = getById(id);
        RedisData data = new RedisData();
        data.setData(shop);
        data.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(data));
    }

    /**
     * 尝试获取指定的 Redis 互斥锁。
     */
    private boolean tryLock(String key) {
        Boolean b = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", RedisConstants.LOCK_SHOP_TTL, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(b);
    }

    /**
     * 删除指定的 Redis 互斥锁。
     */
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}
