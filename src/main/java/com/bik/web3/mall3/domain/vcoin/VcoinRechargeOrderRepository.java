package com.bik.web3.mall3.domain.vcoin;

import com.bik.web3.mall3.domain.vcoin.VcoinRechargeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Vcoin充值单
 * @author Mingo.Liu
 * @date 2022-12-08
 */
@Repository
public interface VcoinRechargeOrderRepository extends JpaRepository<VcoinRechargeOrder, Long>, JpaSpecificationExecutor<VcoinRechargeOrder> {
}
