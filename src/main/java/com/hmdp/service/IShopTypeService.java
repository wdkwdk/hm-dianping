package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.entity.ShopType;

import java.util.List;

/**
 * 定义商铺类型相关业务服务接口。
 *
 * @author wdk
 */
public interface IShopTypeService extends IService<ShopType> {

    /**
     * 读取商铺类型列表并使用 Redis 缓存结果。
     */
    List<ShopType> getTypeList();
}
