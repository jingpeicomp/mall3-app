package com.bik.web3.mall3.domain.goods;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 商品附属信息，卡号
 *
 * @author Mingo.Liu
 * @date 2022-12-09
 */
@Data
@Entity
@Table(name = "t_goods_item")
public class GoodsItem implements Serializable {
    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "varchar(32) not null comment 'ID，主键'")
    private String id;

    /**
     * 商品ID
     */
    @Column(name = "goods_id", nullable = false, updatable = false, columnDefinition = "bigint not null comment '商品ID'")
    private Long goodsId;
}
