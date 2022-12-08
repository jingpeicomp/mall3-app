package com.bik.web3.mall3.common.configure.async;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 异步线程池配置参数
 *
 * @author Mingo.Liu
 * @date 2022-12-08
 */
@ConfigurationProperties(prefix = "mall3.async")
@Data
public class AsyncTaskProperties {
    /**
     * Set the ThreadPoolExecutor's core pool size.
     * Default is 5.
     */
    protected int corePoolSize = 5;

    /**
     * Set the ThreadPoolExecutor's maximum pool size.
     * Default is 20.
     */
    protected int maxPoolSize = 20;

    /**
     * Set the capacity for the ThreadPoolExecutor's BlockingQueue.
     * Default is 2000.
     */
    protected int queueCapacity = 2000;

    /**
     * Set the name prefix for thread.
     * Default is 'async-'.
     */
    protected String threadNamePrefix = "async-";
}
