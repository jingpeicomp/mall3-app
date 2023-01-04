package com.bik.web3.mall3.domain.goods;

import com.bik.web3.mall3.bean.goods.request.GoodsCreateRequest;
import com.bik.web3.mall3.common.consts.Mall3Const;
import com.bik.web3.mall3.common.enums.CurrencyType;
import com.bik.web3.mall3.common.enums.DeviceType;
import com.bik.web3.mall3.common.enums.PeriodType;
import com.bik.web3.mall3.common.enums.SaleChannel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * @author Mingo.Liu
 * @date 2022-12-29
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsServiceTest {

    @Autowired
    private GoodsService goodsService;

    @Test
    public void create() {
        GoodsCreateRequest request = new GoodsCreateRequest();
        request.setUserId(180L);
        request.setUserName("bm");
        request.setUserPubWeb3Addr("0xe5a70661c17ac8b012cb9a822ebab93a27c19859");
        request.setStockId(1L);
        request.setUserId(180L);
        request.setCount(999);
        request.setBrand(Mall3Const.BRAND_PLATFORM);
        request.setImage("https://s1.ax1x.com/2022/12/29/pSp96iR.jpg");
        request.setDeviceType(DeviceType.SINGLE);
        request.setPeriodType(PeriodType.Month);
        request.setSaleChannel(SaleChannel.Web2);
        request.setCurrencyType(CurrencyType.VCOIN);
        request.setPrice(BigDecimal.valueOf(100));
        request.setName("白牌卡" + request.getDeviceType().getDisplay() + request.getPeriodType().getDisplay());
        goodsService.create(request);

        request.setCount(20);
        request.setDeviceType(DeviceType.SINGLE);
        request.setPeriodType(PeriodType.Month);
        request.setSaleChannel(SaleChannel.Web3);
        request.setCurrencyType(CurrencyType.ETH_COIN);
        request.setPrice(BigDecimal.valueOf(0.0001));
        request.setName("白牌卡" + request.getDeviceType().getDisplay() + request.getPeriodType().getDisplay());
        goodsService.create(request);

        request.setCount(999);
        request.setDeviceType(DeviceType.HOME);
        request.setPeriodType(PeriodType.Month);
        request.setSaleChannel(SaleChannel.Web2);
        request.setCurrencyType(CurrencyType.VCOIN);
        request.setPrice(BigDecimal.valueOf(200));
        request.setName("白牌卡" + request.getDeviceType().getDisplay() + request.getPeriodType().getDisplay());
        goodsService.create(request);

        request.setCount(20);
        request.setDeviceType(DeviceType.HOME);
        request.setPeriodType(PeriodType.Month);
        request.setSaleChannel(SaleChannel.Web3);
        request.setCurrencyType(CurrencyType.ETH_COIN);
        request.setPrice(BigDecimal.valueOf(0.0002));
        request.setName("白牌卡" + request.getDeviceType().getDisplay() + request.getPeriodType().getDisplay());
        goodsService.create(request);

        request.setCount(999);
        request.setDeviceType(DeviceType.SINGLE);
        request.setPeriodType(PeriodType.YEAR);
        request.setSaleChannel(SaleChannel.Web2);
        request.setCurrencyType(CurrencyType.VCOIN);
        request.setPrice(BigDecimal.valueOf(100));
        request.setName("白牌卡" + request.getDeviceType().getDisplay() + request.getPeriodType().getDisplay());
        goodsService.create(request);

        request.setCount(20);
        request.setDeviceType(DeviceType.SINGLE);
        request.setPeriodType(PeriodType.YEAR);
        request.setSaleChannel(SaleChannel.Web3);
        request.setCurrencyType(CurrencyType.ETH_COIN);
        request.setPrice(BigDecimal.valueOf(0.0001));
        request.setName("白牌卡" + request.getDeviceType().getDisplay() + request.getPeriodType().getDisplay());
        goodsService.create(request);

        request.setCount(999);
        request.setDeviceType(DeviceType.HOME);
        request.setPeriodType(PeriodType.YEAR);
        request.setSaleChannel(SaleChannel.Web2);
        request.setCurrencyType(CurrencyType.VCOIN);
        request.setPrice(BigDecimal.valueOf(200));
        request.setName("白牌卡" + request.getDeviceType().getDisplay() + request.getPeriodType().getDisplay());
        goodsService.create(request);

        request.setCount(20);
        request.setDeviceType(DeviceType.HOME);
        request.setPeriodType(PeriodType.YEAR);
        request.setSaleChannel(SaleChannel.Web3);
        request.setCurrencyType(CurrencyType.ETH_COIN);
        request.setPrice(BigDecimal.valueOf(0.0002));
        request.setName("白牌卡" + request.getDeviceType().getDisplay() + request.getPeriodType().getDisplay());
        goodsService.create(request);
    }
}