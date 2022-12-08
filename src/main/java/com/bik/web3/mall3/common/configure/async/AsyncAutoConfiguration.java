package com.bik.web3.mall3.common.configure.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 默认异步线程池配置
 *
 * @author liuzhaoming
 * @date 2022-12-08
 */
@Configuration
@EnableConfigurationProperties(AsyncTaskProperties.class)
@Slf4j
@RequiredArgsConstructor
public class AsyncAutoConfiguration implements AsyncConfigurer {

    private final AsyncTaskProperties defaultAsyncProperties;

    @Override
    @Bean(name = "asyncExecutor")
    public Executor getAsyncExecutor() {
        log.debug("Creating async task executor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(defaultAsyncProperties.getCorePoolSize());
        executor.setMaxPoolSize(defaultAsyncProperties.getMaxPoolSize());
        executor.setQueueCapacity(defaultAsyncProperties.getQueueCapacity());
        executor.setThreadNamePrefix(defaultAsyncProperties.getThreadNamePrefix());
        return new ExceptionHandlingTaskExecutor(executor);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }


}
