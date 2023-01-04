package com.bik.web3.mall3.domain.order;

import com.bik.web3.mall3.bean.order.dto.OrderDTO;
import com.bik.web3.mall3.bean.order.request.OrderCreateRequest;
import com.bik.web3.mall3.bean.order.request.OrderQueryRequest;
import com.bik.web3.mall3.bean.stock.request.StockInRequest;
import com.bik.web3.mall3.bean.user.dto.UserDTO;
import com.bik.web3.mall3.common.consts.Mall3Const;
import com.bik.web3.mall3.common.dto.PageResult;
import com.bik.web3.mall3.common.enums.DeviceType;
import com.bik.web3.mall3.common.enums.PeriodType;
import com.bik.web3.mall3.common.enums.SaleChannel;
import com.bik.web3.mall3.common.exception.Mall3Exception;
import com.bik.web3.mall3.common.exception.ResultCodes;
import com.bik.web3.mall3.common.utils.ObjectUtils;
import com.bik.web3.mall3.domain.account.AccountService;
import com.bik.web3.mall3.domain.goods.GoodsService;
import com.bik.web3.mall3.domain.goods.entity.Goods;
import com.bik.web3.mall3.domain.goods.entity.GoodsItem;
import com.bik.web3.mall3.domain.goods.repository.GoodsItemRepository;
import com.bik.web3.mall3.domain.goods.repository.GoodsRepository;
import com.bik.web3.mall3.domain.stock.StockService;
import com.bik.web3.mall3.domain.user.UserService;
import com.bik.web3.mall3.web3.Web3Operations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 订单领域服务
 *
 * @author Mingo.Liu
 * @date 2022-12-29
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final GoodsService goodsService;

    private final StockService stockService;

    private final AccountService accountService;

    private final GoodsRepository goodsRepository;

    private final GoodsItemRepository itemRepository;

    private final OrderRepository orderRepository;

    private final UserService userService;

    private final Web3Operations web3Operations;

    private final TaskScheduler taskScheduler;

    @Qualifier("asyncExecutor")
    private final AsyncTaskExecutor asyncExecutor;

    /**
     * 查询订单请求
     *
     * @param request 订单搜索请求
     * @return 订单分页查询结果
     */
    @Transactional(timeout = 100, rollbackFor = Exception.class, readOnly = true)
    public PageResult<OrderDTO> query(OrderQueryRequest request) {
        Specification<Order> spec = buildQuerySpec(request);
        request.initDefaultSort();
        Page<OrderDTO> page = orderRepository.findAll(spec, request.toSpringPageRequest())
                .map(Order::toValueObject);

        fillUserInfo(page);
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    /**
     * 下单
     *
     * @param request 下单请求
     * @return 订单值对象
     */
    @Transactional(timeout = 100, rollbackFor = Exception.class)
    public OrderDTO create(OrderCreateRequest request) {
        Goods goods = goodsRepository.findById(request.getGoodsId())
                .orElseThrow(() -> new Mall3Exception(ResultCodes.DATA_NOT_EXISTS));
        if (goods.getUserId().equals(request.getUserId())) {
            throw new Mall3Exception(ResultCodes.CANNOT_BUY_OWN_GOODS);
        }

        BigDecimal amount = goods.getPrice().multiply(BigDecimal.valueOf(request.getCount()));
        Order order;
        if (goods.getSaleChannel() == SaleChannel.Web2) {
            goodsService.sell(request.getGoodsId(), request.getCount());
            order = buildWeb2Order(request, goods, amount);
            orderRepository.save(order);
            accountService.pay(order);

            StockInRequest stockInRequest = ObjectUtils.copy(request, new StockInRequest());
            stockInRequest.setOrder(order);
            stockInRequest.setGoods(goods);
            stockService.in(stockInRequest);
        } else {
            GoodsItem item = itemRepository.findById(request.getItemId())
                    .orElseThrow(() -> new Mall3Exception(ResultCodes.DATA_NOT_EXISTS));
            UserDTO userDTO = userService.queryById(goods.getUserId());
            order = buildWeb3Order(request, goods, amount, userDTO);
            orderRepository.save(order);
            item.setSold(Mall3Const.YesOrNo.YES);
            goods.setCount(goods.getCount() - 1);
            itemRepository.save(item);
            goodsRepository.save(goods);

            StockInRequest stockInRequest = ObjectUtils.copy(request, new StockInRequest());
            stockInRequest.setOrder(order);
            stockInRequest.setGoods(goods);
            stockService.in(stockInRequest);

            OrderDTO dto = order.toValueObject();
            dto.setGoods(goods.toValueObject());
            asyncExecutor.execute(() -> checkPayInfo(dto));
        }
        return order.toValueObject();
    }

    /**
     * 构造Web2订单
     *
     * @param request 下单请求
     * @param goods   订单商品
     * @param amount  订单金额
     */
    private Order buildWeb2Order(OrderCreateRequest request, Goods goods, BigDecimal amount) {
        Order order = new Order();
        order.setBuyerId(request.getUserId());
        order.setSellerId(goods.getUserId());
        order.setGoodsId(goods.getId());
        order.setCount(request.getCount());
        order.setPayAmount(amount);
        order.setPaidAmount(amount);
        order.setState(2);
        LocalDateTime now = LocalDateTime.now();
        order.setCreateTime(now);
        order.setPayTime(now);
        order.setName(goods.getName());
        order.setBrand(goods.getBrand());
        order.setImage(goods.getImage());
        order.setPeriodType(goods.getPeriodType());
        order.setDeviceType(goods.getDeviceType());
        order.setSaleChannel(goods.getSaleChannel());
        return order;
    }

    /**
     * 构造Web3订单
     *
     * @param request 下单请求
     * @param goods   订单商品
     * @param amount  订单金额
     * @param userDTO 卖家账户信息
     */
    private Order buildWeb3Order(OrderCreateRequest request, Goods goods, BigDecimal amount, UserDTO userDTO) {
        Order order = new Order();
        order.setBuyerId(request.getUserId());
        order.setSellerId(goods.getUserId());
        order.setGoodsId(goods.getId());
        order.setCount(request.getCount());
        order.setPayAmount(amount);
        order.setState(1);
        order.setCreateTime(LocalDateTime.now());
        order.setEthPubAddr(userDTO.getPubWeb3Addr());
        order.setTxId(request.getTxId());
        order.setName(goods.getName());
        order.setBrand(goods.getBrand());
        order.setImage(goods.getImage());
        order.setSaleChannel(goods.getSaleChannel());
        order.setPeriodType(goods.getPeriodType());
        order.setDeviceType(goods.getDeviceType());
        return order;
    }

    /**
     * 到链上上检测交易
     *
     * @param dto 支付单信息
     */
    @SuppressWarnings("DuplicatedCode")
    private void checkPayInfo(OrderDTO dto) {
        EthTransaction ethTransaction = web3Operations.getTransaction(dto.getTxId());
        Optional<Transaction> optional = ethTransaction.getTransaction();
        BigDecimal gasAmountInWei = BigDecimal.valueOf(0);
        if (optional.isPresent()) {
            Transaction transaction = optional.get();
            String fromPubAddr = transaction.getFrom().toLowerCase();
            String toPubAddr = transaction.getTo().toLowerCase();
            BigInteger amountInWei = transaction.getValue();
            BigInteger payAmountInWei = dto.getPayAmount().multiply(Mall3Const.ETH2WEI).toBigInteger();
            gasAmountInWei = BigDecimal.valueOf(transaction.getGas().multiply(transaction.getGasPrice()).longValue())
                    .setScale(10, RoundingMode.HALF_UP);
            if (amountInWei.compareTo(payAmountInWei) < 0) {
                //web交易中的金额少于应付金额
                log.error("Transaction amount is invalid {} {} {} {}", amountInWei, payAmountInWei,
                        ObjectUtils.toJson(transaction), ObjectUtils.toJson(dto));
                return;
            }
            if (!toPubAddr.equalsIgnoreCase(dto.getGoods().getContractAddress())) {
                log.error("Transaction address is invalid {} {}",
                        ObjectUtils.toJson(transaction), ObjectUtils.toJson(dto));
                return;
            }
        } else {
            log.error("Cannot find transaction {}", dto.getTxId());
        }

        EthGetTransactionReceipt getResult = web3Operations.getTransactionReceipt(dto.getTxId());
        TransactionReceipt result = getResult.getResult();
        log.info("Transaction receipt is {} {}", dto.getTxId(), ObjectUtils.toJson(result));
        if (null == result) {
            //没有查询到结果，需要延时
            log.warn("Cannot get transaction receipt, wait 10s for next operation");
            taskScheduler.schedule(() -> checkPayInfo(dto), Instant.ofEpochMilli(System.currentTimeMillis() + 10000));
        } else if (result.isStatusOK()) {
            log.info("OrderService pay success {}", dto.getId());
            paid(dto.getId(), gasAmountInWei.divide(Mall3Const.ETH2WEI, RoundingMode.HALF_UP));
        } else {
            log.error("OrderService pay error {}", dto.getId());
            paidError(dto.getId(), gasAmountInWei.divide(Mall3Const.ETH2WEI, RoundingMode.HALF_UP));
        }
    }

    /**
     * 充值成功
     *
     * @param id        充值单ID
     * @param gasAmount gas费用
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class)
    public void paid(Long id, BigDecimal gasAmount) {
        Order order = orderRepository.findById(id)
                .orElse(null);
        if (null != order) {
            order.setPaidAmount(order.getPayAmount());
            order.setState(Mall3Const.RechargeOrderState.PAID);
            order.setPayTime(LocalDateTime.now());
            order.setGasAmount(gasAmount);
            orderRepository.save(order);
        }
    }

    /**
     * 充值失败
     *
     * @param id 充值单ID
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class)
    public void paidError(Long id, BigDecimal gasAmount) {
        Order order = orderRepository.findById(id)
                .orElse(null);
        if (null != order) {
            order.setState(Mall3Const.RechargeOrderState.PAY_ERROR);
            order.setPayTime(LocalDateTime.now());
            order.setGasAmount(gasAmount);
            orderRepository.save(order);
        }
    }

    /**
     * 构造查询条件
     *
     * @param request 查询请求
     * @return 查询规格
     */
    @NotNull
    private Specification<Order> buildQuerySpec(OrderQueryRequest request) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (request.isShowSeller()) {
                predicates.add(builder.equal(root.get("sellerId"), request.getUserId()));
            } else {
                predicates.add(builder.equal(root.get("buyerId"), request.getUserId()));
            }
            if (null != request.getState()) {
                predicates.add(builder.equal(root.get("state"), request.getState()));
            }
            if (null != request.getDeviceType()) {
                predicates.add(builder.equal(root.get("deviceType"), DeviceType.fromValue(request.getDeviceType())));
            }
            if (null != request.getPeriodType()) {
                predicates.add(builder.equal(root.get("periodType"), PeriodType.fromValue(request.getPeriodType())));
            }
            if (null != request.getSaleChannel()) {
                predicates.add(builder.equal(root.get("saleChannel"), SaleChannel.fromValue(request.getSaleChannel())));
            }

            if (StringUtils.isNotBlank(request.getBrand())) {
                predicates.add(builder.equal(root.get("brand"), request.getBrand()));
            }

            query.where(predicates.toArray(new Predicate[0]));
            return query.getRestriction();
        };
    }

    /**
     * 填充用户信息
     *
     * @param page 订单分页信息
     */
    private void fillUserInfo(Page<OrderDTO> page) {
        List<Long> userIds = page.getContent()
                .stream()
                .flatMap(order -> Stream.of(order.getBuyerId(), order.getSellerId()))
                .distinct()
                .collect(Collectors.toList());
        Map<Long, UserDTO> userById = userService.queryById(userIds)
                .stream()
                .collect(Collectors.toMap(UserDTO::getId, user -> user));
        page.getContent().forEach(order -> {
            if (userById.containsKey(order.getSellerId())) {
                order.setSeller(userById.get(order.getSellerId()));
            }
            if (userById.containsKey(order.getBuyerId())) {
                order.setBuyer(userById.get(order.getBuyerId()));
            }
        });
    }
}
