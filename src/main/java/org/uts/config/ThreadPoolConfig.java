package org.uts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @Description 线程池 配置类
 * @Author codBoy
 * @Date 2024/8/19 22:59
 */
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ScheduledExecutorService scheduledExecutorService(){
        //核心线程数 = CPU核数 * 2 + 2
        //这里底层采用的是DelayWorkingQueue,即所有多余的任务会放到该队列，用不到非核心线程数
        return new ScheduledThreadPoolExecutor(10);
    }
}
