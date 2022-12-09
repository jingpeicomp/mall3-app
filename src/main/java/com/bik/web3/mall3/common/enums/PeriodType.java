package com.bik.web3.mall3.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.AttributeConverter;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 套餐周期类型
 *
 * @author Mingo.Liu
 * @date 2022-12-09
 */
public enum PeriodType {
    /**
     * 应用包
     */
    Month(1, "月卡(30天)"),

    /**
     * 应用
     */
    YEAR(2, "年卡(365天)");

    /**
     * 枚举对应的值，主要用于数据库和前端，提高效率
     */
    private final int value;

    /**
     * 枚举对应的值，方便理解和配置
     */
    private final String display;

    PeriodType(int value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator
    public static PeriodType fromValue(int value) {
        return Stream.of(values())
                .filter(status -> status.value == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的套餐周期类型值:" + value));
    }

    public static PeriodType fromDisplay(String display) {
        return Stream.of(values())
                .filter(status -> Objects.equals(status.display, display))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的套餐周期类型显示名称:" + display));
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public String getDisplay() {
        return display;
    }

    public static class EnumConvert implements AttributeConverter<PeriodType, Integer> {
        @Override
        public Integer convertToDatabaseColumn(PeriodType attribute) {
            return attribute.getValue();
        }

        @Override
        public PeriodType convertToEntityAttribute(Integer dbData) {
            return PeriodType.fromValue(dbData);
        }
    }
}
