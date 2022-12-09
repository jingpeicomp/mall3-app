package com.bik.web3.mall3.common.configure.request;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;

/**
 * Request日志配置
 *
 * @author Mingo.Liu
 */
@ConfigurationProperties(prefix = "mall3.log.request")
@Data
public class RequestLogProperties {
    private static final String[] DEFAULT_URL_MAPPINGS = {"/api/*"};

    private static final String DEFAULT_EXCLUDE_URL_MAPPINGS = "/static/**";

    /**
     * 是否启用Request日志
     */
    private boolean enable = true;

    /**
     * 是否记录请求参数；默认：true;
     */
    private boolean needParam = false;

    /**
     * 是否记录响应结果；默认：false;
     */
    private boolean needResult = false;

    /**
     * 最大返回结果长度，超过此长度记录时会被截取
     */
    private int maxResultLength = 500;

    /**
     * 最大请求参数体长度，超过此长度记录时会被截取
     */
    private int maxBodyLength = 500;

    /**
     * 过滤器路径, 多个可以采用英文半角逗号(",")分隔。单个Pattern的语法遵循Filter的规则。
     */
    private String[] urlPatterns = DEFAULT_URL_MAPPINGS;

    /**
     * 过滤器加载顺序，数字越小优先级越高，可以是负数；默认为第二优先级, 给EncodingFilter、AuthClearFilter预留位置
     */
    private int order = Ordered.HIGHEST_PRECEDENCE + 3;

    /**
     * 不希望记录日志的URL Pattern, 如果有多个可以采用英文半角逗号(",")分隔; 单个Pattern语法支持AntPathMatcher匹配。
     */
    private String excludePatterns = DEFAULT_EXCLUDE_URL_MAPPINGS;

    /**
     * 不希望记录日志的Method, 如果有多个可以采用英文半角逗号(",")分隔;
     * Request Method: GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
     */
    private String excludeMethods = "OPTIONS,HEAD";

    /**
     * 不希望记录日志的Header, 如果有多个可以采用英文半角逗号(",")分隔; 默认排除鉴权字段。
     */
    private String excludeHeaders = "";

    /**
     * 希望记录日志的Header, 如果有多个可以采用英文半角逗号(",")分隔; 配置此字段时，excludeHeaders将不生效
     */
    private String includeHeaders = "";
}
