package com.bik.web3.mall3.web3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

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
}
