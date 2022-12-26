package com.bik.web3.mall3.domain.goods;

import com.bik.web3.mall3.adapter.github.GithubService;
import com.bik.web3.mall3.bean.goods.dto.GoodsDTO;
import com.bik.web3.mall3.bean.goods.dto.GoodsItemDTO;
import com.bik.web3.mall3.bean.goods.dto.WebsGoodsItemMeta;
import com.bik.web3.mall3.bean.goods.request.GoodsCreateRequest;
import com.bik.web3.mall3.bean.goods.request.GoodsSearchRequest;
import com.bik.web3.mall3.common.dto.PageResult;
import com.bik.web3.mall3.common.enums.DeviceType;
import com.bik.web3.mall3.common.enums.PeriodType;
import com.bik.web3.mall3.common.enums.SaleChannel;
import com.bik.web3.mall3.common.exception.Mall3Exception;
import com.bik.web3.mall3.common.exception.ResultCodes;
import com.bik.web3.mall3.common.utils.ObjectUtils;
import com.bik.web3.mall3.common.utils.generator.CardIdGenerator;
import com.bik.web3.mall3.domain.goods.entity.Goods;
import com.bik.web3.mall3.domain.goods.entity.GoodsItem;
import com.bik.web3.mall3.domain.goods.repository.GoodsItemRepository;
import com.bik.web3.mall3.domain.goods.repository.GoodsRepository;
import com.bik.web3.mall3.web3.Web3Operations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.bik.web3.mall3.bean.goods.dto.WebsGoodsItemMeta.MetaAttribute;

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

    private final GithubService githubService;

    private final Web3Operations web3Operations;

    @Qualifier("asyncExecutor")
    private final AsyncTaskExecutor asyncExecutor;

    /**
     * 创建销售商品
     *
     * @param request 商品创建请求
     * @return 商品值对象
     */
    @Transactional(timeout = 100, rollbackFor = Exception.class)
    public GoodsDTO create(GoodsCreateRequest request) {
        if (request.getSaleChannel() == SaleChannel.Web3 && StringUtils.isBlank(request.getUserPubWeb3Addr())) {
            throw new Mall3Exception(ResultCodes.WEB3_ADDRESS_NOT_EXIST);
        }

        Goods goods = ObjectUtils.copy(request, new Goods(), true);
        goodsRepository.save(goods);

        String shopId = request.getUserId().toString();
        List<GoodsItem> items = IntStream.range(0, request.getCount())
                .mapToObj(i -> {
                    GoodsItem item = new GoodsItem();
                    item.setGoodsId(goods.getId());
                    item.setUserId(request.getUserId());
                    item.setId(cardIdGenerator.generate(shopId));
                    return item;
                })
                .collect(Collectors.toList());
        itemRepository.saveAll(items);

        deployWeb3Nft(request, goods, items);
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

    /**
     * 查询商品下的所有附属Item
     *
     * @param userId  用户ID
     * @param goodsId 商品ID
     * @return 附属Item列表
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class, readOnly = true)
    public List<GoodsItemDTO> queryItem(Long userId, Long goodsId) {
        return itemRepository.findByUserIdAndGoodsIdOrderByIdAsc(userId, goodsId)
                .stream()
                .map(GoodsItem::toValueObject)
                .collect(Collectors.toList());
    }

    /**
     * 构造查询条件
     *
     * @param request 商品搜索请求
     * @return 查询规格
     */
    @NotNull
    private Specification<Goods> buildQuerySpecification(GoodsSearchRequest request) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (null != request.getUserId()) {
                predicates.add(builder.equal(root.get("userId"), request.getUserId()));
            }
            if (StringUtils.isNotBlank(request.getBrand())) {
                predicates.add(builder.equal(root.get("brand"), request.getBrand()));
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

            query.where(predicates.toArray(new Predicate[0]));
            return query.getRestriction();
        };
    }

    /**
     * 部署web3 智能合约
     *
     * @param request 销售商品创建请求
     * @param goods   web3商品
     * @param items   web3商品附属卡号列表
     */
    private void deployWeb3Nft(GoodsCreateRequest request, Goods goods, List<GoodsItem> items) {
        if (goods.getSaleChannel() == SaleChannel.Web3) {
            String contractMetaUrl = buildContractMeta(goods);
            for (GoodsItem item : items) {
                buildNftMeta(goods, item);
            }

            String contractAddress = web3Operations.deploy(goods, items, request.getUserPubWeb3Addr(), contractMetaUrl);
            if (StringUtils.isNotBlank(contractAddress)) {
                goods.setContractAddress(contractAddress);
                goodsRepository.save(goods);
            }
        }
    }

    /**
     * 构造NFT meta文件
     *
     * @param goods 商品
     * @param item  商品卡号
     */
    private void buildNftMeta(Goods goods, GoodsItem item) {
        WebsGoodsItemMeta meta = new WebsGoodsItemMeta();
        meta.setName(goods.getName());
        String description = "- 品牌: " + goods.getBrand()
                + "- 设备类型: " + goods.getDeviceType().getDisplay()
                + "- 时长: " + goods.getPeriodType().getDisplay()
                + "- 卡号: " + item.getId();
        meta.setDescription(description);
        meta.setExternalUrl("https://www.baidu.com");
        meta.setImage(goods.getImage());
        List<MetaAttribute> attributes = new ArrayList<>();
        attributes.add(MetaAttribute.builder()
                .key("品牌")
                .value(goods.getBrand())
                .build());
        attributes.add(MetaAttribute.builder()
                .key("设备类型")
                .value(goods.getDeviceType().getDisplay())
                .build());
        attributes.add(MetaAttribute.builder()
                .key("时长")
                .value(goods.getPeriodType().getDisplay())
                .build());
        attributes.add(MetaAttribute.builder()
                .key("卡号")
                .value(String.valueOf(item.getId()))
                .build());
        meta.setAttributes(attributes);
        githubService.uploadJson(ObjectUtils.toJson(meta), item.getId());
    }

    /**
     * 生成合约meta信息文件
     *
     * @param goods 商品
     * @return 合约文件
     */
    private String buildContractMeta(Goods goods) {
        WebsGoodsItemMeta meta = new WebsGoodsItemMeta();
        meta.setName(goods.getName());
        String description = "- 品牌: " + goods.getBrand()
                + "- 设备类型: " + goods.getDeviceType().getDisplay()
                + "- 时长: " + goods.getPeriodType().getDisplay();
        meta.setDescription(description);
        meta.setExternalUrl("https://www.baidu.com");
        meta.setImage(goods.getImage());
        return githubService.uploadJson(ObjectUtils.toJson(meta));
    }
}
