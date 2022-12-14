package com.bik.web3.mall3.bean.user.request;

import com.bik.web3.mall3.common.dto.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 绑定Web3钱包地址请求
 *
 * @author Mingo.Liu
 * @date 2022-12-14
 */
@ApiModel(description = "绑定Web3钱包地址请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class BindWeb3AddressRequest extends BaseRequest {
    /**
     * We3钱包地址
     */
    @ApiModelProperty("We3钱包地址")
    @NotBlank(message = "We3钱包地址不能为空")
    private String pubAddress;

    /**
     * 签名
     */
    @ApiModelProperty("签名")
    @NotBlank(message = "签名不能为空")
    private String signature;
}
