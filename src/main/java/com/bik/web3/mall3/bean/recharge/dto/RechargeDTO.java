package com.bik.web3.mall3.bean.recharge.dto;

import com.bik.web3.mall3.bean.goods.dto.GoodsDTO;
import com.bik.web3.mall3.common.enums.DeviceType;
import com.bik.web3.mall3.common.enums.PeriodType;
import com.bik.web3.mall3.common.enums.SaleChannel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 充值流水值对象
 *
 * @author Mingo.Liu
 * @date 2022-12-27
 */
@Data
public class RechargeDTO implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    /**
     * 卡号ID
     */
    @ApiModelProperty("卡号ID")
    private Long goodsItemId;

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

    /**
     * 充值账户
     */
    @ApiModelProperty("充值账户")
    private Long toUserId;

    /**
     * 充值账户用户名
     */
    @ApiModelProperty("充值账户用户名")
    private String toUserName;

    /**
     * 商品详情
     */
    @ApiModelProperty("商品详情")
    private GoodsDTO goods;

    /**
     * 充值发生时间
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime time;

    /**
     * 销售渠道
     */
    @ApiModelProperty("销售渠道")
    private SaleChannel saleChannel;

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
     * 周期类型
     */
    @ApiModelProperty("周期类型")
    private PeriodType periodType;

    /**
     * 设备类型
     */
    @ApiModelProperty("设备类型")
    private DeviceType deviceType;
}
