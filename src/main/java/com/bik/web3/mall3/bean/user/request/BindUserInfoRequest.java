package com.bik.web3.mall3.bean.user.request;

import com.bik.web3.mall3.common.dto.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 绑定用户账户信息请求
 *
 * @author Mingo.Liu
 * @date 2022-12-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BindUserInfoRequest extends BaseRequest {
    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String name;

    /**
     * 密码
     */
    @ApiModelProperty("密码")
    private String password;

    /**
     * 重复密码
     */
    @ApiModelProperty("重复密码")
    private String rePassword;

    /**
     * 品牌名
     */
    @ApiModelProperty("品牌名")
    private String brand;

    /**
     * 品牌logo
     */
    @ApiModelProperty("品牌logo")
    private String brandIcon;
}
