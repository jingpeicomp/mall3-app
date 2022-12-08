package com.bik.web3.mall3.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.Assert;

import java.util.Locale;

/**
 * 消息资源工具类
 *
 * @author Mingo.Liu
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageSourceUtils {
    private static MessageSource messageSource;

    static {
        init(BeanFactory.getBean(MessageSource.class));
    }

    public static void init(MessageSource messageSource) {
        MessageSourceUtils.messageSource = messageSource;
    }

    public static String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        Assert.notNull(messageSource, "Message source utils not init ...");
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }

    public static String getMessage(String code, Object[] args, Locale locale) {
        Assert.notNull(messageSource, "Message source utils not init ...");
        try {
            return messageSource.getMessage(code, args, locale);
        } catch (NoSuchMessageException e) {
            return null;
        }
    }

    public static String getMessage(MessageSourceResolvable resolvable, Locale locale) {
        Assert.notNull(messageSource, "Message source utils not init ...");
        try {
            return messageSource.getMessage(resolvable, locale);
        } catch (NoSuchMessageException e) {
            return null;
        }
    }
}
