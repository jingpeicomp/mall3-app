package com.bik.web3.mall3.web3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * web3自动配置
 *
 * @author Mingo.Liu
 * @date 2022-12-08
 */
@Configuration
public class Web3AutoConfiguration {
    @Value("${mall3.web3.address}")
    private String web3Address;

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(web3Address));
    }
}
