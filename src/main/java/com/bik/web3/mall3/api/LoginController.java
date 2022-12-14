package com.bik.web3.mall3.api;

import com.bik.web3.mall3.auth.context.AuthContext;
import com.bik.web3.mall3.auth.login.LoginService;
import com.bik.web3.mall3.auth.login.dto.LoginResponse;
import com.bik.web3.mall3.auth.login.dto.PwdLoginRequest;
import com.bik.web3.mall3.auth.login.dto.Web3LoginRequest;
import com.bik.web3.mall3.bean.user.dto.UserDTO;
import com.bik.web3.mall3.common.annotation.ApiDefinition;
import com.bik.web3.mall3.common.dto.BaseResponse;
import com.bik.web3.mall3.domain.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Mingo.Liu
 * @date 2022-12-07
 */
@Api(tags = "登陆API接口")
@RestController
@RequestMapping(path = "/api/mall3/auth")
@Slf4j
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    private final UserService userService;

    /**
     * 用户名密码系统登陆
     *
     * @param request 登陆请求
     * @return 登陆结果
     */
    @ApiDefinition(method = RequestMethod.POST, path = "/login/pwd", requiredLogin = false)
    @ApiOperation(value = "用户名密码登陆", notes = "用户名密码登陆")
    public BaseResponse<LoginResponse> loginByPwd(@ApiParam("用户名密码登陆请求") @RequestBody @Valid PwdLoginRequest request) {
        return BaseResponse.success(loginService.loginByPwd(request));
    }

    /**
     * web3系统登陆
     *
     * @param request 登陆请求
     * @return 登陆结果
     */
    @ApiDefinition(method = RequestMethod.POST, path = "/login/web3", requiredLogin = false)
    @ApiOperation(value = "web3系统登陆", notes = "web3系统登陆")
    public BaseResponse<LoginResponse> loginByWeb3(@ApiParam("web3系统登陆请求") @RequestBody @Valid Web3LoginRequest request) {
        return BaseResponse.success(loginService.loginByWeb3(request));
    }

    @ApiDefinition(method = RequestMethod.GET, path = "/nonce", requiredLogin = false)
    @ApiOperation(value = "查询用户随机字符串", notes = "查询用户随机字符串")
    public BaseResponse<String> queryNonce(@RequestParam String pubAddress) {
        return BaseResponse.success(userService.getNonce(pubAddress));
    }

    /**
     * 系统退出登陆
     *
     * @return 退出登陆结果，成功返回true，反之false
     */
    @ApiDefinition(method = RequestMethod.POST, path = "/logout")
    @ApiOperation(value = "系统退出登陆", notes = "系统退出登陆")
    public BaseResponse<Boolean> logout() {
        return BaseResponse.success(loginService.logout());
    }

    /**
     * SaaS系统获取当前登录用户信息
     *
     * @return 登陆结果
     */
    @ApiDefinition(method = RequestMethod.GET, path = "/me")
    @ApiOperation(value = "系统获取当前登录用户信息", notes = "系统获取当前登录用户信息")
    public BaseResponse<UserDTO> me() {
        return BaseResponse.success(userService.queryById(AuthContext.me().getLoginUser().getUserId()));
    }
}
