package com.bik.web3.mall3.auth.context;

import com.bik.web3.mall3.auth.login.dto.LoginUser;
import com.bik.web3.mall3.common.exception.Mall3Exception;
import com.bik.web3.mall3.common.exception.ResultCodes;
import lombok.RequiredArgsConstructor;


/**
 * 鉴权上下文API实现类
 *
 * @author Mingo.Liu
 */
@RequiredArgsConstructor
public class AuthContextApiImpl implements AuthContextApi {
    /**
     * 获取当前用户信息，如果用户未登陆，会抛出异常
     *
     * @return 用户信息
     */
    @Override
    public LoginUser getLoginUser() {
        LoginUser loginUser = AuthContextHolder.getLoginUser();
        if (null == loginUser) {
            throw new Mall3Exception(ResultCodes.EXPIRED_TOKEN);
        }
        return loginUser;
    }

    /**
     * 获取当前用户信息，如果用户未登陆，会返回null
     *
     * @return 用户信息
     */
    @Override
    public LoginUser getLoginUserNullable() {
        try {
            return getLoginUser();
        } catch (Exception ignore) {
            return null;
        }
    }

    /**
     * 用户是否登陆
     *
     * @return 登陆返回true，反之false
     */
    @Override
    public boolean hasLogin() {
        try {
            return null != getLoginUser();
        } catch (Exception ignore) {
            return false;
        }
    }
}
