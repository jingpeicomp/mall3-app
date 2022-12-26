package com.bik.web3.mall3.bean.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品附属卡信息
 *
 * @author Mingo.Liu
 * @date 2022-12-13
 */
@Data
public class GoodsItemDTO implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    /**
     * 商品ID
     */
    @ApiModelProperty("商品ID")
    private Long goodsId;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private Long userId;
}
