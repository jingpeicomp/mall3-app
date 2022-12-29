package com.bik.web3.mall3.domain.account;

import com.bik.web3.mall3.bean.account.AccountDTO;
import com.bik.web3.mall3.bean.vcoin.dto.VcoinRechargeOrderDTO;
import com.bik.web3.mall3.common.exception.Mall3Exception;
import com.bik.web3.mall3.common.exception.ResultCodes;
import com.bik.web3.mall3.domain.account.entity.Account;
import com.bik.web3.mall3.domain.account.entity.AccountBill;
import com.bik.web3.mall3.domain.account.repository.AccountBillRepository;
import com.bik.web3.mall3.domain.account.repository.AccountRepository;
import com.bik.web3.mall3.domain.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 账户领域服务
 *
 * @author Mingo.Liu
 * @date 2022-12-08
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;

    private final AccountBillRepository billRepository;

    /**
     * 查询账户信息
     *
     * @param userId 用户ID
     * @return 账户信息
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class, readOnly = true)
    public AccountDTO query(Long userId) {
        return repository.findById(userId)
                .map(Account::toValueObject)
                .orElse(AccountDTO.builder()
                        .userId(userId)
                        .balanceAmount(0L)
                        .build());
    }

    /**
     * 账户充值
     *
     * @param rechargeOrder VCoin充值单
     * @return 账户信息
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class)
    public AccountDTO recharge(VcoinRechargeOrderDTO rechargeOrder) {
        Account account = repository.findById(rechargeOrder.getAccountId())
                .orElseGet(() -> {
                    Account newAccount = new Account();
                    newAccount.setUserId(rechargeOrder.getAccountId());
                    newAccount.setBalanceAmount(0L);
                    return repository.save(newAccount);
                });
        AccountBill bill = account.recharge(rechargeOrder);
        repository.save(account);
        billRepository.save(bill);
        return account.toValueObject();
    }

    /**
     * 商品交易
     *
     * @param order 订单
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class)
    public void pay(Order order) {
        Account buyerAccount = repository.findById(order.getBuyerId())
                .orElseGet(() -> {
                    Account newAccount = new Account();
                    newAccount.setUserId(order.getBuyerId());
                    newAccount.setBalanceAmount(0L);
                    return repository.save(newAccount);
                });
        if (buyerAccount.getBalanceAmount() < order.getPayAmount().longValue()) {
            throw new Mall3Exception(ResultCodes.NOT_ENOUGH_AMOUNT);
        }
        AccountBill buyerBill = buyerAccount.buy(order);
        repository.save(buyerAccount);
        billRepository.save(buyerBill);

        Account sellerAccount = repository.findById(order.getSellerId())
                .orElseGet(() -> {
                    Account newAccount = new Account();
                    newAccount.setUserId(order.getSellerId());
                    newAccount.setBalanceAmount(0L);
                    return repository.save(newAccount);
                });
        AccountBill sellerBill = sellerAccount.sell(order);
        repository.save(sellerAccount);
        billRepository.save(sellerBill);
    }
}
