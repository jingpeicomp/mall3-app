package com.bik.web3.mall3.domain.vcoin;

import com.bik.web3.mall3.bean.vcoin.dto.VcoinRechargeOrderDTO;
import com.bik.web3.mall3.bean.vcoin.request.VcoinRechargeOrderCreateRequest;
import com.bik.web3.mall3.bean.vcoin.request.VcoinRechargeOrderPayRequest;
import com.bik.web3.mall3.bean.vcoin.request.VcoinRechargeQueryRequest;
import com.bik.web3.mall3.common.consts.Mall3Const;
import com.bik.web3.mall3.common.dto.PageResult;
import com.bik.web3.mall3.common.dto.Sort;
import com.bik.web3.mall3.common.exception.Mall3Exception;
import com.bik.web3.mall3.common.exception.ResultCodes;
import com.bik.web3.mall3.common.utils.ObjectUtils;
import com.bik.web3.mall3.domain.account.AccountService;
import com.bik.web3.mall3.web3.Web3Operations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Vcoin充值领域服务
 *
 * @author Mingo.Liu
 * @date 2022-12-08
 */
@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
public class VcoinRechargeService {

    /**
     * 以太币和Vcoin汇率
     */
    private static final BigDecimal EXCHANGE_RATE = BigDecimal.valueOf(100000000L);

    /**
     * 平台收款Web3账户地址
     */
    private static final String PLATFORM_WEB3_ADDRESS = "0xe5a70661c17ac8b012cb9a822ebab93a27c19859";

    private final VcoinRechargeOrderRepository repository;

    private final Web3Operations web3Operations;

    @Qualifier("asyncExecutor")
    private final AsyncTaskExecutor asyncExecutor;

    private final AccountService accountService;

    private final TaskScheduler taskScheduler;

    /**
     * 查询充值单
     *
     * @param request 查询请求
     * @return 查询结果
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class, readOnly = true)
    public PageResult<VcoinRechargeOrderDTO> query(VcoinRechargeQueryRequest request) {
        Specification<VcoinRechargeOrder> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("accountId"), request.getUserId()));
            if (null != request.getState()) {
                predicates.add(builder.equal(root.get("state"), request.getState()));
            }
            query.where(predicates.toArray(new Predicate[0]));
            return query.getRestriction();
        };

        request.setSort(Sort.by(Sort.Direction.DESC, "id"));
        Page<VcoinRechargeOrderDTO> page = repository.findAll(spec, request.toSpringPageRequest())
                .map(VcoinRechargeOrder::toValueObject);
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    /**
     * 创建充值单
     *
     * @param request 充值单创建请求
     * @return 充值单值对象
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class)
    public VcoinRechargeOrderDTO create(VcoinRechargeOrderCreateRequest request) {
        VcoinRechargeOrder order = new VcoinRechargeOrder();
        order.setAccountId(request.getUserId());
        order.setAmount(request.getVcoinAmount());
        order.setPayAmount(BigDecimal.valueOf(request.getVcoinAmount()).divide(EXCHANGE_RATE));
        order.setEthPubAddr(PLATFORM_WEB3_ADDRESS);
        order.setPayEthPubAddr(request.getFromPubAddress().toLowerCase());
        order.setState(Mall3Const.RechargeOrderState.CREATED);
        order.setCreateTime(LocalDateTime.now());
        return repository.save(order).toValueObject();
    }

    /**
     * 充值单支付
     *
     * @param request 支付请求
     * @return 充值单结果
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class)
    public VcoinRechargeOrderDTO pay(VcoinRechargeOrderPayRequest request) {
        VcoinRechargeOrder order = repository.findById(request.getId())
                .orElseThrow(() -> new Mall3Exception(ResultCodes.DATA_NOT_EXISTS));
        if (order.getState() == Mall3Const.RechargeOrderState.PAID) {
            throw new Mall3Exception(ResultCodes.PARAMETER_ERROR, "订单已经支付完成，不能重复支付");
        }

        order.setTxId(request.getTxId());
        VcoinRechargeOrderDTO dto = repository.save(order).toValueObject();
        asyncExecutor.execute(() -> checkPayInfo(dto));
        return dto;
    }

    /**
     * 充值成功
     *
     * @param id        充值单ID
     * @param gasAmount gas费用
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class)
    public void paid(Long id, BigDecimal gasAmount) {
        VcoinRechargeOrder order = repository.findById(id)
                .orElse(null);
        if (null != order) {
            order.setPaidAmount(order.getPayAmount());
            order.setState(Mall3Const.RechargeOrderState.PAID);
            order.setPayTime(LocalDateTime.now());
            order.setGasAmount(gasAmount);
            repository.save(order);
            accountService.recharge(order.toValueObject());
        }
    }

    /**
     * 充值失败
     *
     * @param id 充值单ID
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class)
    public void paidError(Long id) {
        VcoinRechargeOrder order = repository.findById(id)
                .orElse(null);
        if (null != order) {
            order.setState(Mall3Const.RechargeOrderState.PAY_ERROR);
            order.setPayTime(LocalDateTime.now());
            repository.save(order);
        }
    }

    /**
     * 到链上上检测交易
     *
     * @param dto 支付单信息
     */
    @SuppressWarnings("DuplicatedCode")
    private void checkPayInfo(VcoinRechargeOrderDTO dto) {
        EthTransaction ethTransaction = web3Operations.getTransaction(dto.getTxId());
        Optional<Transaction> optional = ethTransaction.getTransaction();
        BigInteger gasAmountInWei = BigInteger.valueOf(0);
        if (optional.isPresent()) {
            Transaction transaction = optional.get();
            String fromPubAddr = transaction.getFrom().toLowerCase();
            String toPubAddr = transaction.getTo().toLowerCase();
            BigInteger amountInWei = transaction.getValue();
            BigInteger payAmountInWei = dto.getPayAmount().multiply(Mall3Const.ETH2WEI).toBigInteger();
            gasAmountInWei = transaction.getGas().multiply(transaction.getGasPrice());
            if (amountInWei.compareTo(payAmountInWei) < 0) {
                //web交易中的金额少于应付金额
                log.error("Transaction amount is invalid {} {} {} {}", amountInWei, payAmountInWei,
                        ObjectUtils.toJson(transaction), ObjectUtils.toJson(dto));
                return;
            }
            if (!fromPubAddr.equals(dto.getPayEthPubAddr()) || !toPubAddr.equals(dto.getEthPubAddr())) {
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
            log.info("VcoinRechargeOrder pay success {}", dto.getId());
            paid(dto.getId(), BigDecimal.valueOf(gasAmountInWei.longValue()).divide(Mall3Const.ETH2WEI));
        } else {
            log.error("VcoinRechargeOrder pay error {}", dto.getId());
            paidError(dto.getId());
        }
    }
}
