package com.bik.web3.mall3.common.configure.http;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * http客户端自动配置
 *
 * @author Mingo.Liu
 * @date 2022-12-15
 */
@Configuration
@ConditionalOnClass(OkHttpClient.class)
@EnableConfigurationProperties(HttpClientProperties.class)
public class HttpClientConfiguration {

    @Bean
    @ConditionalOnMissingBean(OkHttpClient.class)
    public OkHttpClient okHttpClient(HttpClientProperties httpProperties) {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(httpProperties.getMaxRequests());
        dispatcher.setMaxRequestsPerHost(httpProperties.getMaxRequestsPerHost());
        ConnectionPool connectionPool = new ConnectionPool(httpProperties.getMaxIdleNum(), httpProperties.getAliveSeconds(), TimeUnit.SECONDS);
        return new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .connectTimeout(httpProperties.getConnectTimeoutInSecond(), TimeUnit.SECONDS)
                .writeTimeout(httpProperties.getWriteTimeoutInSecond(), TimeUnit.SECONDS)
                .readTimeout(httpProperties.getReadTimeoutInSecond(), TimeUnit.SECONDS)
                .dispatcher(dispatcher)
                .build();
    }
}
