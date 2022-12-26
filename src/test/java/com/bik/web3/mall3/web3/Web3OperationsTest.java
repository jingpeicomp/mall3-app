package com.bik.web3.mall3.web3;

import com.bik.web3.mall3.common.utils.generator.CardIdGenerator;
import com.bik.web3.mall3.domain.goods.entity.Goods;
import com.bik.web3.mall3.domain.goods.entity.GoodsItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @author Mingo.Liu
 * @date 2022-12-26
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class Web3OperationsTest {

    @Autowired
    private Web3Operations web3Operations;

    @Autowired
    private CardIdGenerator cardIdGenerator;

    @Test
    public void deploy() {
        Goods goods = new Goods();
        goods.setPrice(BigDecimal.valueOf(999999));
        GoodsItem item1 = new GoodsItem();
        item1.setId(cardIdGenerator.generate());
        GoodsItem item2 = new GoodsItem();
        item2.setId(cardIdGenerator.generate());
        web3Operations.deploy(goods, Arrays.asList(item1, item2), "0x7D6BB87b3F523F7Cf08f8e246cE14d21d35b7540", "");
    }
}