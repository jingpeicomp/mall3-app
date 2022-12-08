package com.bik.web3.mall3.auth.login;


import com.bik.web3.mall3.auth.common.AuthUtils;
import com.bik.web3.mall3.auth.jwt.JwtService;
import com.bik.web3.mall3.auth.login.dto.LoginResponse;
import com.bik.web3.mall3.auth.login.dto.LoginUser;
import com.bik.web3.mall3.auth.login.dto.PwdLoginRequest;
import com.bik.web3.mall3.auth.login.dto.Web3LoginRequest;
import com.bik.web3.mall3.auth.session.SessionService;
import com.bik.web3.mall3.bean.user.dto.UserDTO;
import com.bik.web3.mall3.bean.user.request.VerifyPasswordRequest;
import com.bik.web3.mall3.bean.user.request.VerifyWeb3AddressRequest;
import com.bik.web3.mall3.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统登陆服务接口
 *
 * @author Mingo.Liu
 */
@RequiredArgsConstructor
@Slf4j
public class LoginService {
    private final UserService userService;
    private final JwtService jwtService;
    private final SessionService sessionService;

    /**
     * 系统用户名密码登陆
     *
     * @param request 用户名密码登陆请求
     * @return 登陆响应结果
     */
    public LoginResponse loginByPwd(PwdLoginRequest request) {
        VerifyPasswordRequest verifyRequest = VerifyPasswordRequest
                .builder()
                .password(request.getPassword())
                .name(request.getUsername())
                .build();
        UserDTO user = userService.verifyPassword(verifyRequest);

        return addSession(user, request.isSetCookie());
    }

    /**
     * 系统用户Web3登陆
     *
     * @param request Web3登陆请求
     * @return 登陆响应结果
     */
    public LoginResponse loginByWeb3(Web3LoginRequest request) {
        VerifyWeb3AddressRequest verifyRequest = VerifyWeb3AddressRequest
                .builder()
                .pubAddress(request.getPubAddress())
                .signature(request.getSignature())
                .build();
        UserDTO user = userService.verifyWeb3Addr(verifyRequest);

        return addSession(user, request.isSetCookie());
    }


    /**
     * 系统退出登陆
     *
     * @return 操作结果，成功返回true，反之false
     */
    public boolean logout() {
        String token = AuthUtils.getToken();
        sessionService.remove(token);
        sessionService.removeSessionCookie();
        return true;
    }

    /**
     * 将登陆用户信息添加到Session
     *
     * @param user        已经登陆用户信息
     * @param isSetCookie 是否将token设置到cookie
     * @return 登陆结果
     */
    private LoginResponse addSession(UserDTO user, boolean isSetCookie) {
        Map<String, Object> payload = new HashMap<>(2);
        payload.put("userId", user.getId());
        payload.put("loginTime", System.currentTimeMillis());
        String token = jwtService.generateToken(payload);

        LoginUser loginUser = LoginUser
                .builder()
                .loginTime(LocalDateTime.now())
                .userId(user.getId())
                .refreshTime(LocalDateTime.now())
                .token(token)
                .build();
        sessionService.create(loginUser.getToken(), loginUser, isSetCookie);

        //员工详情不放在session缓存中
        loginUser.setDetail(user);
        return LoginResponse.builder()
                .loginUser(loginUser)
                .token(loginUser.getToken())
                .build();
    }
}
