package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.dto.Result;
import com.hmdp.entity.VoucherOrder;

/**
 * 定义优惠券订单相关业务服务接口。
 *
 * @author wdk
 */
public interface IVoucherOrderService extends IService<VoucherOrder> {

    /**
     * 创建秒杀卷订单
     *
     * @param voucherId
     * @return
     */
    Result secKillVoucher(Long voucherId);


    boolean createVoucherOrder(VoucherOrder voucherOrder);
}
