package com.bik.web3.mall3.domain.recharge;

import com.bik.web3.contracts.Mall3Goods;
import com.bik.web3.mall3.bean.goods.dto.GoodsDTO;
import com.bik.web3.mall3.bean.goods.dto.GoodsItemDTO;
import com.bik.web3.mall3.bean.recharge.dto.RechargeDTO;
import com.bik.web3.mall3.bean.recharge.request.RechargeCreateRequest;
import com.bik.web3.mall3.bean.recharge.request.RechargeSearchRequest;
import com.bik.web3.mall3.common.consts.Mall3Const;
import com.bik.web3.mall3.common.dto.PageResult;
import com.bik.web3.mall3.common.enums.SaleChannel;
import com.bik.web3.mall3.common.exception.Mall3Exception;
import com.bik.web3.mall3.common.exception.ResultCodes;
import com.bik.web3.mall3.domain.goods.GoodsService;
import com.bik.web3.mall3.web3.Web3Operations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 充值领域服务
 *
 * @author Mingo.Liu
 * @date 2022-12-27
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RechargeService {

    private final GoodsService goodsService;

    private final RechargeRepository repository;

    private final Web3Operations web3Operations;

    /**
     * 搜索充值流水
     *
     * @param request 搜索请求
     * @return 流水搜索结果
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class)
    public PageResult<RechargeDTO> search(RechargeSearchRequest request) {
        Specification<Recharge> spec = buildQuerySpecification(request);
        request.initDefaultSort();
        Page<RechargeDTO> page = repository.findAll(spec, request.toSpringPageRequest())
                .map(Recharge::toValueObject);
        fillGoodsDetail(page);
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    /**
     * 充值
     *
     * @param request 充值请求
     * @return 充值实体对象
     */
    @Transactional(timeout = 100, rollbackFor = Exception.class)
    public RechargeDTO recharge(RechargeCreateRequest request) {
        GoodsItemDTO item = goodsService.queryItemById(request.getItemId())
                .orElseThrow(() -> new Mall3Exception(ResultCodes.DATA_NOT_EXISTS));
        GoodsDTO goods = goodsService.queryById(item.getGoodsId())
                .orElseThrow(() -> new Mall3Exception(ResultCodes.DATA_NOT_EXISTS));
        if (item.getSold() == Mall3Const.YesOrNo.YES) {
            throw new Mall3Exception(ResultCodes.SOLD_GOODS_ITEM);
        }
        if (item.getRecharged() == Mall3Const.YesOrNo.YES) {
            throw new Mall3Exception(ResultCodes.RECHARGED_GOODS_ITEM);
        }

        if (goods.getSaleChannel() == SaleChannel.Web2) {
            if (!Objects.equals(goods.getUserId(), request.getUserId())) {
                throw new Mall3Exception(ResultCodes.OTHER_GOODS_ITEM);
            }
        } else {
            doWeb3Recharge(request, goods);
        }

        goodsService.recharge(item.getGoodsId(), item.getId());
        Recharge recharge = buildRechargeEntity(request, goods, item);
        repository.save(recharge);
        return recharge.toValueObject();
    }

    /**
     * 执行web3智能合约充值相关操作
     *
     * @param request 充值请求
     * @param goods   销售商品
     */
    private void doWeb3Recharge(RechargeCreateRequest request, GoodsDTO goods) {
        if (StringUtils.isBlank(goods.getContractAddress())) {
            throw new Mall3Exception(ResultCodes.PARAMETER_ERROR);
        }

        Mall3Goods mall3Goods = web3Operations.load(goods.getContractAddress());
        try {
            String ownerAddress = mall3Goods.getItemOwner(BigInteger.valueOf(request.getItemId())).send();
            if (StringUtils.isBlank(ownerAddress)) {
                throw new Mall3Exception(ResultCodes.PARAMETER_ERROR);
            }
            if (!ownerAddress.equals(request.getUserPubWeb3Addr())) {
                throw new Mall3Exception(ResultCodes.OTHER_GOODS_ITEM);
            }

            mall3Goods.burn(BigInteger.valueOf(request.getItemId())).send();
        } catch (Exception e) {
            log.error("Web3 recharge error {}", request, e);
            throw new Mall3Exception(ResultCodes.CONTRACT_OPERATION_ERROR);
        }
    }

    /**
     * 构造充值流水实体对象
     *
     * @param request 充值请求
     * @param goods   销售商品
     * @param item    商品附属卡信息
     * @return 充值流水实体对象
     */
    @NotNull
    private Recharge buildRechargeEntity(RechargeCreateRequest request, GoodsDTO goods, GoodsItemDTO item) {
        Recharge recharge = new Recharge();
        recharge.setUserId(request.getUserId());
        recharge.setToUserId(request.getToUserId());
        recharge.setGoodsItemId(item.getId());
        recharge.setGoodsId(goods.getId());
        recharge.setBrand(goods.getBrand());
        recharge.setName(goods.getName());
        recharge.setDeviceType(goods.getDeviceType());
        recharge.setPeriodType(goods.getPeriodType());
        recharge.setSaleChannel(goods.getSaleChannel());
        return recharge;
    }

    /**
     * 构造查询条件
     *
     * @param request 充值流水搜索请求
     * @return 查询规格
     */
    @NotNull
    private Specification<Recharge> buildQuerySpecification(RechargeSearchRequest request) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (null != request.getUserId()) {
                predicates.add(builder.equal(root.get("userId"), request.getUserId()));
            }
            if (null != request.getToUserId()) {
                predicates.add(builder.equal(root.get("toUserId"), request.getToUserId()));
            }
            if (StringUtils.isNotBlank(request.getBrand())) {
                predicates.add(builder.equal(root.get("brand"), request.getBrand()));
            }
            if (null != request.getSaleChannel()) {
                predicates.add(builder.equal(root.get("saleChannel"), SaleChannel.fromValue(request.getSaleChannel())));
            }

            query.where(predicates.toArray(new Predicate[0]));
            return query.getRestriction();
        };
    }

    /**
     * 填充商品详情
     *
     * @param page 充值流水分页信息
     */
    private void fillGoodsDetail(Page<RechargeDTO> page) {
        List<Long> goodsIds = page.getContent().stream()
                .map(RechargeDTO::getGoodsId)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(goodsIds)) {
            Map<Long, GoodsDTO> goodsById = goodsService.queryByIds(goodsIds)
                    .stream()
                    .collect(Collectors.toMap(GoodsDTO::getId, goods -> goods));
            page.getContent().forEach(rechargeDTO -> {
                if (goodsById.containsKey(rechargeDTO.getGoodsId())) {
                    rechargeDTO.setGoods(goodsById.get(rechargeDTO.getGoodsId()));
                }
            });
        }
    }

}
