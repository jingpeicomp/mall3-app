package com.bik.web3.mall3.common.exception;

import com.bik.web3.mall3.common.dto.BaseResponse;
import com.bik.web3.mall3.common.utils.MessageSourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * 统一异常处理
 *
 * @author Mingo.Liu
 */
@ControllerAdvice
@Slf4j
public class Mall3ExceptionHandler {
    /**
     * 请求参数缺失异常
     *
     * @param exception 异常
     * @return BaseResponse
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public BaseResponse<Object> missingParam(MissingServletRequestParameterException exception) {
        log.error("Missing param ", exception);
        String parameterName = exception.getParameterName();
        String parameterType = exception.getParameterType();
        String message = MessageSourceUtils.getMessage(ResultCodes.MISSING_SERVLET_REQUEST_PARAMETER,
                new String[]{parameterName, parameterType}, LocaleContextHolder.getLocale());
        return BaseResponse.error(ResultCodes.MISSING_SERVLET_REQUEST_PARAMETER, message);
    }

    /**
     * HttpMessageConverter转化异常，一般为json解析异常
     *
     * @param exception 异常
     * @return BaseResponse
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public BaseResponse<Object> httpMessageNotReadable(HttpMessageNotReadableException exception) {
        log.error("Message not readable ", exception);
        return BaseResponse.error(ResultCodes.HTTP_MESSAGE_CONVERTER_ERROR);
    }

    /**
     * 拦截不支持媒体类型异常
     *
     * @param exception 异常
     * @return BaseResponse
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public BaseResponse<Object> httpMediaTypeNotSupport(HttpMediaTypeNotSupportedException exception) {
        log.error("Media type not support", exception);
        return BaseResponse.error(ResultCodes.HTTP_MEDIA_TYPE_NOT_SUPPORT);
    }

    /**
     * 不受支持的http method
     *
     * @param request 请求
     * @return BaseResponse
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public BaseResponse<Object> methodNotSupport(HttpServletRequest request) {
        String httpMethod = request.getMethod().toUpperCase();
        log.error("Not support http method {} {}", httpMethod, request.getRequestURI());
        return BaseResponse.error(ResultCodes.HTTP_METHOD_NOT_SUPPORT, null, null, httpMethod);
    }

    /**
     * 404找不到资源
     *
     * @param exception 异常
     * @return BaseResponse
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public BaseResponse<Object> notFound(NoHandlerFoundException exception) {
        log.error("Not found", exception);
        return BaseResponse.error(ResultCodes.DATA_NOT_EXISTS);
    }

    /**
     * 请求参数校验失败，拦截 @Valid 校验失败的情况
     *
     * @param exception 异常
     * @return BaseResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public BaseResponse<Object> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("Param invalid", exception);
        String bindingResult = getArgNotValidMessage(exception.getBindingResult());
        return BaseResponse.error(ResultCodes.PARAMETER_ERROR, bindingResult);
    }

    /**
     * 请求参数校验失败，拦截 @Validated 校验失败的情况
     * <p>
     * 两个注解 @Valid 和 @Validated 区别是后者可以加分组校验，前者没有分组校验
     *
     * @param exception 异常
     * @return BaseResponse
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public BaseResponse<Object> bindException(BindException exception) {
        log.error("Param invalid", exception);
        String bindingResult = getArgNotValidMessage(exception.getBindingResult());
        return BaseResponse.error(ResultCodes.PARAMETER_ERROR, bindingResult);
    }

    /**
     * 业务异常
     *
     * @param exception 业务异常
     * @return BaseResponse
     */
    @ExceptionHandler(Mall3Exception.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public BaseResponse<Object> businessException(Mall3Exception exception) {
        log.error("Business exception", exception);
        return BaseResponse.errorNotFormatCode(exception.getErrorCode(), exception.getDetail());
    }

    /**
     * 其它异常
     *
     * @param exception 其它异常
     * @return BaseResponse
     */
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public BaseResponse<Object> serverError(Throwable exception) {
        log.error("Server error", exception);
        return BaseResponse.error(ResultCodes.FAILED);
    }

    /**
     * 获取请求参数不正确的提示信息，多个信息，拼接成用逗号分隔的形式
     */
    private String getArgNotValidMessage(BindingResult bindingResult) {
        if (null == bindingResult) {
            return "";
        }

        return bindingResult.getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(","));
    }
}
