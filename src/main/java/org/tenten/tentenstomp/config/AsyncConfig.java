package org.tenten.tentenstomp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    @Bean(name = "pathTaskExecutor")
    public Executor myPool() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(20);    // 기본 스레드 수
        threadPoolTaskExecutor.setMaxPoolSize(30);     // 최대 스레드 수
        threadPoolTaskExecutor.setThreadNamePrefix("AsyncThread-");
        return threadPoolTaskExecutor;
    }
}
