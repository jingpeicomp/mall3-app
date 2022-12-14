package com.bik.web3.mall3.bean.stock.request;

import com.bik.web3.mall3.common.dto.PageRequest;
import com.bik.web3.mall3.common.enums.DeviceType;
import com.bik.web3.mall3.common.enums.PeriodType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存搜索请求
 *
 * @author Mingo.Liu
 * @date 2022-12-14
 */
@ApiModel(description = "库存搜索请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class StockSearchRequest extends PageRequest {
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
}
