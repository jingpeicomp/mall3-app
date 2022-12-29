package com.bik.web3.mall3.domain.order;

import com.bik.web3.mall3.domain.goods.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 订单领域
 * @author Mingo.Liu
 * @date 2022-12-29
 */
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
}
