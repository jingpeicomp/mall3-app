package com.bik.web3.mall3.common.utils;

import com.bik.web3.mall3.common.exception.Mall3Exception;
import com.bik.web3.mall3.common.exception.ResultCodes;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.StringTokenizer;

/**
 * http工具类
 *
 * @author Mingo.Liu
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class HttpUtils {
    /**
     * 获取真实的远端IP
     *
     * @param request HttpServletRequest
     * @return ip
     */
    public static String getRealRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        log.debug("X-Real-IP is {}", ip);

        if (StringUtils.isBlank(ip)) {
            ip = request.getHeader("REMOTE-HOST");
            log.debug("REMOTE-HOST is {}", ip);
        }

        if (StringUtils.isBlank(ip)) {
            ip = request.getHeader("x-forwarded-for");
            log.debug("x-forwarded-for is {}", ip);
        }

        if (StringUtils.isBlank(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            log.debug("Proxy-Client-IP is {}", ip);
        }

        if (StringUtils.isBlank(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            log.debug("WL-Proxy-Client-IP is {}", ip);
        }

        if (StringUtils.isBlank(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            log.debug("HTTP_CLIENT_IP is {}", ip);
        }

        if (StringUtils.isBlank(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            log.debug("HTTP_X_FORWARDED_FOR is {}", ip);
        }

        if (StringUtils.isNotBlank(ip)) {
            StringTokenizer st = new StringTokenizer(ip, ",");

            // 多级反向代理时，取第一个IP
            if (st.countTokens() > 1) {
                ip = st.nextToken();
            }
        } else {
            // 未获取到反向代理IP，返回Remote Address
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    /**
     * 获取当前请求的request对象
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == requestAttributes) {
            throw new Mall3Exception(ResultCodes.HTTP_CONTEXT_ERROR);
        } else {
            return requestAttributes.getRequest();
        }
    }

    /**
     * 获取当前请求的response对象
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == requestAttributes) {
            throw new Mall3Exception(ResultCodes.HTTP_CONTEXT_ERROR);
        } else {
            return requestAttributes.getResponse();
        }
    }
}
