package com.bik.web3.mall3.auth.configure;

import com.bik.web3.mall3.auth.jwt.JwtProperties;
import lombok.Data;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * 鉴权配置参数
 *
 * @author Mingo.Liu
 */
@ConfigurationProperties(prefix = "mall3.auth")
@Data
public class AuthProperties implements Serializable {
    /**
     * 启用鉴权
     */
    private boolean enable;

    /**
     * 鉴权url路径
     */
    private String[] urls = {"/api/**"};

    /**
     * session存活时间, 默认30天
     */
    private Integer sessionAliveSeconds = 3600 * 24 * 30;

    /**
     * cookie中保存token的属性名称
     */
    private String cookieName = "mall3_session";

    /**
     * http请求携带token的header名称
     */
    private String tokenHeaderName = "Authorization";

    /**
     * http请求中携带token的param名称
     */
    private String tokenParamName = "token";

    /**
     * jwt相关配置参数
     */
    private JwtProperties jwt = new JwtProperties();

    /**
     * session会话redis配置，不支持集群和哨兵模式
     */
    private RedisProperties redis = new RedisProperties();
}
