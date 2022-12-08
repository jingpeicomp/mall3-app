package com.bik.web3.mall3.domain.account.entity;

import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 账户流水
 *
 * @author Mingo.Liu
 * @date 2022-12-08
 */
@Data
@Entity
@Table(name = "t_account_bill")
public class AccountBill implements Serializable {
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
    @Column(name = "account_id", nullable = false, updatable = false, columnDefinition = "bigint not null comment '用户ID'")
    private Long accountId;

    /**
     * 流水发生时间
     */
    @Column(name = "time", columnDefinition = "datetime comment '流水发生时间'")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime time = LocalDateTime.now();

    /**
     * 流水类型，充值:1,支付:2,售卖:3
     */
    @Column(name = "type", columnDefinition = "tinyint not null comment '流水类型'")
    private Integer type;

    /**
     * 金额，整数
     */
    @Column(name = "amount", columnDefinition = "bigint not null comment '金额，整数'")
    private Long amount;

    /**
     * 关联ID
     */
    @Column(name = "related_id", columnDefinition = "varchar(32) comment '关联ID'")
    private String relatedId;

    /**
     * 详细信息
     */
    @Column(name = "detail", columnDefinition = "text comment '详细信息'")
    private String detail;

    /**
     * 版本号
     */
    @Version
    @Column(name = "version", columnDefinition = "bigint comment '版本号'")
    private Long version;
}
