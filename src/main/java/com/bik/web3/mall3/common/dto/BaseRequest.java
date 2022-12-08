package com.bik.web3.mall3.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * API请求基类
 *
 * @author Mingo.Liu
 */
@ApiModel(description = "API请求基类")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
@Slf4j
public abstract class BaseRequest implements Serializable {
    /**
     * 员工ID，内部使用，无需前端传入，直接从session中获取
     */
    @ApiModelProperty(value = "员工ID，内部使用，无需前端传入，直接从session中获取", hidden = true)
    private Long userId;

    /**
     * 员工姓名，内部使用，无需前端传入，直接从session中获取
     */
    @ApiModelProperty(value = "员工姓名，内部使用，无需前端传入，直接从session中获取", hidden = true)
    private String userName;

    /**
     * web3钱包账户地址
     */
    @ApiModelProperty(value = "web3钱包账户地址，无需前端传入，直接从session中获取", hidden = true)
    private String userPubWeb3Addr;

}
