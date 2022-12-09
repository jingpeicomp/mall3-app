package com.bik.web3.mall3.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期工具类
 *
 * @author Mingo.Liu
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class DateUtils {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 格式化时间 ，默认格式为："yyyy-MM-dd HH:mm:ss.SSS"
     *
     * @param localDateTime 时间
     * @return 时间字符串
     */
    public static String formatTime(final LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return null;
        }
        return DATE_TIME_FORMATTER.format(localDateTime);
    }
}
