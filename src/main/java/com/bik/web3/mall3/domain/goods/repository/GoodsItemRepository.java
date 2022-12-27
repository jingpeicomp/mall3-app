package com.bik.web3.mall3.domain.goods.repository;

import com.bik.web3.mall3.domain.goods.entity.GoodsItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author Mingo.Liu
 * @date 2022-12-13
 */
public interface GoodsItemRepository extends JpaRepository<GoodsItem, Long>, JpaSpecificationExecutor<GoodsItem> {
    /**
     * 查询商品下所有的附属Item
     *
     * @param userId  用户ID
     * @param goodsId 商品ID
     * @return 附属Item列表
     */
    List<GoodsItem> findByUserIdAndGoodsIdOrderByIdAsc(Long userId, Long goodsId);
}
