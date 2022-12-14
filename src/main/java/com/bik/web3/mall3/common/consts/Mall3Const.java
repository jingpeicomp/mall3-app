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

    /**
     * 默认商品图片
     */
    String DEFAULT_GOODS_IMAGE_URL = "https://s1.ax1x.com/2022/12/13/z50QxI.png";

    /**
     * 默认品牌图片
     */
    String DEFAULT_BRAND_IMAGE_URL = "https://s1.ax1x.com/2022/12/13/z5BlY4.png";

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
