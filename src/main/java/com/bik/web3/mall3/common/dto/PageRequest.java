package com.bik.web3.mall3.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 分页请求
 *
 * @author Mingo.Liu
 */
@ApiModel("分页请求")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PageRequest extends BaseRequest {
    /**
     * 页码，从0开始
     */
    @ApiModelProperty("页码，从0开始")
    @Builder.Default
    private int pageNumber = 0;

    /**
     * 每页大小
     */
    @ApiModelProperty("每页大小")
    @Builder.Default
    private int pageSize = 20;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Sort sort;

    public PageRequest(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public long getOffset() {
        return (long) pageNumber * (long) pageSize;
    }

    public org.springframework.data.domain.PageRequest toSpringPageRequest() {
        if (null != sort) {
            return org.springframework.data.domain.PageRequest.of(pageNumber, pageSize, sort.toSpringSort());
        } else {
            return org.springframework.data.domain.PageRequest.of(pageNumber, pageSize);
        }
    }

    public void initDefaultSort() {
        if (null == sort) {
            sort = Sort.by(Sort.Direction.DESC, "id");
        }
    }
}
