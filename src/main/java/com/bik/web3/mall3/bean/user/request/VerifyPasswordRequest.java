package com.bik.web3.mall3.bean.user.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用户密码校验请求
 *
 * @author Mingo.Liu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPasswordRequest implements Serializable {
    /**
     * 账户名
     */
    @ApiModelProperty("账户名")
    @NotBlank(message = "账户名不能为空")
    private String name;

    /**
     * 用户密码
     */
    @ApiModelProperty("用户密码")
    @NotBlank(message = "用户密码不能为空")
    private String password;
}
