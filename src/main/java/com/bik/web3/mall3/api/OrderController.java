package com.bik.web3.mall3.api;

import com.bik.web3.mall3.bean.order.dto.OrderDTO;
import com.bik.web3.mall3.bean.order.request.OrderCreateRequest;
import com.bik.web3.mall3.bean.order.request.OrderQueryRequest;
import com.bik.web3.mall3.common.annotation.ApiDefinition;
import com.bik.web3.mall3.common.dto.BaseResponse;
import com.bik.web3.mall3.common.dto.PageResult;
import com.bik.web3.mall3.domain.order.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 订单API接口
 *
 * @author Mingo.Liu
 * @date 2022-12-29
 */
@Api(tags = "订单API接口")
@RestController
@RequestMapping(path = "/api/mall3/order")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 查询订单
     *
     * @return 订单查询请求
     */
    @ApiDefinition(method = RequestMethod.GET)
    @ApiOperation(value = "查询订单", notes = "查询订单")
    public BaseResponse<PageResult<OrderDTO>> query(@Valid OrderQueryRequest request) {
        return BaseResponse.success(orderService.query(request));
    }

    /**
     * 下单
     *
     * @param request 下单请求
     * @return 订单
     */
    @ApiDefinition(method = RequestMethod.POST)
    @ApiOperation(value = "下单", notes = "下单")
    public BaseResponse<OrderDTO> create(@Valid @RequestBody OrderCreateRequest request) {
        return BaseResponse.success(orderService.create(request));
    }
}
