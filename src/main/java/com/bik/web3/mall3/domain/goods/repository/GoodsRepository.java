package com.bik.web3.mall3.domain.goods.repository;

import com.bik.web3.mall3.domain.goods.entity.Goods;
import com.bik.web3.mall3.domain.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Mingo.Liu
 * @date 2022-12-13
 */
@Repository
public interface GoodsRepository extends JpaRepository<Goods, Long>, JpaSpecificationExecutor<Goods> {
}
