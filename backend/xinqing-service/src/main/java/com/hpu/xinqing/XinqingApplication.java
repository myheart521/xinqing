package com.hpu.xinqing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@MapperScan("com.hpu.xinqing.mapper")
@EnableTransactionManagement //开启注解方式的事务管理
@SpringBootApplication
@EnableAsync
@EnableScheduling//开启定时任务处理 Spring Task
public class XinqingApplication {
    public static void main(String[] args) {
        SpringApplication.run(XinqingApplication.class, args);
        System.out.println("成功启动了！！！！！！");
    }

}
