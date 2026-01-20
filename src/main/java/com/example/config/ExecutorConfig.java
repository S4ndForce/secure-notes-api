package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ExecutorConfig {

    @Bean(name = "appExecutor")
    public Executor appExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);         // threads always alive
        executor.setMaxPoolSize(8);          // cap on growth
        executor.setQueueCapacity(100);      // tasks before rejecting
        executor.setThreadNamePrefix("app-exec-");
        executor.setTaskDecorator(MdcRunnableWrapper::new);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}