package com.bik.web3.mall3.bean.vcoin.request;

import com.bik.web3.mall3.common.dto.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * Vcoin充值支付请求
 *
 * @author Mingo.Liu
 * @date 2022-12-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VcoinRechargeOrderPayRequest extends BaseRequest {
    /**
     * 充值单ID
     */
    @NotNull(message = "充值单ID不能为空")
    private Long id;

    /**
     * 以太网交易hash
     */
    @NotNull(message = "以太网交易hash不能为空")
    private String txId;
}
