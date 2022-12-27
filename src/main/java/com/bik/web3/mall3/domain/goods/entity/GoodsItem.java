package com.bik.web3.mall3.domain.goods.entity;

import com.bik.web3.mall3.bean.goods.dto.GoodsItemDTO;
import com.bik.web3.mall3.common.consts.Mall3Const;
import com.bik.web3.mall3.common.utils.ObjectUtils;
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
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "bigint not null comment 'ID，主键'")
    private Long id;

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
     * 是否已卖出,1表示卖出
     */
    @Column(name = "sold", nullable = false, columnDefinition = "tinyint default 0 not null comment '是否已卖出,1表示卖出'")
    private Integer sold = Mall3Const.YesOrNo.NO;

    /**
     * 是否已充值,1表示已充值
     */
    @Column(name = "recharged", nullable = false, columnDefinition = "tinyint default 0 not null comment '是否已充值,1表示卖出'")
    private Integer recharged = Mall3Const.YesOrNo.NO;

    public GoodsItemDTO toValueObject() {
        return ObjectUtils.copy(this, new GoodsItemDTO());
    }
}
