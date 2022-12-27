package com.bik.web3.mall3.domain.recharge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 充值数据仓库
 *
 * @author Mingo.Liu
 * @date 2022-12-27
 */
public interface RechargeRepository extends JpaRepository<Recharge, Long>, JpaSpecificationExecutor<Recharge> {
}
