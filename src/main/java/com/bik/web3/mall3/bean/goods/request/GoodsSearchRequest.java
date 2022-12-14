package com.bik.web3.mall3.bean.goods.request;

import com.bik.web3.mall3.common.dto.PageRequest;
import com.bik.web3.mall3.common.enums.DeviceType;
import com.bik.web3.mall3.common.enums.PeriodType;
import com.bik.web3.mall3.common.enums.SaleChannel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品搜索请求
 *
 * @author Mingo.Liu
 * @date 2022-12-14
 */
@Data
public class GoodsSearchRequest extends PageRequest {
    /**
     * 品牌名
     */
    @ApiModelProperty("品牌名")
    private String brand;

    /**
     * 周期类型
     */
    @ApiModelProperty("周期类型")
    private PeriodType periodType;

    /**
     * 设备类型
     */
    @ApiModelProperty("设备类型")
    private DeviceType deviceType;

    /**
     * 销售渠道
     */
    @ApiModelProperty("销售渠道")
    private SaleChannel saleChannel;
}
