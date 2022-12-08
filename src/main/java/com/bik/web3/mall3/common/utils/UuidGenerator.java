package com.bik.web3.mall3.common.utils;

import java.math.BigInteger;
import java.util.UUID;

/**
 * 生成22位的短UUID。 字符编码为57个字符，大小写字母和数字，排除掉1、l和I，0和o易混字符
 *
 * @author Mingo.Liu
 */
public final class UuidGenerator {
    /**
     * 短UUID可用字符编码，一共57个字符，大小写字母+数字，排除掉易混字符1、l和I，0和o
     */
    private final char[] alphabet = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();

    private final BigInteger alphabetSize;

    /**
     * 短UUID长度
     */
    private final int shortUuidLength;

    private static final UuidGenerator INSTANCE = new UuidGenerator();

    /**
     * 生成22位的UUID，本质是将UUID（32位16进制整数）转换为57进制
     *
     * @return UUID
     */
    public static String generate() {
        return INSTANCE.doGenerate();
    }

    private UuidGenerator() {
        alphabetSize = BigInteger.valueOf(alphabet.length);
        shortUuidLength = (int) Math.ceil(32 * Math.log(16) / Math.log(alphabet.length));
    }

    /**
     * 生成22位的UUID，本质是将UUID（32位16进制整数）转换为57进制
     *
     * @return UUID
     */
    private String doGenerate() {
        String uuidStr = UUID.randomUUID().toString().replaceAll("-", "");
        BigInteger number = new BigInteger(uuidStr, 16);
        return encode(number);
    }

    /**
     * 将整数转换为57进制，不足22位前面补0（默认'2'代表0）
     *
     * @param number 整数
     * @return 编码
     */
    private String encode(BigInteger number) {
        BigInteger value = new BigInteger(number.toString());
        StringBuilder shortUuid = new StringBuilder();

        while (value.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] fracAndRemainder = value.divideAndRemainder(alphabetSize);
            shortUuid.append(alphabet[fracAndRemainder[1].intValue()]);
            value = fracAndRemainder[0];
        }

        int padding = shortUuidLength - shortUuid.length();
        for (int i = 0; i < padding; i++) {
            shortUuid.append(alphabet[0]);
        }

        return shortUuid.reverse().toString();
    }
}
