package com.bik.web3.mall3.domain.stock.entity;

import com.bik.web3.mall3.common.enums.DeviceType;
import com.bik.web3.mall3.common.enums.PeriodType;
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
     * 品牌名
     */
    private String brand;

    /**
     * 周期类型
     */
    private PeriodType periodType;

    /**
     * 设备类型
     */
    private DeviceType deviceType;

    /**
     * 库存数目
     */
    private Integer count;

    /**
     * 版本号
     */
    @Version
    @Column(name = "version", columnDefinition = "bigint comment '版本号'")
    private Long version;
}