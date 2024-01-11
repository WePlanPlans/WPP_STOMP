package org.tenten.tentenstomp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    @Bean
    public Executor myPool() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);    // 기본 스레드 수
        threadPoolTaskExecutor.setMaxPoolSize(20);     // 최대 스레드 수
        return threadPoolTaskExecutor;
    }
}
