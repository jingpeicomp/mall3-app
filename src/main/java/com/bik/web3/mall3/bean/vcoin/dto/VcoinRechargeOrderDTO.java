package com.bik.web3.mall3.bean.vcoin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Vcoin充值订单值对象
 *
 * @author Mingo.Liu
 * @date 2022-12-08
 */
@Data
public class VcoinRechargeOrderDTO implements Serializable {
    /**
     * 流水ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "bigint not null comment 'ID，主键'")
    private Long id;

    /**
     * 账户ID
     */
    private Long accountId;

    /**
     * 金额，整数
     */
    private Long amount;

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
     * 以太坊账户地址
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
}
