package com.bik.web3.mall3.web3;

import com.bik.web3.contracts.Mall3Goods;
import com.bik.web3.mall3.common.exception.Mall3Exception;
import com.bik.web3.mall3.domain.goods.entity.Goods;
import com.bik.web3.mall3.domain.goods.entity.GoodsItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.*;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Web3操作
 *
 * @author Mingo.Liu
 * @date 2022-12-07
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class Web3Operations {
    private static final String PERSONAL_MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";

    private final Web3j web3j;

    @Value("${mall3.platform.privateKey}")
    private String platformPrivateKey;

    private Credentials platformCredentials;

    /**
     * 对签名消息，原始消息，账号地址三项信息进行认证，判断签名是否有效
     *
     * @param signature 签名消息
     * @param message   原始消息
     * @param address   账号地址
     * @return 是否匹配
     */
    public boolean validate(String signature, String message, String address) {
        String prefix = PERSONAL_MESSAGE_PREFIX + message.length();
        byte[] msgHash = Hash.sha3((prefix + message).getBytes());
        byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
        byte v = signatureBytes[64];
        if (v < 27) {
            v += 27;
        }

        SignatureData sd = new SignatureData(v, Arrays.copyOfRange(signatureBytes, 0, 32),
                Arrays.copyOfRange(signatureBytes, 32, 64));
        String addressRecovered = null;
        boolean match = false;

        for (byte i = 0; i < 4; i++) {
            BigInteger publicKey = Sign.recoverFromSignature(i,
                    new ECDSASignature(new BigInteger(1, sd.getR()), new BigInteger(1, sd.getS())),
                    msgHash);

            if (null != publicKey) {
                addressRecovered = "0x" + Keys.getAddress(publicKey);
                if (addressRecovered.equals(address)) {
                    match = true;
                    break;
                }
            }
        }
        return match;
    }

    /**
     * 获取交易结果
     *
     * @param txId 交易Hash
     * @return 交易结果
     */
    public EthGetTransactionReceipt getTransactionReceipt(String txId) {
        try {
            return web3j.ethGetTransactionReceipt(txId).send();
        } catch (Exception e) {
            log.error("Error get transaction receipt {}", txId, e);
        }

        return null;
    }

    /**
     * 获取交易信息
     *
     * @param txId 交易Hash
     * @return 交易信息
     */
    public EthTransaction getTransaction(String txId) {
        try {
            return web3j.ethGetTransactionByHash(txId).send();
        } catch (IOException e) {
            log.error("Error get transaction {}", txId, e);
        }

        return null;
    }

    /**
     * 部署Web3 nft智能合约
     *
     * @param goods            商品信息
     * @param items            商品附属卡信息
     * @param ownerWeb3Address 商品拥有者web3地址
     * @param contractMetaUrl  智能合约meta url
     */
    public String deploy(Goods goods, List<GoodsItem> items, String ownerWeb3Address, String contractMetaUrl) {
        List<BigInteger> itemIds = items.stream()
                .map(item -> BigInteger.valueOf(item.getId()))
                .collect(Collectors.toList());
        BigInteger price = goods.getPrice().toBigInteger();
        ContractGasProvider gasProvider = new DefaultGasProvider();
        try {
            Mall3Goods web3Goods = Mall3Goods.deploy(web3j, platformCredentials, gasProvider, ownerWeb3Address, itemIds, price, contractMetaUrl).send();
            log.info("Deploy mall3 web3 goods successfully goods id {} , contract address {} ", goods.getId(), web3Goods.getContractAddress());
            return web3Goods.getContractAddress();
        } catch (Exception e) {
            log.error("Deploy mall3 web3 goods error {}", goods.getId(), e);
            throw new Mall3Exception(e);
        }
    }

    /**
     * 加载web3智能合约实例
     *
     * @param contractAddress web3智能合约地址
     * @return 智能合约实例
     */
    public Mall3Goods load(String contractAddress) {
        ContractGasProvider gasProvider = new DefaultGasProvider();
        return Mall3Goods.load(contractAddress, web3j, platformCredentials, gasProvider);
    }

    @PostConstruct
    public void init() {
        platformCredentials = Credentials.create(platformPrivateKey);
    }
}
