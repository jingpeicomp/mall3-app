package com.bik.web3.mall3.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.AttributeConverter;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 套餐设备类型
 *
 * @author Mingo.Liu
 * @date 2022-12-09
 */
public enum DeviceType {
    /**
     * 应用包
     */
    SINGLE(1, "个人(单设备)"),

    /**
     * 应用
     */
    HOME(2, "家庭(4设备)");

    /**
     * 枚举对应的值，主要用于数据库和前端，提高效率
     */
    private final int value;

    /**
     * 枚举对应的值，方便理解和配置
     */
    private final String display;

    DeviceType(int value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator
    public static DeviceType fromValue(int value) {
        return Stream.of(values())
                .filter(status -> status.value == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的套餐设备类型类型值:" + value));
    }

    public static DeviceType fromDisplay(String display) {
        return Stream.of(values())
                .filter(status -> Objects.equals(status.display, display))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的套餐设备类型类型显示名称:" + display));
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public String getDisplay() {
        return display;
    }

    public static class EnumConvert implements AttributeConverter<DeviceType, Integer> {
        @Override
        public Integer convertToDatabaseColumn(DeviceType attribute) {
            return attribute.getValue();
        }

        @Override
        public DeviceType convertToEntityAttribute(Integer dbData) {
            return DeviceType.fromValue(dbData);
        }
    }
}
