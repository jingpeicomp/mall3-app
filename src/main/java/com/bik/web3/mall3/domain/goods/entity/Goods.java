package com.bik.web3.mall3.domain.goods.entity;

import com.bik.web3.mall3.bean.goods.dto.GoodsDTO;
import com.bik.web3.mall3.common.enums.CurrencyType;
import com.bik.web3.mall3.common.enums.DeviceType;
import com.bik.web3.mall3.common.enums.PeriodType;
import com.bik.web3.mall3.common.enums.SaleChannel;
import com.bik.web3.mall3.common.utils.ObjectUtils;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品SKU
 *
 * @author Mingo.Liu
 * @date 2022-12-09
 */
@Data
@Entity
@Table(name = "t_goods")
public class Goods implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "bigint not null comment 'ID，主键'")
    private Long id;

    /**
     * 库存ID
     */
    @Column(name = "stock_id", nullable = false, updatable = false, columnDefinition = "bigint not null comment '库存ID'")
    private Long stockId;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, updatable = false, columnDefinition = "bigint not null comment '用户ID'")
    private Long userId;

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
     * 库存数目
     */
    @Column(name = "count", columnDefinition = "int not null comment '库存数目'")
    private Integer count;

    /**
     * 货币种类
     */
    @Column(name = "currency_type", nullable = false, updatable = false, columnDefinition = "tinyint not null comment '货币种类'")
    @Convert(converter = CurrencyType.EnumConvert.class)
    private CurrencyType currencyType;

    /**
     * 价格
     */
    @Column(name = "price", columnDefinition = "decimal(20,10) comment '价格'")
    private BigDecimal price;

    /**
     * 销售渠道
     */
    @Column(name = "sale_channel", nullable = false, updatable = false, columnDefinition = "tinyint not null comment '销售渠道'")
    @Convert(converter = SaleChannel.EnumConvert.class)
    private SaleChannel saleChannel;

    /**
     * web3 nft智能合约地址
     */
    @Column(name = "contract_address", columnDefinition = "varchar(64) comment 'web3 nft智能合约地址'")
    private String contractAddress;

    /**
     * 版本号
     */
    @Version
    @Column(name = "version", columnDefinition = "bigint comment '版本号'")
    private Long version;

    public GoodsDTO toValueObject() {
        return ObjectUtils.copy(this, new GoodsDTO());
    }
}
