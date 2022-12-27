package com.bik.web3.mall3.bean.recharge.request;

import com.bik.web3.mall3.common.dto.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 充值搜索请求
 *
 * @author Mingo.Liu
 * @date 2022-12-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RechargeSearchRequest extends PageRequest {
    /**
     * 充值账户ID
     */
    @ApiModelProperty("充值账户ID")
    private Long toUserId;

    /**
     * 品牌名
     */
    @ApiModelProperty("品牌名")
    private String brand;

    /**
     * 销售渠道
     */
    @ApiModelProperty("销售渠道")
    private Integer saleChannel;
}
