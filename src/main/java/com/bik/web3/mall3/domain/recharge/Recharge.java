package com.bik.web3.mall3.domain.recharge;

import com.bik.web3.mall3.bean.recharge.dto.RechargeDTO;
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
import java.time.LocalDateTime;

/**
 * 充值领域实体对象
 *
 * @author Mingo.Liu
 * @date 2022-12-27
 */
@EntityListeners({AuditingEntityListener.class})
@Data
@Entity
@Table(name = "t_recharge")
public class Recharge implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "bigint not null comment 'ID，主键'")
    private Long id;

    /**
     * 卡号ID
     */
    @Column(name = "goods_item_id", nullable = false, updatable = false, columnDefinition = "bigint not null comment '卡号ID'")
    private Long goodsItemId;

    /**
     * 商品ID
     */
    @Column(name = "goods_id", nullable = false, updatable = false, columnDefinition = "bigint not null comment '商品ID'")
    private Long goodsId;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, updatable = false, columnDefinition = "bigint not null comment '用户ID'")
    private Long userId;

    /**
     * 充值账户
     */
    @Column(name = "to_user_id", nullable = false, updatable = false, columnDefinition = "bigint not null comment '充值账户'")
    private Long toUserId;

    /**
     * 充值发生时间
     */
    @CreatedDate
    @Column(name = "time", columnDefinition = "datetime comment '充值发生时间'")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime time;

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

    public RechargeDTO toValueObject() {
        return ObjectUtils.copy(this, new RechargeDTO());
    }
}
