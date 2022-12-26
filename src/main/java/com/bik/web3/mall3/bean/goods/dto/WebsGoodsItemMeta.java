package com.bik.web3.mall3.bean.goods.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * web3商品Meta信息
 *
 * @author Mingo.Liu
 * @date 2022-12-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebsGoodsItemMeta implements Serializable {
    private String name;

    private String image;

    private String description;

    @JsonProperty("external_url")
    private String externalUrl;

    private List<MetaAttribute> attributes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetaAttribute implements Serializable {
        @JsonProperty("trait_type")
        private String key;

        private String value;
    }
}
