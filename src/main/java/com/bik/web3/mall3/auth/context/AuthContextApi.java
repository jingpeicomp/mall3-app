package com.bik.web3.mall3.auth.context;

import com.bik.web3.mall3.auth.common.AuthUtils;
import com.bik.web3.mall3.auth.login.dto.LoginUser;
import org.apache.commons.lang3.StringUtils;

/**
 * 权限身份API
 *
 * @author Mingo.Liu
 */
public interface AuthContextApi {
    /**
     * 获取用户token
     *
     * @return token
     */
    default String getToken() {
        String token = AuthContextHolder.getToken();
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        return AuthUtils.getToken();
    }

    /**
     * 获取系统当前用户信息，如果用户未登陆，会抛出异常
     *
     * @return 系统用户信息
     */
    LoginUser getLoginUser();

    /**
     * 获取系统当前用户信息，如果用户未登陆，会返回null
     *
     * @return 系统用户信息
     */
    LoginUser getLoginUserNullable();

    /**
     * 用户是否登陆
     *
     * @return 登陆返回true，反之false
     */
    boolean hasLogin();
}
