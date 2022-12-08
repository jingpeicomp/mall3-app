package com.bik.web3.mall3.auth.session;

import com.bik.web3.mall3.auth.configure.AuthProperties;
import com.bik.web3.mall3.auth.login.dto.LoginUser;
import com.bik.web3.mall3.common.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Session 服务接口
 *
 * @author Mingo.Liu
 */
@RequiredArgsConstructor
@Slf4j
public class SessionService {
    private final AuthProperties authProperties;

    private final SessionRedisOperations redisOperations;

    /**
     * 创建session
     *
     * @param token     token，用作session标识
     * @param loginUser 登陆用户信息
     * @param setCookie 是否将session标识设置到cookie中，移动端不需要
     */
    public void create(String token, LoginUser loginUser, boolean setCookie) {
        log.info("Create session {} {}", token, loginUser);
        redisOperations.create(token, loginUser);
        if (setCookie) {
            addSessionCookie(token);
        }
    }

    /**
     * 更新session
     *
     * @param token     token，用作session标识
     * @param loginUser 登陆用户信息
     */
    public void update(String token, LoginUser loginUser) {
        log.info("Update session {} {}", token, loginUser);
        loginUser.setRefreshTime(LocalDateTime.now());
        redisOperations.update(token, loginUser);
    }

    /**
     * 刷新session，主要是更新session的有效期
     *
     * @param token token，用作session标识
     */
    public void refresh(String token) {
        log.info("Refresh session {}", token);
        redisOperations.refresh(token);
    }

    /**
     * 获取session 用户
     *
     * @param token token，用作session标识
     * @return session用户信息
     */
    public LoginUser get(String token) {
        return redisOperations.get(token);
    }

    /**
     * 根据token清除指定的session信息
     *
     * @param userId 用户ID
     * @param token  token，用作session标识
     */
    public void remove(Long userId, String token) {
        log.info("Remove session by token and user {} {}", userId, token);
        redisOperations.remove(userId, token);
    }

    /**
     * 根据token清除指定的session信息
     *
     * @param token token，用作session标识
     */
    public void remove(String token) {
        log.info("Remove session only by token {}", token);
        redisOperations.remove(token);
    }

    /**
     * 清除用户所有的session
     *
     * @param userId 用户ID
     */
    public void removeUser(Long userId) {
        log.info("Remove session by user {}", userId);
        redisOperations.removeUser(userId);
    }

    /**
     * 获取系统所有已登陆用户
     *
     * @return 已登录用户ID列表
     */
    public List<Long> queryAllLoggedUsers() {
        return redisOperations.queryAllLoggedUsers();
    }

    /**
     * 清理用户无效的token，需要扫描所有的用户，非常耗时，系统空闲时才能执行
     */
    public void cleanUserTokens() {
        List<Long> userIds = redisOperations.queryAllLoggedUsers();
        userIds.forEach(redisOperations::cleanUserToken);
    }

    /**
     * 清除cookie中的session信息
     */
    public void removeSessionCookie() {
        String cookieName = authProperties.getCookieName();
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        HttpUtils.getResponse().addCookie(cookie);
    }

    /**
     * 将session设置到cookie中
     *
     * @param token token
     */
    public void addSessionCookie(String token) {
        String cookieName = authProperties.getCookieName();
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setMaxAge(authProperties.getSessionAliveSeconds());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        HttpUtils.getResponse().addCookie(cookie);
    }
}
