package com.bik.web3.mall3.bean.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户值对象
 *
 * @author Mingo.Liu
 * @date 2022-12-07
 */
@Data
public class UserDTO implements Serializable {
    @ApiModelProperty("ID")
    private Long id;

    /**
     * web3钱包账户地址
     */
    @ApiModelProperty("web3钱包账户地址")
    private String pubWeb3Addr;

    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String name;


    /**
     * 品牌名
     */
    @ApiModelProperty("品牌名")
    private String brand;

    /**
     * 品牌logo
     */
    @ApiModelProperty("品牌logo")
    private String brandIcon;
}
