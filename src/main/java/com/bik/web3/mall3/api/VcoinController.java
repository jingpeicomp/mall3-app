package com.bik.web3.mall3.api;

import com.bik.web3.mall3.bean.vcoin.dto.VcoinRechargeOrderDTO;
import com.bik.web3.mall3.bean.vcoin.request.VcoinRechargeOrderCreateRequest;
import com.bik.web3.mall3.bean.vcoin.request.VcoinRechargeOrderPayRequest;
import com.bik.web3.mall3.bean.vcoin.request.VcoinRechargeQueryRequest;
import com.bik.web3.mall3.common.annotation.ApiDefinition;
import com.bik.web3.mall3.common.dto.BaseResponse;
import com.bik.web3.mall3.common.dto.PageResult;
import com.bik.web3.mall3.domain.vcoin.VcoinRechargeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Vcoin API接口
 *
 * @author Mingo.Liu
 * @date 2022-12-08
 */
@Api(tags = "Vcoin API接口")
@RestController
@RequestMapping(path = "/api/mall3/vcoin")
@Slf4j
@RequiredArgsConstructor
public class VcoinController {
    private final VcoinRechargeService rechargeService;

    /**
     * 创建Vcoin充值单
     *
     * @param request 创建请求
     * @return 充值单信息
     */
    @ApiDefinition(method = RequestMethod.POST, path = "/recharge")
    @ApiOperation(value = "创建Vcoin充值单", notes = "创建Vcoin充值单")
    public BaseResponse<VcoinRechargeOrderDTO> createRecharge(@RequestBody @Valid VcoinRechargeOrderCreateRequest request) {
        return BaseResponse.success(rechargeService.create(request));
    }

    /**
     * 支付Vcoin充值单
     *
     * @param request 支付请求
     * @return 充值单信息
     */
    @ApiDefinition(method = RequestMethod.PUT, path = "/recharge/pay")
    @ApiOperation(value = "支付Vcoin充值单", notes = "支付Vcoin充值单")
    public BaseResponse<VcoinRechargeOrderDTO> payRecharge(@RequestBody @Valid VcoinRechargeOrderPayRequest request) {
        return BaseResponse.success(rechargeService.pay(request));
    }

    /**
     * 分页查询Vcoin充值单
     *
     * @param request 分页查询请求
     * @return 查询结果
     */
    @ApiDefinition(method = RequestMethod.GET, path = "/recharge")
    @ApiOperation(value = "分页查询Vcoin充值单", notes = "分页查询Vcoin充值单")
    @ResponseBody
    public BaseResponse<PageResult<VcoinRechargeOrderDTO>> queryMineRechargeOrder(VcoinRechargeQueryRequest request) {
        return BaseResponse.success(rechargeService.query(request));
    }
}
