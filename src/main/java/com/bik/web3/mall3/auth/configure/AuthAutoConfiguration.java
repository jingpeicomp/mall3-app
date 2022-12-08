package com.bik.web3.mall3.auth.configure;

import com.bik.web3.mall3.auth.authentication.AuthService;
import com.bik.web3.mall3.auth.common.AuthUtils;
import com.bik.web3.mall3.auth.context.AuthContextApi;
import com.bik.web3.mall3.auth.context.AuthContextApiImpl;
import com.bik.web3.mall3.auth.interceptor.AuthClearFilter;
import com.bik.web3.mall3.auth.interceptor.AuthEmployeeAspect;
import com.bik.web3.mall3.auth.interceptor.AuthInterceptor;
import com.bik.web3.mall3.auth.jwt.JwtService;
import com.bik.web3.mall3.auth.login.LoginService;
import com.bik.web3.mall3.auth.session.SessionRedisOperations;
import com.bik.web3.mall3.auth.session.SessionService;
import com.bik.web3.mall3.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.Arrays;

/**
 * 权限自动配置类
 *
 * @author Mingo.Liu
 */
@Configuration
@ConditionalOnProperty(prefix = "mall3.auth", name = "enable", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(AuthProperties.class)
@RequiredArgsConstructor
public class AuthAutoConfiguration {
    private final AuthProperties authProperties;

    @Bean
    @ConditionalOnClass(Filter.class)
    public FilterRegistrationBean<AuthClearFilter> authClearFilter() {
        final FilterRegistrationBean<AuthClearFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthClearFilter());
        String[] urls = Arrays.stream(authProperties.getUrls())
                .map(url -> url.replace("**", "*"))
                .toArray(String[]::new);
        registrationBean.addUrlPatterns(urls);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        return registrationBean;
    }

    @Bean
    public AuthUtils authUtils() {
        return new AuthUtils();
    }

    @Bean
    public JwtService jwtService() {
        return new JwtService(authProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisTemplateFactory redisTemplateFactory() {
        return new RedisTemplateFactory(authProperties);
    }

    @Bean
    public SessionRedisOperations sessionRedisOperations(RedisTemplateFactory redisTemplateFactory) {
        return new SessionRedisOperations(authProperties, redisTemplateFactory);
    }

    @Bean
    public SessionService sessionService(SessionRedisOperations sessionRedisOperations) {
        return new SessionService(authProperties, sessionRedisOperations);
    }

    @Bean
    public LoginService loginService(UserService userService,
                                     JwtService jwtService, SessionService sessionService) {
        return new LoginService(userService, jwtService, sessionService);
    }

    @Bean
    public AuthContextApi authContextApi() {
        return new AuthContextApiImpl();
    }

    @Bean
    public AuthService authService(JwtService jwtService, SessionService sessionService,
                                   UserService userService) {
        return new AuthService(jwtService, sessionService, userService);
    }

    @Bean
    public AuthInterceptor authInterceptor(AuthService authService) {
        return new AuthInterceptor(authService);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthEmployeeAspect authEmployeeAspect() {
        return new AuthEmployeeAspect();
    }

    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnProperty(prefix = "mall3.auth", name = "enable", havingValue = "true", matchIfMissing = true)
    @RequiredArgsConstructor
    public static class AuthWebMvcConfigurer implements WebMvcConfigurer {

        private final AuthInterceptor authInterceptor;

        private final AuthProperties authProperties;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(authInterceptor).addPathPatterns(authProperties.getUrls());
        }
    }
}
