package com.bik.web3.mall3.auth.configure;

import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Optional;

/**
 * redis自动配置，因为session所用的redis和业务redis可能不相同(应该不用同一个)，因此需要注册多个redis
 *
 * @author Mingo.Liu
 */
@RequiredArgsConstructor
public class RedisTemplateFactory {

    private final AuthProperties authProperties;

    private StringRedisTemplate redisTemplate;

    public synchronized StringRedisTemplate getRedisTemplate() {
        if (null == redisTemplate) {
            redisTemplate = buildSessionRedisTemplate(authProperties.getRedis());
        }

        return redisTemplate;
    }

    private StringRedisTemplate buildSessionRedisTemplate(RedisProperties redisProperties) {
        LettuceClientConfiguration clientConfiguration = buildClientConfig(redisProperties);
        RedisStandaloneConfiguration config = buildServerConfig(redisProperties);
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(config, clientConfiguration);
        connectionFactory.afterPropertiesSet();
        return new StringRedisTemplate(connectionFactory);
    }

    /**
     * 构造redis服务器相关配置
     *
     * @param redisProperties redis参数
     * @return redis服务器相关配置
     */
    private RedisStandaloneConfiguration buildServerConfig(RedisProperties redisProperties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHost());
        config.setPort(redisProperties.getPort());
        config.setUsername(redisProperties.getUsername());
        config.setPassword(RedisPassword.of(redisProperties.getPassword()));
        config.setDatabase(redisProperties.getDatabase());
        return config;
    }

    /**
     * 构造redis client相关配置
     *
     * @param redisProperties redis配置属性
     * @return redis client配置
     */
    @SuppressWarnings("rawtypes")
    private LettuceClientConfiguration buildClientConfig(RedisProperties redisProperties) {
        RedisProperties.Pool pool = Optional.ofNullable(redisProperties.getLettuce().getPool())
                .orElse(new RedisProperties.Pool());
        GenericObjectPoolConfig redisPool = new GenericObjectPoolConfig();
        redisPool.setMaxIdle(pool.getMaxIdle());
        redisPool.setMaxTotal(pool.getMaxActive());
        redisPool.setMinIdle(pool.getMinIdle());
        return LettucePoolingClientConfiguration.builder().poolConfig(redisPool).build();
    }
}
