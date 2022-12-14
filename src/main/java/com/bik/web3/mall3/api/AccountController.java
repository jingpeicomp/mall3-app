package com.bik.web3.mall3.api;

import com.bik.web3.mall3.auth.context.AuthContext;
import com.bik.web3.mall3.bean.account.AccountDTO;
import com.bik.web3.mall3.common.annotation.ApiDefinition;
import com.bik.web3.mall3.common.dto.BaseResponse;
import com.bik.web3.mall3.domain.account.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 账户API接口
 *
 * @author Mingo.Liu
 * @date 2022-12-08
 */
@Api(tags = "账户API接口")
@RestController
@RequestMapping(path = "/api/mall3/account")
@Slf4j
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * 获取当前用户账户信息
     *
     * @return 当前用户账户信息
     */
    @ApiDefinition(method = RequestMethod.GET, path = "/me")
    @ApiOperation(value = "获取当前用户账户信息", notes = "获取当前用户账户信息")
    public BaseResponse<AccountDTO> query() {
        return BaseResponse.success(accountService.query(AuthContext.me().getLoginUser().getUserId()));
    }
}
