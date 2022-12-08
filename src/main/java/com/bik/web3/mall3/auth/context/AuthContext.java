package com.bik.web3.mall3.auth.context;


import com.bik.web3.mall3.common.utils.BeanFactory;

/**
 * 权限上下文
 *
 * @author Mingo.Liu
 */
public class AuthContext {
    /**
     * 获取当前用户身份
     *
     * @return 用户身份信息
     */
    public static AuthContextApi me() {
        return BeanFactory.getBeanNullable(AuthContextApi.class);
    }
}