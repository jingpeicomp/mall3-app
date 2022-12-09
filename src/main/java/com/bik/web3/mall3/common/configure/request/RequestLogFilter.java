package com.bik.web3.mall3.common.configure.request;

import com.bik.web3.mall3.common.configure.request.web.HttpServletRequestCopier;
import com.bik.web3.mall3.common.configure.request.web.HttpServletResponseCopier;
import com.bik.web3.mall3.common.utils.HttpUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * http请求日志过滤器
 *
 * @author Mingo.Liu
 */
@Slf4j
public class RequestLogFilter extends GenericFilterBean {
    private static final String SEPARATOR = ",";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Logger REQUEST_LOG = LoggerFactory.getLogger("requestLog");

    private static final String SUB_SUFFIX = "...(source length is %s)";

    /**
     * 是否需要记录参数, 包括queryString、parameterMap、headers
     */
    private boolean needParam = true;

    /**
     * 是否需要记录响应结果
     */
    private boolean needResult = false;

    private int maxResultLength = 500;

    private int maxBodyLength = 500;

    /**
     * 不希望记录日志的URL Pattern, 如果有多个可以采用英文半角逗号(",")分隔; 单个Pattern语法支持AntPathMatcher匹配。
     */
    private String[] excludePatterns;

    /**
     * 不需要记录日志的请求method。譬如通常可以忽略的method: OPTIONS
     */
    private String[] excludeMethods;

    /**
     * 不希望记录日志的Header, 如果有多个可以采用英文半角逗号(",")分隔;
     */
    private String[] excludeHeaders;

    /**
     * 希望记录日志的Header, 如果有多个可以采用英文半角逗号(",")分隔; 配置此字段时，excludeHeaders将不生效
     */
    private String[] includeHeaders;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 初始参数设置
     */
    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();

        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        FilterConfig filterConfig = this.getFilterConfig();
        if (null == filterConfig) {
            return;
        }

        String needResultStr = filterConfig.getInitParameter("NEED_RESULT");
        needResult = Boolean.parseBoolean(needResultStr);

        String needParamStr = filterConfig.getInitParameter("NEED_PARAM");
        needParam = Boolean.parseBoolean(needParamStr);

        String maxResultLengthStr = filterConfig.getInitParameter("MAX_RESULT_LENGTH");
        if (StringUtils.isBlank(maxResultLengthStr)) {
            maxResultLength = Integer.parseInt(maxResultLengthStr);
        }

        String maxBodyLengthStr = filterConfig.getInitParameter("MAX_BODY_LENGTH");
        if (StringUtils.isBlank(maxBodyLengthStr)) {
            maxBodyLength = Integer.parseInt(maxBodyLengthStr);
        }

        String initExcludePatterns = filterConfig.getInitParameter("EXCLUDE_PATTERNS");
        if (StringUtils.isBlank(initExcludePatterns)) {
            excludePatterns = initExcludePatterns.split(SEPARATOR);
        }

        String initExcludeMethods = filterConfig.getInitParameter("EXCLUDE_METHODS");
        if (StringUtils.isBlank(initExcludeMethods)) {
            excludeMethods = initExcludeMethods.split(SEPARATOR);
        }

        String initExcludeHeaders = filterConfig.getInitParameter("EXCLUDE_HEADERS");
        if (StringUtils.isBlank(initExcludeHeaders)) {
            excludeHeaders = initExcludeHeaders.split(SEPARATOR);
        }

        String initIncludeHeaders = filterConfig.getInitParameter("INCLUDE_HEADERS");
        if (StringUtils.isBlank(initIncludeHeaders)) {
            includeHeaders = initIncludeHeaders.split(SEPARATOR);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            String requestUri = httpRequest.getRequestURI();
            String method = httpRequest.getMethod();

            if (isExcludePath(requestUri) || isExcludeMethod(method)) {
                // 如果是排除的URI或排除的Method则不记录日志, 直接放行
                chain.doFilter(request, response);
            } else {
                RequestLogBean logBean = new RequestLogBean();

                // 非排除的uri则记录日志
                long startTimeMills = System.currentTimeMillis();

                if (null == response.getCharacterEncoding()) {
                    response.setCharacterEncoding("UTF-8");
                }

                HttpServletResponseCopier responseCopier = null;
                HttpServletRequestCopier requestCopier = null;
                try {
                    responseCopier = new HttpServletResponseCopier((HttpServletResponse) response);
                    requestCopier = new HttpServletRequestCopier((HttpServletRequest) request);
                    chain.doFilter(requestCopier, responseCopier);
                } finally {
                    try {
                        // 在方法执行完成后再收集日志信息, 缩短logMap生命周期, 以提高性能耗时
                        logBean.setCostTime(System.currentTimeMillis() - startTimeMills);
                        logBean.setUri(requestUri);
                        logBean.setHttpMethod(method);
                        logBean.setFromIp(HttpUtils.getRealRemoteIp((HttpServletRequest) request));
                        // see RequestLogAspect
                        logBean.setMapping(MDC.get("reqLog_mapping"));
                        logBean.setMappingName(MDC.get("reqLog_mappingName"));
                        logBean.setClzMethod(MDC.get("reqLog_clzMethod"));
                        String exMsg = MDC.get("reqLog_exMsg");

                        // 封装参数
                        if (needParam && null != requestCopier) {
                            logBean.setParam(getParam(requestCopier));
                        }

                        // 封装结果集
                        RequestLogBean.Result result = new RequestLogBean.Result();
                        result.setStatus(((HttpServletResponse) response).getStatus());
                        if (needResult && null != responseCopier) {
                            String responseContent = getOutputParamJsonStr(responseCopier);
                            result.setContent(truncString(responseContent, maxResultLength));
                        }
                        result.setExceptionMsg(exMsg);
                        logBean.setResult(result);

                        // 根据异常消息判断日志级别
                        recordLog(logBean, StringUtils.isNotBlank(exMsg));
                        MDC.clear(); // 清空MDC
                    } catch (Exception ex) {
                        log.info("记录request log处理异常", ex);
                    }
                }
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * 判断是否为排除的uri
     *
     * @param requestUri 需判定的uri
     */
    private boolean isExcludePath(String requestUri) {
        if (null == excludePatterns) {
            return false;
        }

        for (String pattern : this.excludePatterns) {
            if (pathMatcher.match(pattern.trim(), requestUri)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否为排除的method
     *
     * @param method request method(GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE)
     */
    private boolean isExcludeMethod(String method) {
        if (null == excludeMethods) {
            return false;
        }
        for (String excludeMethod : excludeMethods) {
            if (excludeMethod.equalsIgnoreCase(method)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否为排除的header
     */
    private boolean isExcludeHeaders(String header) {
        if (null == excludeHeaders) {
            return false;
        }
        for (String excludeHeader : excludeHeaders) {
            if (excludeHeader.trim().equalsIgnoreCase(header)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否为包含的header
     *
     * @param header header
     */
    private boolean isIncludeHeaders(String header) {
        if (null == includeHeaders) {
            return true;
        }
        for (String includeHeader : includeHeaders) {
            if (includeHeader.trim().equalsIgnoreCase(header)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取参数
     *
     * @param requestCopier requestCopier
     */
    private RequestLogBean.Param getParam(HttpServletRequestCopier requestCopier) {
        // get paramMap
        String paramMap = null;
        try {
            paramMap = OBJECT_MAPPER.writeValueAsString(requestCopier.getParameterMap());
        } catch (JsonProcessingException e) {
            // do nothing, ignore
            log.info("记录request log日志JSON处理异常", e);
        }

        // get queryString
        String queryStr = requestCopier.getQueryString();
        try {
            queryStr = null != queryStr ? URLDecoder.decode(queryStr, requestCopier.getCharacterEncoding()) : null;
        } catch (UnsupportedEncodingException e) {
            log.debug("URL decode [{}] by [{}] failed.", queryStr, requestCopier.getCharacterEncoding(), e);
        }

        // get header
        Enumeration<String> headers = requestCopier.getHeaderNames();
        Map<String, String> headersMap = new HashMap<>();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();

            // 如果配置了includeHeaders
            if (null != includeHeaders && includeHeaders.length > 0) {
                if (isIncludeHeaders(header)) {
                    // 只记录包含的header
                    headersMap.put(header, requestCopier.getHeader(header));
                }
            } else {
                // 如果没有配置includeHeaders，则按排除方式处理
                if (!isExcludeHeaders(header)) {
                    // 判断header是否需要记录
                    headersMap.put(header, requestCopier.getHeader(header));
                }
            }
        }

        // get payload
        String payload = getRequestBody(requestCopier);
        // 超长截取
        payload = truncString(payload, maxBodyLength);

        return new RequestLogBean.Param(headersMap, queryStr, payload, paramMap);
    }

    /**
     * 记录日志
     *
     * @param requestLogBean http请求日志对象
     * @param hasError       是否有异常
     */
    private void recordLog(RequestLogBean requestLogBean, boolean hasError) throws JsonProcessingException {
        if (hasError) {
            REQUEST_LOG.error(OBJECT_MAPPER.writeValueAsString(requestLogBean));
        } else if (null != requestLogBean.getResult() && requestLogBean.getResult().getStatus() >= 500) {
            REQUEST_LOG.error(OBJECT_MAPPER.writeValueAsString(requestLogBean));
        } else {
            REQUEST_LOG.info(OBJECT_MAPPER.writeValueAsString(requestLogBean));
        }
    }

    /**
     * 获取http response 响应结果
     *
     * @param responseCopier HttpServletResponseCopier
     * @return 响应json
     */
    private String getOutputParamJsonStr(HttpServletResponseCopier responseCopier) {
        String contentType = responseCopier.getContentType();

        // 只记录文本
        try {
            responseCopier.flushBuffer();
            byte[] copy = responseCopier.getCopy();

            if (null == contentType || contentType.contains("text") || contentType.contains("json")) {
                return new String(copy, responseCopier.getCharacterEncoding());
            } else {
                return String.format("无法获取内容。contentType:%s, contentLength:%s bytes", contentType, copy.length);
            }
        } catch (Exception e) {
            log.warn("http接口日志返回值封装失败", e);
        }
        return null;
    }

    /**
     * 截断字符串
     *
     * @param str       原始字符串
     * @param maxLength 最大长度
     * @return 截断后的字符
     */
    private String truncString(String str, int maxLength) {
        if (null == str) {
            return null;
        }

        if (str.length() > maxLength) {
            String subSuffix = String.format(SUB_SUFFIX, str.length());
            return str.substring(0, maxLength - subSuffix.length()) + subSuffix;
        }

        return str;
    }

    private String getRequestBody(HttpServletRequestCopier requestCopier) {
        try {
            if (null != requestCopier.getContentType()
                    && requestCopier.getContentType().contains("multipart/form-data")) {
                // 过滤掉文件上传
                return "multipart/form-data, contentLength is: " + requestCopier.getContentLength();
            }

            ServletInputStream servletInputStream = requestCopier.getInputStream();
            byte[] bytes = requestCopier.getCopy();
            if (null == bytes || bytes.length == 0) {
                // 判断request流是否读取过
                if (servletInputStream.isFinished()) {
                    bytes = requestCopier.getCopy();
                } else {
                    bytes = convertInputStreamToBytes(servletInputStream);
                }
            }

            bytes = null == bytes ? new byte[0] : bytes;

            String charset = requestCopier.getCharacterEncoding();
            if (null != charset) {
                return new String(bytes, charset);
            } else {
                return new String(bytes);
            }
        } catch (Exception ex) {
            log.warn("http接口日志参数体封装失败", ex);
        }

        return "";
    }

    private byte[] convertInputStreamToBytes(InputStream input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int n;

        try {
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.info("convertInputStreamToBytes failed.", e);
            }
        }
        return output.toByteArray();
    }
}
