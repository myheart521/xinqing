package com.hpu.xinqing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {
    public static final String IO_POOL="ioPool";

    @Bean(name = IO_POOL)
    public TaskExecutor ioTaskExecutor() {
        // 获取系统的核心数量
        int core = Runtime.getRuntime().availableProcessors();
        int corePoolSize = core*2;

        // 创建线程池
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 设置核心线程数为核心数的两倍
        executor.setCorePoolSize(corePoolSize);

        // 设置最大线程数为核心线程数
        executor.setMaxPoolSize(corePoolSize);

        // 设置队列长度为200
        executor.setQueueCapacity(2000);

        // 设置线程名称前缀
        executor.setThreadNamePrefix("IO-Executor-");

        // 设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 初始化线程池
        executor.initialize();

        return executor;
    }


    /**
     * 文件流处理线程池
     * @return 线程池
     */
    @Bean("fileTaskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);             // 核心线程数
        executor.setMaxPoolSize(20);             // 最大线程数
        executor.setQueueCapacity(100);          // 队列容量
        executor.setThreadNamePrefix("sse-file-"); // 线程名前缀
        executor.initialize();
        return executor;
    }
}
