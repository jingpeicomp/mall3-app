package com.bik.web3.mall3.bean.order.request;

import com.bik.web3.mall3.common.dto.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单创建请求
 *
 * @author Mingo.Liu
 * @date 2022-12-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderCreateRequest extends BaseRequest {
    private Long goodsId;

    private Integer count;

    private Long itemId;

    private String txId;
}
