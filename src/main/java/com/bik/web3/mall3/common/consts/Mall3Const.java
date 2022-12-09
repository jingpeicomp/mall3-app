package com.bik.web3.mall3.common.consts;

/**
 * 常量
 *
 * @author Mingo.Liu
 * @date 2022-12-08
 */
public interface Mall3Const {
    /**
     * 平台品牌名称
     */
    String BRAND_PLATFORM = "白牌";

    interface RechargeOrderState {
        /**
         * 充值单创建成功
         */
        int CREATED = 1;

        /**
         * 充值单支付成功
         */
        int PAID = 2;

        /**
         * 支付失败
         */

        int PAY_ERROR = 3;
    }
}
