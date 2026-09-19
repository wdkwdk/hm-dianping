package com.hmdp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmdp.entity.Voucher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定义优惠券数据访问接口。
 * @author wdk
 */
public interface VoucherMapper extends BaseMapper<Voucher> {

    /**
     * 查询指定商铺下的优惠券列表。
     */
    List<Voucher> queryVoucherOfShop(@Param("shopId") Long shopId);
}
