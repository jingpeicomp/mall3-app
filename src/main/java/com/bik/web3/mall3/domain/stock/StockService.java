package com.bik.web3.mall3.domain.stock;

import com.bik.web3.mall3.bean.goods.request.GoodsCreateRequest;
import com.bik.web3.mall3.bean.stock.request.StockShelveRequest;
import com.bik.web3.mall3.bean.user.dto.UserDTO;
import com.bik.web3.mall3.common.consts.Mall3Const;
import com.bik.web3.mall3.common.enums.CurrencyType;
import com.bik.web3.mall3.common.enums.SaleChannel;
import com.bik.web3.mall3.common.exception.Mall3Exception;
import com.bik.web3.mall3.common.exception.ResultCodes;
import com.bik.web3.mall3.domain.goods.GoodsService;
import com.bik.web3.mall3.domain.stock.entity.Stock;
import com.bik.web3.mall3.domain.stock.repository.StockRepository;
import com.bik.web3.mall3.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 库存领域服务
 *
 * @author Mingo.Liu
 * @date 2022-12-09
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {
    private final StockRepository stockRepository;

    private final UserService userService;

    private final GoodsService goodsService;

    /**
     * 商品上架
     *
     * @param request 商品上架请求
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class)
    public void shelve(StockShelveRequest request) {
        Stock stock = stockRepository.findByIdAndUserId(request.getStockId(), request.getUserId())
                .orElseThrow(() -> new Mall3Exception(ResultCodes.DATA_NOT_EXISTS));
        if (stock.getCount() <= request.getCount()) {
            throw new Mall3Exception(ResultCodes.NOT_ENOUGH_STOCK);
        }

        String brand = stock.getBrand();
        if (StringUtils.isBlank(brand)) {
            UserDTO user = userService.queryById(request.getUserId());
            if (StringUtils.isBlank(user.getBrand())) {
                throw new Mall3Exception(ResultCodes.NOT_BRAND);
            }
            brand = user.getBrand();

            if (StringUtils.isBlank(request.getGoodsImage())) {
                if (StringUtils.isBlank(user.getBrandIcon())) {
                    request.setGoodsImage(Mall3Const.DEFAULT_GOODS_IMAGE_URL);
                } else {
                    request.setGoodsImage(user.getBrandIcon());
                }
            }
        }

        createGoods(request, stock, brand);
    }

    /**
     * 创建上架商品
     *
     * @param request 库存上架请求
     * @param stock   库存
     * @param brand   品牌
     */
    private void createGoods(StockShelveRequest request, Stock stock, String brand) {
        GoodsCreateRequest goodsRequest = new GoodsCreateRequest();

        goodsRequest.setStockId(stock.getId());
        goodsRequest.setUserId(stock.getUserId());
        goodsRequest.setName(request.getGoodsName());
        goodsRequest.setBrand(brand);
        goodsRequest.setImage(request.getGoodsImage());
        goodsRequest.setPeriodType(stock.getPeriodType());
        goodsRequest.setDeviceType(stock.getDeviceType());
        goodsRequest.setCount(request.getCount());
        goodsRequest.setSaleChannel(request.getSaleChannel());
        goodsRequest.setPrice(request.getPrice());
        goodsRequest.setCurrencyType(request.getSaleChannel() == SaleChannel.Web3 ? CurrencyType.ETH_COIN : CurrencyType.VCOIN);
        goodsService.create(goodsRequest);
    }
}
