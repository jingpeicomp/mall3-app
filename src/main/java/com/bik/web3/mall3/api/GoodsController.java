package com.bik.web3.mall3.api;

import com.bik.web3.mall3.bean.goods.dto.GoodsDTO;
import com.bik.web3.mall3.bean.goods.request.GoodsSearchRequest;
import com.bik.web3.mall3.common.annotation.ApiDefinition;
import com.bik.web3.mall3.common.dto.BaseResponse;
import com.bik.web3.mall3.common.dto.PageResult;
import com.bik.web3.mall3.domain.goods.GoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 商品API接口
 *
 * @author Mingo.Liu
 * @date 2022-12-14
 */
@Api(tags = "商品API接口")
@RestController
@RequestMapping(path = "/api/mall3/goods")
@Slf4j
@RequiredArgsConstructor
public class GoodsController {
    private final GoodsService goodsService;

    /**
     * 查询上架商品
     *
     * @return 上架商品查询请求
     */
    @ApiDefinition(method = RequestMethod.GET)
    @ApiOperation(value = "查询上架商品", notes = "查询上架商品")
    public BaseResponse<PageResult<GoodsDTO>> query(@Valid GoodsSearchRequest request) {
        return BaseResponse.success(goodsService.search(request));
    }
}
