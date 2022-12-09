package com.bik.web3.mall3.domain.stock.repository;

import com.bik.web3.mall3.domain.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 库存数据仓库
 *
 * @author Mingo.Liu
 * @date 2022-12-09
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, Long>, JpaSpecificationExecutor<Stock> {
    /**
     * 根据ID查询库存
     *
     * @param id     库存ID
     * @param userId 用户ID
     * @return 库存
     */
    Optional<Stock> findByIdAndUserId(Long id, Long userId);
}
