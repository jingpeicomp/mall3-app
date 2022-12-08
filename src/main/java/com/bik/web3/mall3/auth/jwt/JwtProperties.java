package com.bik.web3.mall3.auth.jwt;

import lombok.Data;

/**
 * jwt配置参数
 *
 * @author Mingo.Liu
 */
@Data
public class JwtProperties {
    /**
     * 私钥信息
     */
    private String secretKey = "dXV1TDRhaWd2aHhWU2lJITw+KClQc2hzMnM1MlViVT1kbFpMNEAjXiYkeFZTaUlQczUyVWRsWkw0YXdxZTF4N1VJKyU=";

    /**
     * jwt过期时间（秒），默认不过期
     */
    private Integer expiredSeconds = 3600 * 24 * 9999;
}
