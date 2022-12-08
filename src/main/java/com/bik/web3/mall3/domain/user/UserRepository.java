package com.bik.web3.mall3.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据仓库
 *
 * @author Mingo.Liu
 * @date 2022-12-07
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    /**
     * 根据用户名查询用户
     *
     * @param name 用户名
     * @return 用户信息
     */
    @Query("select t from User t where t.name = :name")
    Optional<User> findByName(String name);

    /**
     * 根据用户Web3钱包地址查询用户
     *
     * @param web3Addr Web3钱包地址
     * @return 用户信息
     */
    @Query("select t from User t where t.pubWeb3Addr = :web3Addr")
    Optional<User> findByPubWeb3Addr(String web3Addr);
}
