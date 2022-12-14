package com.bik.web3.mall3.api;

import com.bik.web3.mall3.bean.stock.dto.StockDTO;
import com.bik.web3.mall3.bean.stock.request.StockSearchRequest;
import com.bik.web3.mall3.bean.stock.request.StockShelveRequest;
import com.bik.web3.mall3.common.annotation.ApiDefinition;
import com.bik.web3.mall3.common.dto.BaseResponse;
import com.bik.web3.mall3.common.dto.PageResult;
import com.bik.web3.mall3.domain.stock.StockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 库存API接口
 *
 * @author Mingo.Liu
 * @date 2022-12-14
 */
@Api(tags = "库存API接口")
@RestController
@RequestMapping(path = "/api/mall3/stock")
@Slf4j
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    /**
     * 查询库存
     *
     * @return 库存查询请求
     */
    @ApiDefinition(method = RequestMethod.GET)
    @ApiOperation(value = "查询库存", notes = "查询库存")
    public BaseResponse<PageResult<StockDTO>> query(@Valid StockSearchRequest request) {
        return BaseResponse.success(stockService.search(request));
    }

    /**
     * 销售商品上架
     *
     * @param stockId 库存ID
     * @param request 商品上架请求
     * @return 上架结果
     */
    @ApiDefinition(method = RequestMethod.POST, path = "/{stockId}/shelve")
    @ApiOperation(value = "销售商品上架", notes = "销售商品上架")
    public BaseResponse<Boolean> shelve(@PathVariable Long stockId, @RequestBody @Valid StockShelveRequest request) {
        request.setStockId(stockId);
        stockService.shelve(request);
        return BaseResponse.success(true);
    }
}
