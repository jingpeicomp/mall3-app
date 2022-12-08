package com.bik.web3.mall3.bean.vcoin.request;

import com.bik.web3.mall3.common.dto.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Vcoin充值单创建请求
 *
 * @author Mingo.Liu
 * @date 2022-12-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VcoinRechargeOrderCreateRequest extends BaseRequest {
    /**
     * 支付Web钱包账户地址
     */
    @NotBlank(message = "付款Web3账户地址不能为空")
    private String fromPubAddress;

    /**
     * 要充值的Vcoin金额
     */
    @NotNull(message = "要充值的Vcoin金额不能为空")
    private Long vcoinAmount;
}
