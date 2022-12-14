package com.bik.web3.mall3.domain.stock.entity;

import com.bik.web3.mall3.bean.stock.dto.StockDTO;
import com.bik.web3.mall3.common.enums.DeviceType;
import com.bik.web3.mall3.common.enums.PeriodType;
import com.bik.web3.mall3.common.utils.ObjectUtils;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 仓库实体对象
 *
 * @author Mingo.Liu
 * @date 2022-12-09
 */
@Data
@Entity
@Table(name = "t_stock")
public class Stock implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "bigint not null comment 'ID，主键'")
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, updatable = false, columnDefinition = "bigint not null comment '用户ID'")
    private Long userId;

    /**
     * 品牌名
     */
    @Column(name = "brand", nullable = false, updatable = false, columnDefinition = "varchar(32) not null comment '品牌名'")
    private String brand;

    /**
     * 品牌logo
     */
    @Column(name = "brand_icon", nullable = false, updatable = false, columnDefinition = "varchar(128) not null comment '品牌logo'")
    private String brandIcon;

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
     * 库存数目
     */
    @Column(name = "count", columnDefinition = "int not null comment '库存数目'")
    private Integer count;

    /**
     * 版本号
     */
    @Version
    @Column(name = "version", columnDefinition = "bigint comment '版本号'")
    private Long version;

    public StockDTO toValueObject() {
        return ObjectUtils.copy(this, new StockDTO());
    }
}
