package com.bik.web3.mall3.domain.goods;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 商品附属信息，卡号
 * @author Mingo.Liu
 * @date 2022-12-09
 */
@Data
@Entity
@Table(name = "t_goods_item")
public class GoodsItem implements Serializable {
}
