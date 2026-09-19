package com.hmdp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 点评系统后端的 Spring Boot 应用启动入口。
 * @author wdk
 */
@MapperScan("com.hmdp.mapper")
@SpringBootApplication
public class HmDianPingApplication {

    /**
     * 启动 Spring Boot 应用。
     */
    public static void main(String[] args) {
        SpringApplication.run(HmDianPingApplication.class, args);
    }

}
