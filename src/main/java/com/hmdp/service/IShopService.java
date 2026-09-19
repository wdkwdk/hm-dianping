package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;

/**
 * 定义商铺相关业务服务接口。
 * @author wdk
 */
public interface IShopService extends IService<Shop> {
    /**
     * 根据商铺编号查询缓存或数据库并返回详情。
     */
    Result queryById(Long id);

    /**
     * 更新商铺信息并处理对应缓存。
     */
    Result updateShop(Shop shop);
}
