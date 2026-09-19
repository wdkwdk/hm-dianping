package com.hmdp;

import com.hmdp.service.impl.ShopServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 验证点评系统 Spring Boot 应用及相关服务。
 * @author wdk
 */
@SpringBootTest
class HmDianPingApplicationTests {


    @Resource
    private ShopServiceImpl shopService;

    /**
     * 调用商铺服务写入逻辑过期缓存数据。
     */
    @Test
    void testSaveShop() throws InterruptedException {
        shopService.saveShopRedis(1L, 10L);
    }

}
