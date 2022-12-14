package com.bik.web3.mall3.domain.goods;

import com.bik.web3.mall3.bean.goods.request.GoodsCreateRequest;
import com.bik.web3.mall3.bean.goods.dto.GoodsDTO;
import com.bik.web3.mall3.bean.goods.request.GoodsSearchRequest;
import com.bik.web3.mall3.common.dto.PageResult;
import com.bik.web3.mall3.common.utils.ObjectUtils;
import com.bik.web3.mall3.common.utils.generator.CardIdGenerator;
import com.bik.web3.mall3.domain.goods.entity.Goods;
import com.bik.web3.mall3.domain.goods.entity.GoodsItem;
import com.bik.web3.mall3.domain.goods.repository.GoodsItemRepository;
import com.bik.web3.mall3.domain.goods.repository.GoodsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 商品领域服务
 *
 * @author Mingo.Liu
 * @date 2022-12-13
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GoodsService {
    private final GoodsRepository goodsRepository;

    private final GoodsItemRepository itemRepository;

    private final CardIdGenerator cardIdGenerator;

    /**
     * 构造查询条件
     *
     * @param request 商品搜索请求
     * @return 查询规格
     */
    @NotNull
    private static Specification<Goods> buildQuerySpecification(GoodsSearchRequest request) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (null != request.getUserId()) {
                predicates.add(builder.equal(root.get("userId"), request.getUserId()));
            }
            if (StringUtils.isNotBlank(request.getBrand())) {
                predicates.add(builder.equal(root.get("brand"), request.getBrand()));
            }
            if (null != request.getDeviceType()) {
                predicates.add(builder.equal(root.get("deviceType"), request.getDeviceType()));
            }
            if (null != request.getPeriodType()) {
                predicates.add(builder.equal(root.get("periodType"), request.getPeriodType()));
            }
            if (null != request.getSaleChannel()) {
                predicates.add(builder.equal(root.get("saleChannel"), request.getSaleChannel()));
            }

            query.where(predicates.toArray(new Predicate[0]));
            return query.getRestriction();
        };
    }

    /**
     * 创建销售商品
     *
     * @param request 商品创建请求
     * @return 商品值对象
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class)
    public GoodsDTO create(GoodsCreateRequest request) {
        Goods goods = ObjectUtils.copy(request, new Goods(), true);
        goodsRepository.save(goods);

        String shopId = request.getUserId().toString();
        List<GoodsItem> items = IntStream.range(0, request.getCount())
                .mapToObj(i -> {
                    GoodsItem item = new GoodsItem();
                    item.setGoodsId(goods.getId());
                    item.setUserId(request.getUserId());
                    item.setId(String.valueOf(cardIdGenerator.generate(shopId)));
                    return item;
                })
                .collect(Collectors.toList());
        itemRepository.saveAll(items);

        return goods.toValueObject();
    }

    /**
     * 搜索商品信息
     *
     * @param request 商品搜索请求
     * @return 商品信息分页结果
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class, readOnly = true)
    public PageResult<GoodsDTO> search(GoodsSearchRequest request) {
        Specification<Goods> spec = buildQuerySpecification(request);
        request.initDefaultSort();
        Page<GoodsDTO> page = goodsRepository.findAll(spec, request.toSpringPageRequest())
                .map(Goods::toValueObject);
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }
}
