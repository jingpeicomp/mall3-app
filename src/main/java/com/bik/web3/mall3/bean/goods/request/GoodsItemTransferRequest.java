package com.bik.web3.mall3.bean.goods.request;

import com.bik.web3.mall3.common.dto.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * web3商品附属卡转让请求
 *
 * @author Mingo.Liu
 * @date 2022-12-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GoodsItemTransferRequest extends BaseRequest {
    /**
     * 销售商品ID
     */
    @NotNull(message = "销售商品ID不能为空")
    private Long goodsId;

    /**
     * 卡号
     */
    @NotNull(message = "卡号不能为空")
    private Long itemId;

    /**
     * web3地址
     */
    @NotBlank(message = "Web3地址不能为空")
    private String toWeb3Address;
}
