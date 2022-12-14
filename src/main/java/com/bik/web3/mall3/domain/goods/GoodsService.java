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
 * ??????????????????
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
     * ??????????????????
     *
     * @param request ??????????????????
     * @return ???????????????
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
     * ??????????????????
     *
     * @param request ??????????????????
     * @return ????????????????????????
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
     * ??????????????????????????????Item
     *
     * @param goodsId ??????ID
     * @return ??????Item??????
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class, readOnly = true)
    public List<GoodsItemDTO> queryItemByGoodsId(Long goodsId) {
        return itemRepository.findByGoodsIdOrderByIdAsc(goodsId)
                .stream()
                .map(GoodsItem::toValueObject)
                .collect(Collectors.toList());
    }

    /**
     * ????????????ID?????????????????????
     *
     * @param itemId ??????ID
     * @return ???????????????
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class, readOnly = true)
    public Optional<GoodsItemDTO> queryItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .map(GoodsItem::toValueObject);
    }

    /**
     * ????????????????????????
     *
     * @param goodsId ??????ID
     * @return ??????????????????
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class, readOnly = true)
    public Optional<GoodsDTO> queryById(Long goodsId) {
        return goodsRepository.findById(goodsId)
                .map(Goods::toValueObject);
    }

    /**
     * ??????ID????????????????????????
     *
     * @param goodsIds ID??????
     * @return ????????????
     */
    public List<GoodsDTO> queryByIds(List<Long> goodsIds) {
        return goodsRepository.findAllById(goodsIds)
                .stream()
                .map(Goods::toValueObject)
                .collect(Collectors.toList());
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param goodsId ??????ID
     * @param itemId  ?????????ID
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
     * ????????????
     *
     * @param goodsId ??????ID
     * @param count   ????????????
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
     * ??????web3 nft owner
     *
     * @param request nft????????????
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
     * ??????nft
     *
     * @param request nft????????????
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
     * ??????NFT
     *
     * @param request nft????????????
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
     * ??????NFT
     *
     * @param request nft????????????
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
     * ??????????????????
     *
     * @param request ??????????????????
     * @return ????????????
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
     * ??????web3 ????????????
     *
     * @param request ????????????????????????
     * @param goods   web3??????
     * @param items   web3????????????????????????
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
     * ??????NFT meta??????
     *
     * @param goods ??????
     * @param item  ????????????
     */
    private void buildNftMeta(Goods goods, GoodsItem item) {
        WebsGoodsItemMeta meta = new WebsGoodsItemMeta();
        meta.setName(goods.getName() + "-" + item.getId());
        String description = " * ??????: " + goods.getBrand()
                + " * ????????????: " + goods.getDeviceType().getDisplay()
                + " * ??????: " + goods.getPeriodType().getDisplay()
                + " * ??????: " + item.getId();
        meta.setDescription(description);
        meta.setExternalUrl("https://www.baidu.com");
        meta.setImage(goods.getImage());
        List<MetaAttribute> attributes = new ArrayList<>();
        attributes.add(MetaAttribute.builder()
                .key("??????")
                .value(goods.getBrand())
                .build());
        attributes.add(MetaAttribute.builder()
                .key("????????????")
                .value(goods.getDeviceType().getDisplay())
                .build());
        attributes.add(MetaAttribute.builder()
                .key("??????")
                .value(goods.getPeriodType().getDisplay())
                .build());
        attributes.add(MetaAttribute.builder()
                .key("??????")
                .value(String.valueOf(item.getId()))
                .build());
        meta.setAttributes(attributes);
        githubService.uploadJson(ObjectUtils.toJson(meta), item.getId());
    }

    /**
     * ????????????meta????????????
     *
     * @param goods ??????
     * @return ????????????
     */
    private String buildContractMeta(Goods goods) {
        WebsGoodsItemMeta meta = new WebsGoodsItemMeta();
        meta.setName(goods.getBrand() + "-" + goods.getId());
        String description = " * ??????: " + goods.getBrand()
                + " * ????????????: " + goods.getDeviceType().getDisplay()
                + " * ??????: " + goods.getPeriodType().getDisplay();
        meta.setDescription(description);
        meta.setExternalUrl("https://www.baidu.com");
        meta.setImage(goods.getImage());
        return githubService.uploadJson(ObjectUtils.toJson(meta));
    }

    /**
     * ??????????????????
     *
     * @param page ??????????????????
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
