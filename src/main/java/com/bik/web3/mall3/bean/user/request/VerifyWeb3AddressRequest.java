package com.bik.web3.mall3.bean.user.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 校验Web3地址
 *
 * @author Mingo.Liu
 * @date 2022-12-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyWeb3AddressRequest implements Serializable {
    /**
     * We3钱包地址
     */
    private String pubAddress;

    /**
     * 签名
     */
    private String signature;
}
