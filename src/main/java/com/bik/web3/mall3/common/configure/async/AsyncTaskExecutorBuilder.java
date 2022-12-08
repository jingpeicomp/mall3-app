package com.bik.web3.mall3.common.configure.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * spring 线程池构造器
 *
 * @author Mingo.Liu
 * @date 2022-12-08
 */
@Slf4j
public class AsyncTaskExecutorBuilder {
    /**
     * 异步线程池构造
     *
     * @param asyncTaskProperties 线程池配置参数
     * @return 异步线程池
     */
    public static AsyncTaskExecutor build(AsyncTaskProperties asyncTaskProperties) {
        log.debug("Creating async task executor {}", asyncTaskProperties);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncTaskProperties.getCorePoolSize());
        executor.setMaxPoolSize(asyncTaskProperties.getMaxPoolSize());
        executor.setQueueCapacity(asyncTaskProperties.getQueueCapacity());
        executor.setThreadNamePrefix(asyncTaskProperties.getThreadNamePrefix());
        return new ExceptionHandlingTaskExecutor(executor);
    }
}
