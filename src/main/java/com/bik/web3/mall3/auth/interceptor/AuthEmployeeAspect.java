package com.bik.web3.mall3.auth.interceptor;

import com.bik.web3.mall3.auth.context.AuthContext;
import com.bik.web3.mall3.bean.user.dto.UserDTO;
import com.bik.web3.mall3.common.annotation.ApiDefinition;
import com.bik.web3.mall3.common.dto.BaseRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 员工切面
 *
 * @author Mingo.Liu
 */
@Aspect
@Slf4j
public class AuthEmployeeAspect {
    @Around(value = "(@within(org.springframework.stereotype.Controller) " +
            "|| @within(org.springframework.web.bind.annotation.RestController)) " +
            "&& (@annotation(apiDefinition) )",
            argNames = "joinPoint,apiDefinition")
    public Object logAround(ProceedingJoinPoint joinPoint, ApiDefinition apiDefinition) throws Throwable {
        if (null != apiDefinition && apiDefinition.requiredLogin()) {
            Long userId = AuthContext.me().getLoginUser().getUserId();
            UserDTO user = AuthContext.me().getLoginUser().getDetail();
            Object[] args = joinPoint.getArgs();
            if (ArrayUtils.isNotEmpty(args)) {
                for (Object arg : args) {
                    if (arg instanceof BaseRequest) {
                        BaseRequest request = (BaseRequest) arg;
                        request.setUserId(userId);
                        if (null != user) {
                            request.setUserName(user.getName());
                            request.setUserPubWeb3Addr(user.getPubWeb3Addr());
                        }
                    }
                }
            }
        }

        return joinPoint.proceed();
    }
}
