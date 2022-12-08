package com.bik.web3.mall3.auth.context;

import com.bik.web3.mall3.auth.login.dto.LoginUser;

/**
 * 当前用户上下文容器
 *
 * @author Mingo.Liu
 */
public class AuthContextHolder {
    private static final ThreadLocal<LoginUser> LONGIN_USER_HOLDER = ThreadLocal.withInitial(() -> null);

    private static final ThreadLocal<String> TOKEN_HOLDER = ThreadLocal.withInitial(() -> null);

    /**
     * 获取系统登陆用户信息
     *
     * @return 系统登陆用户信息
     */
    public static LoginUser getLoginUser() {
        return LONGIN_USER_HOLDER.get();
    }

    /**
     * 设置系统上下文登陆用户信息
     *
     * @param loginUser 系统登陆用户信息
     */
    public static void setLoginUser(LoginUser loginUser) {
        LONGIN_USER_HOLDER.set(loginUser);
    }

    /**
     * 获取token信息
     *
     * @return token信息
     */
    public static String getToken() {
        return TOKEN_HOLDER.get();
    }

    /**
     * 设置上下文token信息
     *
     * @param token token信息
     */
    public static void setToken(String token) {
        TOKEN_HOLDER.set(token);
    }

    /**
     * 移除当前登陆用户信息
     */
    public static void remove() {
        LONGIN_USER_HOLDER.remove();
        TOKEN_HOLDER.remove();
    }
}
