package com.bik.web3.mall3.common.configure.request;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RequestLog自动配置类
 *
 * @author Mingo.Liu
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "mall3.log.request", name = "enable", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(RequestLogProperties.class)
@RequiredArgsConstructor
public class RequestLogAutoConfiguration {

    private final RequestLogProperties requestLogProperties;

    @Bean
    public FilterRegistrationBean<RequestLogFilter> requestLogFilter() {
        final FilterRegistrationBean<RequestLogFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestLogFilter());
        registrationBean.addInitParameter("NEED_RESULT", String.valueOf(requestLogProperties.isNeedParam()));
        registrationBean.addInitParameter("NEED_PARAM", String.valueOf(requestLogProperties.isNeedParam()));
        registrationBean.addInitParameter("MAX_RESULT_LENGTH", String.valueOf(requestLogProperties.getMaxResultLength()));
        registrationBean.addInitParameter("MAX_BODY_LENGTH", String.valueOf(requestLogProperties.getMaxBodyLength()));
        registrationBean.addInitParameter("EXCLUDE_PATTERNS", requestLogProperties.getExcludePatterns());
        registrationBean.addInitParameter("EXCLUDE_METHODS", requestLogProperties.getExcludeMethods());
        registrationBean.addInitParameter("EXCLUDE_HEADERS", requestLogProperties.getExcludeHeaders());
        registrationBean.addInitParameter("INCLUDE_HEADERS", requestLogProperties.getIncludeHeaders());

        registrationBean.addUrlPatterns(requestLogProperties.getUrlPatterns());
        registrationBean.setOrder(requestLogProperties.getOrder());

        return registrationBean;
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestLogAspect requestLogAspect() {
        return new RequestLogAspect();
    }
}
