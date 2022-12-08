package com.bik.web3.mall3.bean.vcoin.request;

import com.bik.web3.mall3.common.dto.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 充值单查询请求
 * @author Mingo.Liu
 * @date 2022-12-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VcoinRechargeQueryRequest extends PageRequest {
    private Integer state;
}
