package com.bik.web3.mall3.domain.account.entity;

import com.bik.web3.mall3.bean.account.AccountDTO;
import com.bik.web3.mall3.bean.vcoin.dto.VcoinRechargeOrderDTO;
import com.bik.web3.mall3.common.utils.ObjectUtils;
import com.bik.web3.mall3.domain.order.Order;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Mingo.Liu
 * @date 2022-12-08
 */
@Data
@Entity
@Table(name = "t_account")
public class Account implements Serializable {
    @Id
    @Column(name = "user_id", nullable = false, updatable = false, columnDefinition = "bigint not null comment '用户ID'")
    private Long userId;

    /**
     * Vcoin账户余额
     */
    @Column(name = "balance_amount", columnDefinition = "bigint default 0 comment 'Vcoin账户余额'")
    private Long balanceAmount = 0L;

    /**
     * 版本号
     */
    @Version
    @Column(name = "version", columnDefinition = "bigint comment '版本号'")
    private Long version;

    public AccountDTO toValueObject() {
        return ObjectUtils.copy(this, new AccountDTO());
    }

    /**
     * 充值
     *
     * @param order 充值单
     * @return 账户流水
     */
    public AccountBill recharge(VcoinRechargeOrderDTO order) {
        balanceAmount += order.getAmount();
        AccountBill bill = new AccountBill();
        bill.setAccountId(userId);
        bill.setTime(order.getPayTime());
        bill.setAmount(order.getAmount());
        bill.setType(1);
        bill.setRelatedId(order.getId().toString());
        return bill;
    }

    /**
     * 购买商品账户变化
     * @param order 订单
     * @return 账户流水
     */
    public AccountBill buy(Order order) {
        balanceAmount -= order.getPayAmount().longValue();
        AccountBill bill = new AccountBill();
        bill.setAccountId(userId);
        bill.setTime(order.getCreateTime());
        bill.setAmount(order.getPayAmount().longValue());
        bill.setType(2);
        bill.setRelatedId(order.getId().toString());
        return bill;
    }

    /**
     * 卖出商品账户变化
     * @param order 订单
     * @return 账户流水
     */
    public AccountBill sell(Order order) {
        balanceAmount += order.getPayAmount().longValue();
        AccountBill bill = new AccountBill();
        bill.setAccountId(userId);
        bill.setTime(order.getCreateTime());
        bill.setAmount(order.getPayAmount().longValue());
        bill.setType(3);
        bill.setRelatedId(order.getId().toString());
        return bill;
    }
}
