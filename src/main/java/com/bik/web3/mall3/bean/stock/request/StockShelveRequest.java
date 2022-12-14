package com.bik.web3.mall3.bean.stock.request;

import com.bik.web3.mall3.common.dto.BaseRequest;
import com.bik.web3.mall3.common.enums.SaleChannel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 商品上架请求
 *
 * @author Mingo.Liu
 * @date 2022-12-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StockShelveRequest extends BaseRequest {
    /**
     * 库存ID
     */
    @ApiModelProperty("库存ID")
    private Long stockId;

    /**
     * 上架数目
     */
    @ApiModelProperty("上架数目")
    private Integer count;

    /**
     * 销售渠道
     */
    @ApiModelProperty("销售渠道")
    private SaleChannel saleChannel;

    /**
     * 商品名称
     */
    @ApiModelProperty("商品名称")
    private String goodsName;

    /**
     * 商品图片
     */
    @ApiModelProperty("商品图片")
    private String goodsImage;

    /**
     * 商品价格
     */
    @ApiModelProperty("商品价格")
    private BigDecimal price;
}
