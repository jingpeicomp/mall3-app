package com.bik.web3.mall3.domain.goods.repository;

import com.bik.web3.mall3.domain.goods.entity.GoodsItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author Mingo.Liu
 * @date 2022-12-13
 */
public interface GoodsItemRepository extends JpaRepository<GoodsItem, String>, JpaSpecificationExecutor<GoodsItem> {
}
