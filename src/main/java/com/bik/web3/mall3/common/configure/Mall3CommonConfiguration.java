package com.bik.web3.mall3.common.configure;

import com.bik.web3.mall3.common.utils.generator.CardIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 公共配置
 *
 * @author Mingo.Liu
 * @date 2022-12-09
 */
@Configuration
public class Mall3CommonConfiguration {
    @Bean
    public CardIdGenerator cardIdGenerator() {
        return new CardIdGenerator(1);
    }
}
