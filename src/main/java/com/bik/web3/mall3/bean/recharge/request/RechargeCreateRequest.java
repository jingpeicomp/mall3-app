package com.bik.web3.mall3.bean.recharge.request;

import com.bik.web3.mall3.common.dto.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 充值请求
 *
 * @author Mingo.Liu
 * @date 2022-12-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RechargeCreateRequest extends BaseRequest {
    /**
     * 充值卡号
     */
    @NotNull(message = "充值卡号不能为空")
    private Long itemId;

    /**
     * 充值用户ID
     */
    @NotNull(message = "充值用户ID不能为空")
    private Long toUserId;
}
