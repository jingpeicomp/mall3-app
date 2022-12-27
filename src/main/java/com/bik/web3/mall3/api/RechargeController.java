package com.bik.web3.mall3.api;

import com.bik.web3.mall3.bean.recharge.dto.RechargeDTO;
import com.bik.web3.mall3.bean.recharge.request.RechargeCreateRequest;
import com.bik.web3.mall3.bean.recharge.request.RechargeSearchRequest;
import com.bik.web3.mall3.common.annotation.ApiDefinition;
import com.bik.web3.mall3.common.dto.BaseResponse;
import com.bik.web3.mall3.common.dto.PageResult;
import com.bik.web3.mall3.domain.recharge.RechargeService;
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
 * 充值API接口
 *
 * @author Mingo.Liu
 * @date 2022-12-27
 */
@Api(tags = "充值API接口")
@RestController
@RequestMapping(path = "/api/mall3/recharge")
@Slf4j
@RequiredArgsConstructor
public class RechargeController {

    private final RechargeService rechargeService;

    /**
     * 查询充值流水
     *
     * @return 充值流水查询请求
     */
    @ApiDefinition(method = RequestMethod.GET)
    @ApiOperation(value = "查询充值流水", notes = "查询充值流水")
    public BaseResponse<PageResult<RechargeDTO>> query(@Valid RechargeSearchRequest request) {
        return BaseResponse.success(rechargeService.search(request));
    }

    /**
     * 充值
     *
     * @param request 充值请求
     * @return 充值流水
     */
    @ApiDefinition(method = RequestMethod.POST)
    @ApiOperation(value = "充值", notes = "充值")
    public BaseResponse<RechargeDTO> create(@Valid @RequestBody RechargeCreateRequest request) {
        return BaseResponse.success(rechargeService.recharge(request));
    }
}
