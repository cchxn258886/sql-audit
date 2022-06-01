package com.example.sqlexamine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @Author chenl
 * @Date 2022/4/25 5:17 下午
 * 线程池配置
 * 使用多线程完成range OneEnvAllInstanceSlowLogGet
 * 单例的 直接丢给spring了
 */

@Configuration
public class ThreadPoolConfig {

    @Bean
    public ExecutorService threadPool(){
        return new ThreadPoolExecutor(5,10,20L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
    }

}
