package com.bik.web3.mall3.api;

import com.bik.web3.mall3.bean.user.dto.UserDTO;
import com.bik.web3.mall3.bean.user.request.BindUserInfoRequest;
import com.bik.web3.mall3.bean.user.request.BindWeb3AddressRequest;
import com.bik.web3.mall3.common.annotation.ApiDefinition;
import com.bik.web3.mall3.common.dto.BaseResponse;
import com.bik.web3.mall3.domain.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户API接口
 *
 * @author Mingo.Liu
 * @date 2022-12-14
 */
@Api(tags = "账户API接口")
@RestController
@RequestMapping(path = "/api/mall3/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 绑定用户信息
     *
     * @param request 请求
     * @return 绑定成功后用户信息
     */
    @ApiDefinition(method = RequestMethod.PUT, path = "/set/info")
    @ApiOperation(value = "绑定用户信息", notes = "绑定用户信息")
    public BaseResponse<UserDTO> bindUserInfo(@RequestBody @Valid BindUserInfoRequest request) {
        return BaseResponse.success(userService.bindUserInfo(request));
    }

    /**
     * 绑定Web3钱包地址
     *
     * @param request 请求
     * @return 绑定成功后用户信息
     */
    @ApiDefinition(method = RequestMethod.PUT, path = "/set/web3")
    @ApiOperation(value = "绑定用户信息", notes = "绑定用户信息")
    public BaseResponse<UserDTO> bindWeb3Address(@RequestBody @Valid BindWeb3AddressRequest request) {
        return BaseResponse.success(userService.bindWeb3Address(request));
    }

    /**
     * 查询所有用户信息
     *
     * @return 所有用户信息
     */
    @ApiDefinition(method = RequestMethod.GET, path = "/all")
    @ApiOperation(value = "查询所有用户信息", notes = "查询所有用户信息")
    public BaseResponse<List<UserDTO>> queryAllUser() {
        return BaseResponse.success(userService.queryAll());
    }
}
