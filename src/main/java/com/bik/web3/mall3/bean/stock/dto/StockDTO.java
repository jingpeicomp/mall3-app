package com.bik.web3.mall3.bean.stock.dto;

import com.bik.web3.mall3.common.enums.DeviceType;
import com.bik.web3.mall3.common.enums.PeriodType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 库存值对象
 *
 * @author Mingo.Liu
 * @date 2022-12-09
 */
@ApiModel(description = "库存值对象")
@Data
public class StockDTO implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private Long userId;

    /**
     * 品牌名
     */
    @ApiModelProperty("品牌名")
    private String brand;

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
}