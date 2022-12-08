package com.bik.web3.mall3.auth.interceptor;

import com.bik.web3.mall3.auth.authentication.AuthService;
import com.bik.web3.mall3.auth.common.AuthUtils;
import com.bik.web3.mall3.auth.context.AuthContextHolder;
import com.bik.web3.mall3.auth.login.dto.LoginUser;
import com.bik.web3.mall3.common.annotation.ApiDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证和权限拦截器
 *
 * @author Mingo.Liu
 */
@RequiredArgsConstructor
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        ApiDefinition apiDefinition = handlerMethod.getMethodAnnotation(ApiDefinition.class);
        if (null == apiDefinition) {
            apiDefinition = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), ApiDefinition.class);
        }
        if (null == apiDefinition) {
            return true;
        }

        //获取token
        String token = null;
        try {
            token = AuthUtils.getToken();
        } catch (Exception ignore) {
        }
        if (StringUtils.isNotBlank(token)) {
            AuthContextHolder.setToken(token);
        }

        //用户认证
        LoginUser loginUser;
        if (apiDefinition.requiredLogin()) {
            loginUser = authService.identify(token);
        } else {
            loginUser = authService.identifyNonException(token);
        }
        if (null != loginUser) {
            AuthContextHolder.setLoginUser(loginUser);
        }

        return true;
    }
}
