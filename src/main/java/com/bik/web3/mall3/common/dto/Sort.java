package com.bik.web3.mall3.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 排序信息
 *
 * @author Mingo.Liu
 */
@ApiModel("排序信息")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class Sort implements Serializable {
    /**
     * 排序字段列表
     */
    @ApiModelProperty("排序字段列表")
    private List<Order> orders;

    /**
     * 将sort对象转换为spring data sort对象，spring sort对象无法通过json序列化
     *
     * @return spring data sort对象
     */
    public org.springframework.data.domain.Sort toSpringSort() {
        if (CollectionUtils.isEmpty(orders)) {
            return null;
        }

        List<org.springframework.data.domain.Sort.Order> springOrders = orders.stream()
                .map(Order::toSpringOrder)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(springOrders)) {
            return null;
        }

        return org.springframework.data.domain.Sort.by(springOrders);
    }

    public static Sort by(Direction direction, String... properties) {
        Assert.notNull(direction, "Direction must not be null!");
        Assert.notNull(properties, "Properties must not be null!");
        Assert.isTrue(properties.length > 0, "At least one property must be given!");

        List<Order> orders = Arrays.stream(properties)
                .map(it -> new Order(it, direction.name()))
                .collect(Collectors.toList());
        return new Sort(orders);
    }

    /**
     * 单字段排序信息
     */
    @ApiModel("单个字段排序信息，包含字段名和顺序")
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Order implements Serializable {
        /**
         * 排序字段名
         */
        @ApiModelProperty("排序字段名")
        private String property;

        /**
         * 排序顺序，升序：asc；降序：desc
         */
        @ApiModelProperty("排序顺序。升序：asc；降序：desc")
        private String direction;

        public org.springframework.data.domain.Sort.Order toSpringOrder() {
            if (StringUtils.isBlank(property) || StringUtils.isBlank(direction)) {
                return null;
            }

            try {
                org.springframework.data.domain.Sort.Direction direction = org.springframework.data.domain.Sort.Direction.fromString(getDirection());
                return new org.springframework.data.domain.Sort.Order(direction, property);
            } catch (Exception e) {
                log.error("Invalid direction input {}", this);
                return null;
            }

        }
    }

    public enum Direction {
        /**
         * 升序
         */
        ASC,

        /**
         * 降序
         */
        DESC;
    }
}
