package com.bik.web3.mall3.common.exception;

import com.bik.web3.mall3.common.utils.MessageSourceUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 根异常
 *
 * @author Mingo.Liu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Mall3Exception extends RuntimeException {
    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 异常信息
     */
    private String detail;

    public Mall3Exception(String errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.detail = MessageSourceUtils.getMessage(errorCode, null, LocaleContextHolder.getLocale());
    }

    public Mall3Exception(Throwable cause) {
        this(ResultCodes.FAILED, cause);
        this.detail = MessageSourceUtils.getMessage(errorCode, null, LocaleContextHolder.getLocale());
    }

    public Mall3Exception(String errorCode) {
        super();
        this.errorCode = errorCode;
        this.detail = MessageSourceUtils.getMessage(errorCode, null, LocaleContextHolder.getLocale());
    }

    public Mall3Exception(String errorCode, String message) {
        super();
        this.errorCode = errorCode;
        if (StringUtils.isNotBlank(message)) {
            this.detail = MessageSourceUtils.getMessage(errorCode, null, LocaleContextHolder.getLocale()) + "," + message;
        } else {
            this.detail = MessageSourceUtils.getMessage(errorCode, null, LocaleContextHolder.getLocale());
        }
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder("Error code: ");
        message.append(errorCode);
        if (StringUtils.isNotBlank(detail)) {
            message.append(",detail: ").append(detail);
        }
        return message.toString();
    }
}
