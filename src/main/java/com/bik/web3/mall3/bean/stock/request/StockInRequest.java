package com.bik.web3.mall3.bean.stock.request;

import com.bik.web3.mall3.common.dto.BaseRequest;
import com.bik.web3.mall3.domain.goods.entity.Goods;
import com.bik.web3.mall3.domain.order.Order;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品采购入库请求
 *
 * @author Mingo.Liu
 * @date 2022-12-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StockInRequest extends BaseRequest {
    /**
     * 订单实体
     */
    private Order order;

    /**
     * 商品实体
     */
    private Goods goods;
}
