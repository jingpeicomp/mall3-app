package com.bik.web3.mall3.auth.interceptor;

import com.bik.web3.mall3.auth.context.AuthContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * 清除线程中鉴权缓存信息。用单独的filter，不放在鉴权拦截器AuthInterceptor的postHandle方法，原因如下：
 * 1. interceptor在filter之后执行，这样请求日志RequestLogFilter无法打印当前用户信息
 * 2. AuthClearFilter优先级需要比RequestLogFilter高，这样RequestLogFilter才能打印
 *
 * @author Mingo.Liu
 */
@Slf4j
public class AuthClearFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } finally {
            try {
                AuthContextHolder.remove();
            } catch (Exception e) {
                log.error("Clear auth error", e);
            }
        }
    }
}
