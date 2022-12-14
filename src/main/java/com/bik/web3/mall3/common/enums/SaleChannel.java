package com.bik.web3.mall3.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.AttributeConverter;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 销售渠道
 *
 * @author Mingo.Liu
 * @date 2022-12-09
 */
public enum SaleChannel {
    /**
     * 应用包
     */
    Web2(1, "web2"),

    /**
     * 应用
     */
    Web3(2, "web3");

    /**
     * 枚举对应的值，主要用于数据库和前端，提高效率
     */
    private final int value;

    /**
     * 枚举对应的值，方便理解和配置
     */
    private final String display;

    SaleChannel(int value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator
    public static SaleChannel fromValue(int value) {
        return Stream.of(values())
                .filter(status -> status.value == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的销售渠道类型值:" + value));
    }

    public static SaleChannel fromDisplay(String display) {
        return Stream.of(values())
                .filter(status -> Objects.equals(status.display, display))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的销售渠道类型显示名称:" + display));
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public String getDisplay() {
        return display;
    }

    public static class EnumConvert implements AttributeConverter<SaleChannel, Integer> {
        @Override
        public Integer convertToDatabaseColumn(SaleChannel attribute) {
            return attribute.getValue();
        }

        @Override
        public SaleChannel convertToEntityAttribute(Integer dbData) {
            return SaleChannel.fromValue(dbData);
        }
    }
}
