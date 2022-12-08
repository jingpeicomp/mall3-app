package com.bik.web3.mall3.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页响应结果
 *
 * @author Mingo.Liu
 */
@ApiModel("分页响应结果")
@Data
public class PageResult<T> implements Serializable {

    @ApiModelProperty("符合条件的数据数目")
    private long total = 0L;

    @ApiModelProperty("数据列表")
    private List<T> content;

    public PageResult() {
        this(new ArrayList<>(0));
    }

    public PageResult(List<T> content, long total) {
        this.total = total;
        this.content = content;
    }

    public PageResult(List<T> content) {
        this.content = content;
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>(Collections.emptyList(), 0L);
    }

    public <R> PageResult<R> map(Function<T, R> function) {
        if (CollectionUtils.isEmpty(content)) {
            return empty();
        }

        List<R> convertedContent = content.stream().map(function).collect(Collectors.toList());
        return new PageResult<>(convertedContent, total);
    }
}
