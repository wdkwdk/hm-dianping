package com.hmdp.controller;


import com.hmdp.dto.Result;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 处理优惠券秒杀下单请求。
 * @author wdk
 */
@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {
    /**
     * 接收秒杀请求并创建优惠券订单。
     */
    @PostMapping("seckill/{id}")
    public Result seckillVoucher(@PathVariable("id") Long voucherId) {
        return Result.fail("功能未完成");
    }
}
