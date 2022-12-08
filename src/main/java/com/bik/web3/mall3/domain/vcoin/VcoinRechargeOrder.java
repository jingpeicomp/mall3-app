package com.bik.web3.mall3.domain.vcoin;

import com.bik.web3.mall3.bean.vcoin.dto.VcoinRechargeOrderDTO;
import com.bik.web3.mall3.common.utils.ObjectUtils;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Vcoin充值单
 *
 * @author Mingo.Liu
 * @date 2022-12-08
 */
@Data
@Entity
@Table(name = "t_vcoin_recharge_order")
public class VcoinRechargeOrder implements Serializable {
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
    @Column(name = "account_id", nullable = false, updatable = false, columnDefinition = "bigint not null comment '账户ID'")
    private Long accountId;

    /**
     * 金额，整数
     */
    @Column(name = "amount", columnDefinition = "bigint not null comment '金额，整数'")
    private Long amount;

    /**
     * 待支付金额
     */
    @Column(name = "pay_amount", columnDefinition = "decimal(20,10) not null comment '待支付金额'")
    private BigDecimal payAmount;

    /**
     * 实际支付金额
     */
    @Column(name = "paid_amount", columnDefinition = "decimal(20,10) comment '实际支付金额'")
    private BigDecimal paidAmount;

    /**
     * 以太坊交易ID
     */
    @Column(name = "tx_id", columnDefinition = "varchar(100) comment '以太坊交易ID'")
    private String txId;

    /**
     * 以太坊收款账户地址
     */
    @Column(name = "eth_pub_addr", columnDefinition = "varchar(64) not null comment '以太坊收款账户地址'")
    private String ethPubAddr;

    /**
     * 以太坊付款账户地址
     */
    @Column(name = "pay_eth_pub_addr", columnDefinition = "varchar(64) not null comment '以太坊付款账户地址'")
    private String payEthPubAddr;

    /**
     * 充值单状态。待支付：1，已支付：2，支付失败：3
     */
    @Column(name = "state", columnDefinition = "tinyint not null comment '充值单状态。待支付：1，已支付：2，支付失败：3'")
    private Integer state;

    /**
     * 充值单创建时间
     */
    @Column(name = "create_time", columnDefinition = "datetime comment '充值单创建时间'")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime createTime;

    /**
     * 支付时间
     */
    @Column(name = "pay_time", columnDefinition = "datetime comment '支付时间'")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime payTime;

    /**
     * 版本号
     */
    @Version
    @Column(name = "version", columnDefinition = "bigint comment '版本号'")
    private Long version;

    public VcoinRechargeOrderDTO toValueObject() {
        return ObjectUtils.copy(this, new VcoinRechargeOrderDTO());
    }
}
