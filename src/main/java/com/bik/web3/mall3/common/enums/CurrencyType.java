package com.bik.web3.mall3.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.AttributeConverter;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 货币种类
 *
 * @author Mingo.Liu
 * @date 2022-12-09
 */
public enum CurrencyType {
    /**
     * VCOIN
     */
    VCOIN(1, "Vcoin"),

    /**
     * 以太币
     */
    ETH_COIN(2, "ETH");

    /**
     * 枚举对应的值，主要用于数据库和前端，提高效率
     */
    private final int value;

    /**
     * 枚举对应的值，方便理解和配置
     */
    private final String display;

    CurrencyType(int value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator
    public static CurrencyType fromValue(int value) {
        return Stream.of(values())
                .filter(status -> status.value == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的货币种类类型值:" + value));
    }

    public static CurrencyType fromDisplay(String display) {
        return Stream.of(values())
                .filter(status -> Objects.equals(status.display, display))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的货币种类类型显示名称:" + display));
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public String getDisplay() {
        return display;
    }

    public static class EnumConvert implements AttributeConverter<CurrencyType, Integer> {
        @Override
        public Integer convertToDatabaseColumn(CurrencyType attribute) {
            return attribute.getValue();
        }

        @Override
        public CurrencyType convertToEntityAttribute(Integer dbData) {
            return CurrencyType.fromValue(dbData);
        }
    }
}
