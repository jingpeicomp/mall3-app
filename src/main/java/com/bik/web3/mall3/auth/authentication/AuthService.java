package com.bik.web3.mall3.auth.authentication;

import com.bik.web3.mall3.auth.jwt.JwtService;
import com.bik.web3.mall3.auth.login.dto.LoginUser;
import com.bik.web3.mall3.auth.session.SessionService;
import com.bik.web3.mall3.bean.user.dto.UserDTO;
import com.bik.web3.mall3.common.exception.Mall3Exception;
import com.bik.web3.mall3.common.exception.ResultCodes;
import com.bik.web3.mall3.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 系统认证和授权服务接口
 *
 * @author Mingo.Liu
 */
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final JwtService jwtService;

    private final SessionService sessionService;

    private final UserService userService;

    /**
     * 通过token认证系统用户信息，认证不通过会抛出异常
     *
     * @param token token
     * @return 系统登陆用户信息
     */
    public LoginUser identify(String token) {
        if (StringUtils.isBlank(token)) {
            throw new Mall3Exception(ResultCodes.TOKEN_GET_ERROR);
        }

        if (!jwtService.validateToken(token)) {
            //token校验失败
            throw new Mall3Exception(ResultCodes.INVALID_TOKEN);
        }

        LoginUser loginUser = sessionService.get(token);
        if (null == loginUser) {
            throw new Mall3Exception(ResultCodes.EXPIRED_TOKEN);
        }
        UserDTO userDTO = userService.queryById(loginUser.getUserId());
        if (null != userDTO) {
            loginUser.setDetail(userDTO);
        }
        return loginUser;
    }

    /**
     * 通过token认证系统用户信息，认证不通过不会抛出异常，返回null
     *
     * @param token token
     * @return 系统登陆用户信息
     */
    public LoginUser identifyNonException(String token) {
        try {
            return identify(token);
        } catch (Exception ignore) {
            return null;
        }
    }
}
