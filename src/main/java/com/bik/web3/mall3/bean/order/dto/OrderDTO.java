package com.bik.web3.mall3.bean.order.dto;

import com.bik.web3.mall3.bean.goods.dto.GoodsDTO;
import com.bik.web3.mall3.bean.user.dto.UserDTO;
import com.bik.web3.mall3.common.enums.DeviceType;
import com.bik.web3.mall3.common.enums.PeriodType;
import com.bik.web3.mall3.common.enums.SaleChannel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单值对象
 *
 * @author Mingo.Liu
 * @date 2022-12-29
 */
@Data
public class OrderDTO implements Serializable {

    private Long id;

    /**
     * 用户ID
     */
    private Long sellerId;

    /**
     * 用户ID
     */
    private Long buyerId;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 购买数目
     */
    private Integer count;

    /**
     * 待支付金额
     */
    private BigDecimal payAmount;

    /**
     * 实际支付金额
     */
    private BigDecimal paidAmount;

    /**
     * 交易GAS（手续费）
     */
    private BigDecimal gasAmount;

    /**
     * 以太坊交易ID
     */
    private String txId;

    /**
     * 以太坊收款账户地址
     */
    private String ethPubAddr;

    /**
     * 以太坊付款账户地址
     */
    private String payEthPubAddr;

    /**
     * 充值单状态。待支付：1，已支付：2，支付失败：3
     */
    private Integer state;

    /**
     * 充值单创建时间
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 支付时间
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime payTime;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 品牌名
     */
    private String brand;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 周期类型
     */
    private PeriodType periodType;

    /**
     * 设备类型
     */
    private DeviceType deviceType;

    /**
     * 销售渠道
     */
    private SaleChannel saleChannel;

    /**
     * 卖家信息
     */
    private UserDTO seller;

    /**
     * 买家信息
     */
    private UserDTO buyer;

    /**
     * 商品信息
     */
    private GoodsDTO goods;
}
