package com.hmdp.service;

import com.hmdp.dto.Result;
import com.hmdp.entity.Voucher;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 定义优惠券相关业务服务接口。
 * @author wdk
 */
public interface IVoucherService extends IService<Voucher> {

    /**
     * 查询指定商铺下的优惠券列表。
     */
    Result queryVoucherOfShop(Long shopId);

    /**
     * 新增秒杀优惠券及其库存信息。
     */
    void addSeckillVoucher(Voucher voucher);
}
