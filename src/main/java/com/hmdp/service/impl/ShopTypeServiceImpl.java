package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.hmdp.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 实现商铺类型相关业务服务。
 * @author wdk
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ShopTypeMapper shopTypeMapper;

    /**
     * 读取商铺类型列表并使用 Redis 缓存结果。
     */
    @Override
    public List<ShopType> getTypeList() {

        String s = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_SHOP_TYPE_KEY);
        if (StrUtil.isNotBlank(s)) {
            List<ShopType> list = JSONUtil.toList(s, ShopType.class);
            return list;
        }

        List<ShopType> shopTypeList = shopTypeMapper.selectList(null);
        String jsonStr = JSONUtil.toJsonStr(shopTypeList);
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_TYPE_KEY, jsonStr, RedisConstants.CACHE_SHOP_TYPE_TTL, TimeUnit.MINUTES);
        return shopTypeList;

    }
}
