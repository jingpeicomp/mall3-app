package com.bik.web3.mall3.domain.order;

import com.bik.web3.mall3.bean.order.dto.OrderDTO;
import com.bik.web3.mall3.common.enums.DeviceType;
import com.bik.web3.mall3.common.enums.PeriodType;
import com.bik.web3.mall3.common.enums.SaleChannel;
import com.bik.web3.mall3.common.utils.ObjectUtils;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单领域对象
 *
 * @author Mingo.Liu
 * @date 2022-12-29
 */
@EntityListeners({AuditingEntityListener.class})
@Data
@Entity
@Table(name = "t_order")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "bigint not null comment 'ID，主键'")
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, updatable = false, columnDefinition = "bigint not null comment '用户ID'")
    private Long sellerId;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, updatable = false, columnDefinition = "bigint not null comment '用户ID'")
    private Long buyerId;

    /**
     * 商品ID
     */
    @Column(name = "goods_id", nullable = false, updatable = false, columnDefinition = "bigint not null comment '商品ID'")
    private Long goodsId;

    /**
     * 购买数目
     */
    @Column(name = "count", columnDefinition = "int not null comment '购买数目'")
    private Integer count;

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
     * 交易GAS（手续费）
     */
    @Column(name = "gas_amount", columnDefinition = "decimal(20,10) comment '交易GAS（手续费）'")
    private BigDecimal gasAmount;

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
    @CreatedDate
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
     * 商品名称
     */
    @Column(name = "name", columnDefinition = "varchar(64) comment '商品名称'")
    private String name;

    /**
     * 品牌名
     */
    @Column(name = "brand", nullable = false, updatable = false, columnDefinition = "varchar(32) not null comment '品牌名'")
    private String brand;

    /**
     * 商品图片
     */
    @Column(name = "image", columnDefinition = "varchar(128) not null comment '商品图片'")
    private String image;

    /**
     * 周期类型
     */
    @Column(name = "period_type", nullable = false, updatable = false, columnDefinition = "tinyint not null comment '周期类型'")
    @Convert(converter = PeriodType.EnumConvert.class)
    private PeriodType periodType;

    /**
     * 设备类型
     */
    @Column(name = "device_type", nullable = false, updatable = false, columnDefinition = "tinyint not null comment '设备类型'")
    @Convert(converter = DeviceType.EnumConvert.class)
    private DeviceType deviceType;

    /**
     * 销售渠道
     */
    @Column(name = "sale_channel", nullable = false, updatable = false, columnDefinition = "tinyint not null comment '销售渠道'")
    @Convert(converter = SaleChannel.EnumConvert.class)
    private SaleChannel saleChannel;

    /**
     * 版本号
     */
    @Version
    @Column(name = "version", columnDefinition = "bigint comment '版本号'")
    private Long version;

    public OrderDTO toValueObject() {
        return ObjectUtils.copy(this, new OrderDTO());
    }
}
