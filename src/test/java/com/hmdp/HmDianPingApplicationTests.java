package com.hmdp;

import com.hmdp.service.impl.ShopServiceImpl;
import com.hmdp.utils.RedisIdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 验证点评系统 Spring Boot 应用及相关服务。
 *
 * @author wdk
 */
@SpringBootTest
class HmDianPingApplicationTests {


    @Resource
    private ShopServiceImpl shopService;

    @Resource
    private RedisIdWorker redisIdWorker;

    private ExecutorService es = Executors.newFixedThreadPool(500);

    /**
     * 调用商铺服务写入逻辑过期缓存数据。
     */
    @Test
    void testSaveShop() throws InterruptedException {
        shopService.saveShopRedis(1L, 10L);
    }

    @Test
    void testRedisIdWorker() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(300);
        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                long id = redisIdWorker.nextId("test");
                System.out.println("id = " + id);
            }
            latch.countDown();
        };
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 300; i++) {
            es.submit(task);
        }
        latch.await();
        long end = System.currentTimeMillis();

        System.out.println("用时 = " + (end - begin) + "毫秒");
    }
}
