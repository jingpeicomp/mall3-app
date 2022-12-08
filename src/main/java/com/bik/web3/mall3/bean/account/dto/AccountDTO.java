package com.bik.web3.mall3.bean.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 账户信息值对象
 *
 * @author Mingo.Liu
 * @date 2022-12-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO implements Serializable {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * Vcoin账户余额
     */
    private Long balanceAmount;
}
