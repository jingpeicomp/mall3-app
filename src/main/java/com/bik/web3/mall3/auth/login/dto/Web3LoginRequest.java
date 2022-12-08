package com.bik.web3.mall3.auth.login.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 系统Web3登陆请求
 *
 * @author Mingo.Liu
 * @date 2022-12-07
 */
@ApiModel(description = "系统Web3登陆请求")
@Data
public class Web3LoginRequest {
    /**
     * We3钱包地址
     */
    @ApiModelProperty("Web3钱包地址")
    @NotBlank(message = "Web3钱包地址不能为空")
    private String pubAddress;

    /**
     * 签名
     */
    @ApiModelProperty("签名")
    @NotBlank(message = "签名不能为空")
    private String signature;

    /**
     * 是否设置cookie
     */
    @ApiModelProperty("是否设置cookie，一般Web需要设置，移动端无需设置cookie，默认设置")
    private boolean setCookie = true;
}
