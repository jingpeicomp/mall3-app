package com.bik.web3.mall3.common.configure.request;

import com.bik.web3.mall3.common.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * http request日志类
 *
 * @author Mingo.Liu
 */
@Data
public class RequestLogBean implements Serializable {
    private static final Long serialVersionUID = 1L;
    /**
     * 接口调用时间
     */
    private String invokeTime = DateUtils.formatTime(LocalDateTime.now());

    /**
     * 来源IP
     */
    private String fromIp;

    /**
     * 访问耗时
     */
    private Long costTime;

    /**
     * 请求参数
     */
    private Param param;

    /**
     * 响应值
     */
    private Result result;

    /**
     * http请求类型 post get等
     */
    private String httpMethod;

    /**
     * 实际请求uri。譬如:/user/001
     */
    private String uri;

    /**
     * 请求数据包
     */
    private String packet;

    /**
     * 请求Mapping, 主要针对spring mvc。譬如: /user/{id}。方便统计归类
     */
    private String mapping;

    /**
     * 接口名称(通过@RequestMapping在每个Controller的handler方法上设置), 便于阅读和定位
     */
    private String mappingName;

    /**
     * 类名与方法名, 方便开发定义问题
     */
    private String clzMethod;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Param {
        /**
         * Http头
         */
        private Map<String, String> header;

        /**
         * Query String
         */
        private String queryStr;

        /**
         * request.getInputStream()
         */
        private String payload;

        /**
         * request.getParameterMap()
         */
        private String paramMap;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Result {

        /**
         * 响应http状态吗
         */
        private int status;

        /**
         * 响应内容
         */
        private String content;

        /**
         * 异常信息, 只含message
         */
        private String exceptionMsg;
    }
}
