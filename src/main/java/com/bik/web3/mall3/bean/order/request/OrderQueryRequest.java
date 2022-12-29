package com.bik.web3.mall3.bean.order.request;

import com.bik.web3.mall3.common.dto.BaseRequest;
import com.bik.web3.mall3.common.dto.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单搜索请求
 *
 * @author Mingo.Liu
 * @date 2022-12-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderQueryRequest extends PageRequest {
    private Integer state;

    private String brand;

    /**
     * 周期类型
     */
    @ApiModelProperty("周期类型")
    private Integer periodType;

    /**
     * 设备类型
     */
    @ApiModelProperty("设备类型")
    private Integer deviceType;

    /**
     * 销售途径
     */
    @ApiModelProperty("销售途径")
    private Integer saleChannel;


    /**
     * 是否显示卖家订单
     */
    private boolean showSeller = false;
}
