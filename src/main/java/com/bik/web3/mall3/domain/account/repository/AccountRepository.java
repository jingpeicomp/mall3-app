package com.bik.web3.mall3.domain.account.repository;

import com.bik.web3.mall3.domain.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 账户数据仓库
 * @author Mingo.Liu
 * @date 2022-12-08
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
}
