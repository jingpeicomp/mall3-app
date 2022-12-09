package com.bik.web3.mall3.domain.goods;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

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
}
