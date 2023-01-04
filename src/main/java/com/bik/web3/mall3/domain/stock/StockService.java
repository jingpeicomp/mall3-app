package com.bik.web3.mall3.domain.stock;

import com.bik.web3.mall3.bean.goods.request.GoodsCreateRequest;
import com.bik.web3.mall3.bean.stock.dto.StockDTO;
import com.bik.web3.mall3.bean.stock.request.StockInRequest;
import com.bik.web3.mall3.bean.stock.request.StockSearchRequest;
import com.bik.web3.mall3.bean.stock.request.StockShelveRequest;
import com.bik.web3.mall3.bean.user.dto.UserDTO;
import com.bik.web3.mall3.common.consts.Mall3Const;
import com.bik.web3.mall3.common.dto.PageResult;
import com.bik.web3.mall3.common.enums.CurrencyType;
import com.bik.web3.mall3.common.enums.DeviceType;
import com.bik.web3.mall3.common.enums.PeriodType;
import com.bik.web3.mall3.common.enums.SaleChannel;
import com.bik.web3.mall3.common.exception.Mall3Exception;
import com.bik.web3.mall3.common.exception.ResultCodes;
import com.bik.web3.mall3.common.utils.ObjectUtils;
import com.bik.web3.mall3.domain.goods.GoodsService;
import com.bik.web3.mall3.domain.stock.entity.Stock;
import com.bik.web3.mall3.domain.stock.repository.StockRepository;
import com.bik.web3.mall3.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
     * 库存搜索请求
     *
     * @param request 分页搜索请求
     * @return 分页搜索结果
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class, readOnly = true)
    public PageResult<StockDTO> search(StockSearchRequest request) {
        Specification<Stock> spec = buildQuerySpecification(request);
        request.initDefaultSort();
        Page<StockDTO> page = stockRepository.findAll(spec, request.toSpringPageRequest())
                .map(Stock::toValueObject);
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    /**
     * 商品上架
     *
     * @param request 商品上架请求
     */
    @Transactional(timeout = 100, rollbackFor = Exception.class)
    public void shelve(StockShelveRequest request) {
        Stock stock = stockRepository.findByIdAndUserId(request.getStockId(), request.getUserId())
                .orElseThrow(() -> new Mall3Exception(ResultCodes.DATA_NOT_EXISTS));
        if (stock.getCount() <= request.getCount()) {
            throw new Mall3Exception(ResultCodes.NOT_ENOUGH_STOCK);
        }

        String brand = stock.getBrand();
        if (StringUtils.isBlank(brand) || Objects.equals(brand, Mall3Const.BRAND_PLATFORM)) {
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
        stock.setCount(stock.getCount() - request.getCount());
        stockRepository.save(stock);
    }

    /**
     * 采购入库
     *
     * @param request 库存采购入库请求
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class)
    public void in(StockInRequest request) {
        Specification<Stock> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (null != request.getUserId()) {
                predicates.add(builder.equal(root.get("userId"), request.getUserId()));
            }
            if (null != request.getGoods().getDeviceType()) {
                predicates.add(builder.equal(root.get("deviceType"), request.getGoods().getDeviceType()));
            }
            if (null != request.getGoods().getPeriodType()) {
                predicates.add(builder.equal(root.get("periodType"), request.getGoods().getPeriodType()));
            }
            if (StringUtils.isBlank(request.getGoods().getBrand())) {
                predicates.add(builder.isNull(root.get("brand")));
            } else {
                predicates.add(builder.equal(root.get("brand"), request.getGoods().getBrand()));
            }

            query.where(predicates.toArray(new Predicate[0]));
            return query.getRestriction();
        };

        Optional<Stock> optional = stockRepository.findOne(spec);
        Stock stock;
        if (optional.isPresent()) {
            stock = optional.get();
            stock.setCount(stock.getCount() + request.getOrder().getCount());
        } else {
            stock = new Stock();
            stock.setBrand(request.getGoods().getBrand());
            stock.setBrandIcon(request.getGoods().getImage());
            stock.setUserId(request.getUserId());
            stock.setDeviceType(request.getGoods().getDeviceType());
            stock.setPeriodType(request.getGoods().getPeriodType());
            stock.setCount(request.getOrder().getCount());
        }
        stockRepository.save(stock);
    }

    /**
     * 构造查询条件
     *
     * @param request 库存搜索请求
     * @return 查询规格
     */
    @NotNull
    private Specification<Stock> buildQuerySpecification(StockSearchRequest request) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (null != request.getUserId()) {
                predicates.add(builder.equal(root.get("userId"), request.getUserId()));
            }
            if (null != request.getDeviceType()) {
                predicates.add(builder.equal(root.get("deviceType"), DeviceType.fromValue(request.getDeviceType())));
            }
            if (null != request.getPeriodType()) {
                predicates.add(builder.equal(root.get("periodType"), PeriodType.fromValue(request.getPeriodType())));
            }

            query.where(predicates.toArray(new Predicate[0]));
            return query.getRestriction();
        };
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
        ObjectUtils.copy(request, goodsRequest, true);
        goodsService.create(goodsRequest);
    }
}
