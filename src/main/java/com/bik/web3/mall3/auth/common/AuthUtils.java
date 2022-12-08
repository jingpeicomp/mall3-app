package com.bik.web3.mall3.auth.common;

import com.bik.web3.mall3.auth.configure.AuthProperties;
import com.bik.web3.mall3.common.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 权限模块工具类
 *
 * @author Mingo.Liu
 */
@Slf4j
public final class AuthUtils implements ApplicationListener<ContextRefreshedEvent> {

    private static AuthProperties authProperties;

    /**
     * 获取当前登录用户Token
     *
     * @return token
     */
    public static String getToken() {
        HttpServletRequest request = HttpUtils.getRequest();

        // 1. 优先从param参数中获取token
        String parameterToken = request.getParameter(authProperties.getTokenParamName());
        if (StringUtils.isNotBlank(parameterToken)) {
            return parameterToken;
        }

        // 2. 从header中获取token
        String authToken = request.getHeader(authProperties.getTokenHeaderName());
        if (StringUtils.isNotBlank(authToken)) {
            return authToken;
        }

        // 3. 从cookie中获取token
        String sessionCookieName = authProperties.getCookieName();
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                if (sessionCookieName.equals(cookie.getName()) && StringUtils.isNotBlank(cookie.getValue())) {
                    return cookie.getValue();
                }
            }
        }

        // 获取不到token
        return null;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (null == event.getApplicationContext().getParent()) {
            log.info("AuthUtils application start event is called");
            authProperties = event.getApplicationContext().getBean(AuthProperties.class);
        }
    }
}
