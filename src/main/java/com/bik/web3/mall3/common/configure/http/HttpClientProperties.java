package com.bik.web3.mall3.common.configure.http;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * http配置参数
 *
 * @author Mingo.Liu
 * @date 2022-12-15
 */
@ConfigurationProperties(prefix = "pawn.http")
@Data
public class HttpClientProperties {
    /**
     * http connect timeout(seconds)
     */
    private int connectTimeoutInSecond = 10;

    /**
     * http read timeout(seconds)
     */
    private int readTimeoutInSecond = 60;

    /**
     * http write timeout(seconds)
     */
    private int writeTimeoutInSecond = 30;

    /**
     * 最大请求数
     */
    private int maxRequests = 100;

    /**
     * 同一个域名的最大请求数
     */
    private int maxRequestsPerHost = 20;

    /**
     * 最大空闲http连接数
     */
    private int maxIdleNum = 10;

    /**
     * http连接存活时间（秒）
     */
    private int aliveSeconds = 300;
}
