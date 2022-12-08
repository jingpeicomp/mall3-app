package com.bik.web3.mall3.common.dto;

import com.bik.web3.mall3.common.exception.ResultCodes;
import com.bik.web3.mall3.common.utils.MessageSourceUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serializable;

/**
 * API响应结果基类，所有的请求响应结果都是此类
 *
 * @author Mingo.Liu
 */
@ApiModel(description = "API响应结果基类")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> implements Serializable {
    /**
     * 结果码
     */
    @ApiModelProperty(value = "结果码")
    private String code;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String message;

    /**
     * 错误内容
     */
    @ApiModelProperty(value = "错误内容")
    private Object errorData;

    /**
     * 内容
     */
    @ApiModelProperty(value = "内容")
    private T data;

    /**
     * 构造无返回数据的成功响应结果
     *
     * @param <T> 泛型
     * @return 无返回数据的成功响应结果
     */
    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(ResultCodes.SUCCESSFUL, null, null, null);
    }

    /**
     * 构造带返回数据的成功响应结果
     *
     * @param <T> 泛型
     * @return 带返回数据的成功响应结果
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(ResultCodes.SUCCESSFUL, null, null, data);
    }

    /**
     * 构造通用失败响应结果
     *
     * @param <T> 泛型
     * @return 通用失败响应结果
     */
    public static <T> BaseResponse<T> error() {
        return new BaseResponse<>(ResultCodes.FAILED, null, null, null);
    }

    /**
     * 构造错误码失败响应结果
     *
     * @param errorCode 错误码
     * @param <T>       泛型
     * @return 错误码失败响应结果
     */
    public static <T> BaseResponse<T> error(String errorCode) {
        return new BaseResponse<>(errorCode, MessageSourceUtils.getMessage(errorCode, null, LocaleContextHolder.getLocale()), null, null);
    }

    /**
     * 构造错误码和错误信息失败响应结果
     *
     * @param errorCode 错误码
     * @param message   错误信息
     * @param <T>       泛型
     * @return 错误码和错误信息失败响应结果
     */
    public static <T> BaseResponse<T> error(String errorCode, String message) {
        return error(errorCode, message, null);
    }

    /**
     * 构造错误码和错误信息失败响应结果，不附加错误码国际化信息
     *
     * @param errorCode 错误码
     * @param message   错误信息
     * @param <T>       泛型
     * @return 错误码和错误信息失败响应结果
     */
    public static <T> BaseResponse<T> errorNotFormatCode(String errorCode, String message) {
        return new BaseResponse<>(errorCode, message, null, null);
    }

    /**
     * 构造带错误数据的失败响应结果
     *
     * @param errorCode 错误码
     * @param message   错误信息
     * @param obj       错误数据
     * @return 失败响应结果
     */
    public static <T> BaseResponse<T> error(String errorCode, String message, Object obj) {
        String errorCodeMessage = MessageSourceUtils.getMessage(errorCode, null, LocaleContextHolder.getLocale());
        String errorMsg = (StringUtils.isNotBlank(errorCodeMessage) ? errorCodeMessage : "") + (StringUtils.isNotBlank(message) ? message : "");
        return new BaseResponse<>(errorCode, errorMsg, obj, null);
    }

    /**
     * 构造带错误数据的失败响应结果
     *
     * @param errorCode 错误码
     * @param message   错误信息
     * @param obj       错误数据
     * @param args      错误信息填充数据
     * @return 失败响应结果
     */
    public static <T> BaseResponse<T> error(String errorCode, String message, Object obj, Object... args) {
        String errorCodeMessage = MessageSourceUtils.getMessage(errorCode, args, LocaleContextHolder.getLocale());
        String errorMsg = (StringUtils.isNotBlank(errorCodeMessage) ? errorCodeMessage : "") + (StringUtils.isNotBlank(message) ? message : "");
        return new BaseResponse<>(errorCode, errorMsg, obj, null);
    }

    public boolean isSuccess() {
        return ResultCodes.SUCCESSFUL.equals(this.code);
    }
}
