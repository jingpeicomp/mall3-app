package com.bik.web3.mall3.api;

import com.bik.web3.mall3.bean.goods.dto.GoodsDTO;
import com.bik.web3.mall3.bean.goods.dto.GoodsItemDTO;
import com.bik.web3.mall3.bean.goods.request.GoodsItemOperateRequest;
import com.bik.web3.mall3.bean.goods.request.GoodsItemTransferRequest;
import com.bik.web3.mall3.bean.goods.request.GoodsSearchRequest;
import com.bik.web3.mall3.common.annotation.ApiDefinition;
import com.bik.web3.mall3.common.dto.BaseResponse;
import com.bik.web3.mall3.common.dto.PageResult;
import com.bik.web3.mall3.domain.goods.GoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    /**
     * 查询上架商品
     *
     * @return 上架商品查询请求
     */
    @ApiDefinition(method = RequestMethod.GET, path = "/{goodsId}/item")
    @ApiOperation(value = "查询上架商品附属卡号", notes = "查询上架商品附属卡号")
    public BaseResponse<List<GoodsItemDTO>> queryItem(@PathVariable Long goodsId) {
        return BaseResponse.success(goodsService.queryItemByGoodsId(goodsId));
    }

    /**
     * 查询nft持有者
     *
     * @param request nft持有者查询请求
     * @return nft持有者
     */
    @ApiDefinition(method = RequestMethod.GET, path = "/nft/owner")
    @ApiOperation(value = "查询上架商品附属卡号", notes = "查询上架商品附属卡号")
    public BaseResponse<String> queryNftOwner(@Valid GoodsItemOperateRequest request) {
        return BaseResponse.success(goodsService.getNftOwner(request));
    }

    /**
     * 转移nft
     *
     * @param request nft转移请求
     * @return 转移结果
     */
    @ApiDefinition(method = RequestMethod.POST, path = "/nft/transfer")
    @ApiOperation(value = "查询上架商品附属卡号", notes = "查询上架商品附属卡号")
    public BaseResponse<Boolean> transfer(@RequestBody @Valid GoodsItemTransferRequest request) {
        goodsService.transferNft(request);
        return BaseResponse.success();
    }

    /**
     * 销毁nft
     *
     * @param request nft销毁请求
     * @return 销毁结果
     */
    @ApiDefinition(method = RequestMethod.POST, path = "/nft/destroy")
    @ApiOperation(value = "销毁nft", notes = "销毁nft")
    public BaseResponse<Boolean> destroy(@RequestBody @Valid GoodsItemOperateRequest request) {
        goodsService.destroyNft(request);
        return BaseResponse.success();
    }
}
