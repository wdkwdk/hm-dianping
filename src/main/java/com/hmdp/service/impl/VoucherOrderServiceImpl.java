package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.ISeckillVoucherService;
import com.hmdp.service.IVoucherOrderService;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 实现优惠券订单相关业务服务。
 *
 * @author wdk
 */
@Service
@Slf4j
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;
    @Resource
    private RedisIdWorker redisIdWorker;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    // Lua脚本
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        // 设置脚本返回类型为Long
        SECKILL_SCRIPT.setResultType(Long.class);
        SECKILL_SCRIPT.setLocation(new ClassPathResource("lua/seckill.lua"));
    }

    //创建线程池用于保存订单
    private static final ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();

    //代理对象
    private static IVoucherOrderService proxy;

    @PostConstruct
    private void init() {
        SECKILL_ORDER_EXECUTOR.submit(new VoucherOrderHandler());
    }

    //内部类,用于处理订单
    private class VoucherOrderHandler implements Runnable {
        String queueName = "stream:orders";

        @Override
        public void run() {
            while (true) {
                try {
                    //从消息队列中读取消息
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "s1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            StreamOffset.create(queueName, ReadOffset.lastConsumed())
                    );
                    //消息队列中没消息则重新读取
                    if (list == null || list.isEmpty()) {
                        continue;
                    }
                    //将读取出的消息转成VoucherOrder
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> value = record.getValue();
                    VoucherOrder voucherOrder = BeanUtil.toBean(value, VoucherOrder.class);
                    //处理订单
                    handleVoucherOrder(voucherOrder);
                    //确认消息
                    stringRedisTemplate.opsForStream().acknowledge(queueName, "g1", record.getId());
                } catch (Exception e) {
                    log.error("处理订单异常", e);
                    //若出现异常则进入pendingList,重新处理
                    handlePendingList();
                }


            }
        }

        //处理异常消息
        private void handlePendingList() {
            while (true) {
                try {
                    //从pending-list中获取消息(ReadOffset设置成0)
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "s1"),
                            StreamReadOptions.empty().count(1),
                            StreamOffset.create(queueName, ReadOffset.from("0"))
                    );

                    if (list == null || list.isEmpty()) {
                        break;
                    }

                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> value = record.getValue();
                    VoucherOrder voucherOrder = BeanUtil.toBean(value, VoucherOrder.class);
                    handleVoucherOrder(voucherOrder);
                    stringRedisTemplate.opsForStream().acknowledge(queueName,"g1",record.getId());
                } catch (Exception e) {
                    log.error("处理pending-list订单异常",e);
                    //如果处理出现异常则重新处理
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }


            }

        }
    }

    private void handleVoucherOrder(VoucherOrder voucherOrder) {
        Long userId = voucherOrder.getUserId();
        RLock lock = redissonClient.getLock("lock:order:" + userId);

        boolean isLocked = lock.tryLock();

        if (!isLocked) {
            log.error("不允许重复下单,userId: {}", userId);
            return;
        }
        try {
            //保存订单
            boolean isSuccess = proxy.createVoucherOrder(voucherOrder);
            if (!isSuccess) {
                log.error("保存订单失败");
            }
        } catch (Exception e) {
            log.error("保存订单失败", e);
        } finally {
            lock.unlock();
        }

    }

    @Override
    public Result secKillVoucher(Long voucherId) {
        String userId = UserHolder.getUser().getId().toString();
        //获取订单id
        long orderId = redisIdWorker.nextId("order");
        //通过lua脚本判断下单是否成功，成功则先在redis扣库存，记录用户，发送消息队列，异步生成订单
        long ret = stringRedisTemplate.execute(SECKILL_SCRIPT, Collections.emptyList(), voucherId.toString(), userId, String.valueOf(orderId));
        if (ret != 0) {
            return Result.fail(ret == 1 ? "库存不足" : "不能重复下单");
        }


        proxy = (IVoucherOrderService) AopContext.currentProxy();
        return Result.ok(orderId);

    }

    @Transactional
    public boolean createVoucherOrder(VoucherOrder voucherOrder) {
        Long voucherId = voucherOrder.getVoucherId();
        Long userId = voucherOrder.getUserId();
        //一人一单
        Integer count = query().eq("user_id", userId).eq("voucher_id", voucherId).count();
        if (count > 0) {
            log.error("用户已购买过该优惠券");
            return false;
        }
        //扣减库存
        boolean success = seckillVoucherService.update().setSql("stock = stock - 1").eq("voucher_id", voucherId).gt("stock", 0).update();
        if (!success) {
            log.error("库存不足");
            return false;
        }

        save(voucherOrder);
        return true;
    }

}
