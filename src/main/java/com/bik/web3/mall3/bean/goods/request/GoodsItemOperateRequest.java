package com.bik.web3.mall3.bean.goods.request;

import com.bik.web3.mall3.common.dto.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 商品附属卡操作请求
 *
 * @author Mingo.Liu
 * @date 2022-12-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GoodsItemOperateRequest extends BaseRequest {
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
}
