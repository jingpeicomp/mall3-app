package com.bik.web3.mall3.bean.goods.dto;

import com.bik.web3.mall3.common.enums.CurrencyType;
import com.bik.web3.mall3.common.enums.DeviceType;
import com.bik.web3.mall3.common.enums.PeriodType;
import com.bik.web3.mall3.common.enums.SaleChannel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 销售商品值对象
 *
 * @author Mingo.Liu
 * @date 2022-12-13
 */
@Data
public class GoodsDTO implements Serializable {
    /**
     * ID
     */
    @ApiModelProperty("ID")
    private Long id;

    /**
     * 库存ID
     */
    @ApiModelProperty("库存ID")
    private Long stockId;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private Long userId;

    /**
     * 商品名称
     */
    @ApiModelProperty("商品名称")
    private String name;

    /**
     * 品牌名
     */
    @ApiModelProperty("品牌名")
    private String brand;

    /**
     * 商品图片
     */
    @ApiModelProperty("商品图片")
    private String image;

    /**
     * 周期类型
     */
    @ApiModelProperty("周期类型")
    private PeriodType periodType;

    /**
     * 设备类型
     */
    @ApiModelProperty("设备类型")
    private DeviceType deviceType;

    /**
     * 库存数目
     */
    @ApiModelProperty("库存数目")
    private Integer count;

    /**
     * 货币种类
     */
    @ApiModelProperty("货币种类")
    private CurrencyType currencyType;

    /**
     * 价格
     */
    @ApiModelProperty("价格")
    private BigDecimal price;

    /**
     * 销售渠道
     */
    @ApiModelProperty("销售渠道")
    private SaleChannel saleChannel;

    /**
     * 商品附属卡信息
     */
    @ApiModelProperty("商品附属卡信息")
    private List<GoodsItemDTO> items;
}
