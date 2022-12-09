package com.bik.web3.mall3.common.configure.request;

import com.bik.web3.mall3.common.annotation.ApiDefinition;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * RequestLog切面, 与RequestLogFilter相互配合, 让数据记录更完整
 *
 * @author Mingo.Liu
 */
@Aspect
@Slf4j
public class RequestLogAspect {

    @Around(value = "(@within(org.springframework.stereotype.Controller) " +
            "|| @within(org.springframework.web.bind.annotation.RestController)) " +
            "&& (@annotation(apiDefinition) )",
            argNames = "joinPoint,apiDefinition")
    public Object logAround(ProceedingJoinPoint joinPoint, ApiDefinition apiDefinition) throws Throwable {
        String clzMethod = null;
        try {
            Signature signature = joinPoint.getSignature();
            clzMethod = new StringJoiner(".").add(signature.getDeclaringTypeName()).add(signature.getName()).toString();
            log.debug("{} in", clzMethod);
            recordMapping(clzMethod, apiDefinition);
            return joinPoint.proceed();
        } catch (Throwable e) {
            String exMsg = null == e.getCause() ? e.getMessage() : e.getCause().getMessage();
            MDC.put("reqLog_exMsg", exMsg);
            log.debug("{}发生异常, exMsg: {}", clzMethod, exMsg);
            throw e;
        }
    }

    /**
     * 记录request mapping相关信息到MDC
     *
     * @param clzMethod     方法名
     * @param apiDefinition api接口定义
     */
    private void recordMapping(String clzMethod, ApiDefinition apiDefinition) {
        try {
            String[] path = apiDefinition.path();
            path = (path.length == 0) ? apiDefinition.value() : path;
            String mapping = Arrays.stream(path).collect(Collectors.joining(",", "[", "]"));

            RequestMethod[] mappingMethods = apiDefinition.method();
            String mappingMethodsStr = Arrays.stream(mappingMethods).map(RequestMethod::toString).collect(Collectors.joining(",", "[", "]"));
            String mappingName = apiDefinition.name();

            // 记录接口名, 便于问题定位
            MDC.put("reqLog_clzMethod", clzMethod);
            MDC.put("reqLog_mapping", String.format("%s %s", mappingMethodsStr, mapping));
            MDC.put("reqLog_mappingName", mappingName);
        } catch (Exception e) {
            log.info("Record controller mapping error {}", clzMethod, e);
        }
    }
}
