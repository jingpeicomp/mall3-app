package com.bik.web3.mall3.auth.login.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 系统用户名密码登陆请求
 *
 * @author Mingo.Liu
 */
@ApiModel(description = "系统用户名密码登陆请求")
@Data
public class PwdLoginRequest implements Serializable {
    /**
     * 用户账号名称
     */
    @ApiModelProperty(value = "用户账号名称", required = true)
    @NotBlank(message = "用户账号名不能为空")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码必须包含字母和数字，不少于8位")
    private String password;

    /**
     * 是否设置cookie
     */
    @ApiModelProperty("是否设置cookie，一般Web需要设置，移动端无需设置cookie，默认设置")
    private boolean setCookie = true;
}
