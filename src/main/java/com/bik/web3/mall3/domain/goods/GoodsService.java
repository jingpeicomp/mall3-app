package com.bik.web3.mall3.domain.goods;

import com.bik.web3.contracts.Mall3Goods;
import com.bik.web3.mall3.adapter.github.GithubService;
import com.bik.web3.mall3.bean.goods.dto.GoodsDTO;
import com.bik.web3.mall3.bean.goods.dto.GoodsItemDTO;
import com.bik.web3.mall3.bean.goods.dto.WebsGoodsItemMeta;
import com.bik.web3.mall3.bean.goods.request.GoodsCreateRequest;
import com.bik.web3.mall3.bean.goods.request.GoodsItemOperateRequest;
import com.bik.web3.mall3.bean.goods.request.GoodsItemTransferRequest;
import com.bik.web3.mall3.bean.goods.request.GoodsSearchRequest;
import com.bik.web3.mall3.bean.user.dto.UserDTO;
import com.bik.web3.mall3.common.consts.Mall3Const;
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
import com.bik.web3.mall3.domain.user.UserService;
import com.bik.web3.mall3.web3.Web3Operations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.math.BigInteger;
import java.util.*;
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

    private final UserService userService;

    private static final BigInteger GWEI_TO_WEI = BigInteger.valueOf(1000000000L);

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
        if (!request.isShowSeller()) {
            fillUserInfo(page);
        }
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    /**
     * 查询商品下的所有附属Item
     *
     * @param goodsId 商品ID
     * @return 附属Item列表
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class, readOnly = true)
    public List<GoodsItemDTO> queryItemByGoodsId(Long goodsId) {
        return itemRepository.findByGoodsIdOrderByIdAsc(goodsId)
                .stream()
                .map(GoodsItem::toValueObject)
                .collect(Collectors.toList());
    }

    /**
     * 根据卡号ID查询充值卡信息
     *
     * @param itemId 卡号ID
     * @return 充值卡信息
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class, readOnly = true)
    public Optional<GoodsItemDTO> queryItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .map(GoodsItem::toValueObject);
    }

    /**
     * 查询销售商品信息
     *
     * @param goodsId 商品ID
     * @return 销售商品信息
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class, readOnly = true)
    public Optional<GoodsDTO> queryById(Long goodsId) {
        return goodsRepository.findById(goodsId)
                .map(Goods::toValueObject);
    }

    /**
     * 根据ID列表查询销售商品
     *
     * @param goodsIds ID列表
     * @return 销售商品
     */
    public List<GoodsDTO> queryByIds(List<Long> goodsIds) {
        return goodsRepository.findAllById(goodsIds)
                .stream()
                .map(Goods::toValueObject)
                .collect(Collectors.toList());
    }

    /**
     * 充值修改商品库存和附属卡状态
     *
     * @param goodsId 商品ID
     * @param itemId  附属卡ID
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class)
    public void recharge(Long goodsId, Long itemId) {
        Goods goods = goodsRepository.findById(goodsId)
                .orElseThrow(() -> new Mall3Exception(ResultCodes.DATA_NOT_EXISTS));
        goods.setCount(goods.getCount() - 1);
        goodsRepository.save(goods);

        GoodsItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new Mall3Exception(ResultCodes.DATA_NOT_EXISTS));
        item.setRecharged(Mall3Const.YesOrNo.YES);
        itemRepository.save(item);
    }

    /**
     * 卖出商品
     *
     * @param goodsId 商品ID
     * @param count   卖出数目
     */
    @Transactional(timeout = 20, rollbackFor = Exception.class)
    public void sell(Long goodsId, Integer count) {
        Goods goods = goodsRepository.findById(goodsId)
                .orElseThrow(() -> new Mall3Exception(ResultCodes.DATA_NOT_EXISTS));
        if (goods.getCount() < count) {
            throw new Mall3Exception(ResultCodes.NOT_ENOUGH_COUNT);
        }

        goods.setCount(goods.getCount() - count);
        goodsRepository.save(goods);

        List<GoodsItem> goodsItems = itemRepository.findByGoodsIdOrderByIdAsc(goodsId);
        List<GoodsItem> availableItems = goodsItems.stream()
                .filter(item -> item.getSold() == Mall3Const.YesOrNo.NO && item.getRecharged() == Mall3Const.YesOrNo.NO)
                .collect(Collectors.toList());
        if (availableItems.size() < count) {
            throw new Mall3Exception(ResultCodes.NOT_ENOUGH_COUNT);
        }
        availableItems.stream()
                .limit(count)
                .forEach(item -> {
                    item.setSold(Mall3Const.YesOrNo.YES);
                    itemRepository.save(item);
                });
    }

    /**
     * 获取web3 nft owner
     *
     * @param request nft操作请求
     * @return nft owner
     */
    public String getNftOwner(GoodsItemOperateRequest request) {
        Goods goods = goodsRepository.findById(request.getGoodsId())
                .orElseThrow(() -> new Mall3Exception(ResultCodes.DATA_NOT_EXISTS));
        Mall3Goods mall3Goods = web3Operations.load(goods.getContractAddress());
        try {
            return mall3Goods.getItemOwner(BigInteger.valueOf(request.getItemId())).send();
        } catch (Exception e) {
            log.error("Get nft owner error {}", request, e);
            throw new Mall3Exception(ResultCodes.CONTRACT_OPERATION_ERROR);
        }
    }

    /**
     * 转移nft
     *
     * @param request nft转移请求
     */
    public void transferNft(GoodsItemTransferRequest request) {
        Goods goods = goodsRepository.findById(request.getGoodsId())
                .orElseThrow(() -> new Mall3Exception(ResultCodes.DATA_NOT_EXISTS));
        Mall3Goods mall3Goods = web3Operations.load(goods.getContractAddress());
        try {
            String nftOwner = mall3Goods.getItemOwner(BigInteger.valueOf(request.getItemId())).send();
            if (!Objects.equals(nftOwner, request.getUserPubWeb3Addr())) {
                throw new Mall3Exception(ResultCodes.OTHER_NFT);
            }
            mall3Goods.transfer(BigInteger.valueOf(request.getItemId()), request.getToWeb3Address()).send();
        } catch (Mall3Exception e) {
            throw e;
        } catch (Exception e) {
            log.error("Transfer nft error {} {}", request, e);
            throw new Mall3Exception(ResultCodes.CONTRACT_OPERATION_ERROR);
        }
    }

    /**
     * 销毁NFT
     *
     * @param request nft销毁请求
     */
    public void destroyNft(GoodsItemOperateRequest request) {
        Goods goods = goodsRepository.findById(request.getGoodsId())
                .orElseThrow(() -> new Mall3Exception(ResultCodes.DATA_NOT_EXISTS));
        Mall3Goods mall3Goods = web3Operations.load(goods.getContractAddress());
        try {
            String nftOwner = mall3Goods.getItemOwner(BigInteger.valueOf(request.getItemId())).send();
            if (!Objects.equals(nftOwner, request.getUserPubWeb3Addr())) {
                throw new Mall3Exception(ResultCodes.OTHER_NFT);
            }
            mall3Goods.burn(BigInteger.valueOf(request.getItemId())).send();
        } catch (Mall3Exception e) {
            throw e;
        } catch (Exception e) {
            log.error("Destroy nft owner error {}", request, e);
            throw new Mall3Exception(ResultCodes.CONTRACT_OPERATION_ERROR);
        }
    }

    /**
     * 购买NFT
     *
     * @param request nft购买请求
     */
    public void buyNft(GoodsItemOperateRequest request) {
        Goods goods = goodsRepository.findById(request.getGoodsId())
                .orElseThrow(() -> new Mall3Exception(ResultCodes.DATA_NOT_EXISTS));
        Mall3Goods mall3Goods = web3Operations.load(goods.getContractAddress());
        try {
            mall3Goods.buy(BigInteger.valueOf(request.getItemId()), goods.getPrice().toBigInteger().multiply(GWEI_TO_WEI)).send();
        } catch (Exception e) {
            log.error("Buy nft owner error {}", request, e);
            throw new Mall3Exception(ResultCodes.CONTRACT_OPERATION_ERROR);
        }
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
            if (request.isShowSeller()) {
                if (null != request.getUserId()) {
                    predicates.add(builder.equal(root.get("userId"), request.getUserId()));
                }
            } else {
                if (null != request.getUserId()) {
                    predicates.add(builder.notEqual(root.get("userId"), request.getUserId()));
                }
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
        meta.setName(goods.getName() + "-" + item.getId());
        String description = " * 品牌: " + goods.getBrand()
                + " * 设备类型: " + goods.getDeviceType().getDisplay()
                + " * 时长: " + goods.getPeriodType().getDisplay()
                + " * 卡号: " + item.getId();
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
        meta.setName(goods.getBrand() + "-" + goods.getId());
        String description = " * 品牌: " + goods.getBrand()
                + " * 设备类型: " + goods.getDeviceType().getDisplay()
                + " * 时长: " + goods.getPeriodType().getDisplay();
        meta.setDescription(description);
        meta.setExternalUrl("https://www.baidu.com");
        meta.setImage(goods.getImage());
        return githubService.uploadJson(ObjectUtils.toJson(meta));
    }

    /**
     * 填充用户信息
     *
     * @param page 商品分页信息
     */
    private void fillUserInfo(Page<GoodsDTO> page) {
        List<Long> userIds = page.getContent()
                .stream()
                .map(GoodsDTO::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, UserDTO> userById = userService.queryById(userIds)
                .stream()
                .collect(Collectors.toMap(UserDTO::getId, user -> user));
        page.getContent().forEach(goods -> {
            if (userById.containsKey(goods.getUserId())) {
                goods.setUser(userById.get(goods.getUserId()));
            }
        });
    }
}
