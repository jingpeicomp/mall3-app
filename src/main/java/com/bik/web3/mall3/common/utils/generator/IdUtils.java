package com.bik.web3.mall3.common.utils.generator;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * ID生成工具类
 *
 * @author Mingo.Liu
 * @date 2022-12-09
 */
@Slf4j
public class IdUtils {
    /**
     * 将byte数组转化为bit字符串
     *
     * @param value byte数组
     * @return bit字符串
     */
    public static String byteArrayToBits(byte[] value) {
        StringBuilder bits = new StringBuilder();
        for (int i = value.length - 1; i >= 0; i--) {
            byte byteValue = value[i];
            String currentBitString = ""
                    + (byte) ((byteValue >> 7) & 0x1) + (byte) ((byteValue >> 6) & 0x1)
                    + (byte) ((byteValue >> 5) & 0x1) + (byte) ((byteValue >> 4) & 0x1)
                    + (byte) ((byteValue >> 3) & 0x1) + (byte) ((byteValue >> 2) & 0x1)
                    + (byte) ((byteValue >> 1) & 0x1) + (byte) ((byteValue) & 0x1);
            bits.append(currentBitString);
        }

        return bits.toString();
    }

    /**
     * 获取店铺编码
     *
     * @param shopId      店铺ID
     * @param maxShopCode 最大店铺编号校验码
     * @return 店铺编码
     */
    public static Long getShopCode(String shopId, long maxShopCode) {
        long numberShopId = Long.parseLong(shopId, 16);
        String strNumberShopId = String.valueOf(numberShopId);
        int[] numbers = new int[strNumberShopId.length()];
        for (int i = 0; i < strNumberShopId.length(); i++) {
            numbers[i] = Character.getNumericValue(strNumberShopId.charAt(i));
        }
        for (int i = numbers.length - 1; i >= 0; i -= 2) {
            numbers[i] <<= 1;
            numbers[i] = numbers[i] / 10 + numbers[i] % 10;
        }

        int validationCode = 0;
        for (int number : numbers) {
            validationCode += number;
        }
        validationCode *= validationCode;
        validationCode %= maxShopCode;
        return validationCode < 2L ? validationCode + 2L : validationCode;
    }

    /**
     * 获取起始时间戳(单位秒)
     *
     * @param dateStr 时间字符串，格式为"yyyy-MM-dd HH:mm:ss"
     * @return 时间戳
     */
    public static long getTimeStampSecond(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(dateStr, formatter).atZone(ZoneId.systemDefault()).toEpochSecond();
        } catch (Exception e) {
            log.error("Cannot get time stamp string, the invalid date format is yyyy-MM-dd HH:mm:ss, please check!");
            return 1546272000L;
        }
    }

    /**
     * 当前线程sleep一段时间
     *
     * @param mills sleep毫秒数
     */
    public static void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            log.error("Sleep error ", e);
        }
    }

    /**
     * 获取校验码
     *
     * @param originId 原始数字
     * @param maxCode  最大校验码
     * @return 校验码
     */
    public static int getValidationCode(long originId, int maxCode) {
        String strOriginId = String.valueOf(originId);
        int[] numbers = new int[strOriginId.length()];
        for (int i = 0, length = strOriginId.length(); i < length; i++) {
            numbers[i] = Character.getNumericValue(strOriginId.charAt(i));
        }
        for (int i = numbers.length - 2; i >= 0; i -= 2) {
            numbers[i] <<= 1;
            numbers[i] = numbers[i] / 10 + numbers[i] % 10;
        }

        int validationCode = 0;
        for (int number : numbers) {
            validationCode += number;
        }
        validationCode *= 9;
        return validationCode % maxCode;
    }

    /**
     * 将二进制字符串补充到指定长度，不足部分用'0'填充在头部
     *
     * @param binaryString 二进制字符串
     * @param len          指定长度
     * @return 头部补'0'后的二进制字符串
     */
    private static String fillBinaryString(String binaryString, int len) {
        if (binaryString.length() > len) {
            throw new IllegalArgumentException("Value is too large");
        }

        if (binaryString.length() < len) {
            char[] appendChars = new char[len - binaryString.length()];
            Arrays.fill(appendChars, '0');
            return new String(appendChars) + binaryString;
        }

        return binaryString;
    }
}
