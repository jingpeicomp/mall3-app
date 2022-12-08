package com.bik.web3.mall3.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

/**
 * Spring Bean 工厂类
 *
 * @author Mingo.Liu
 */
@Component
@Slf4j
public class BeanFactory implements ApplicationContextAware {
    private static ApplicationContext applicationContext;


    /**
     * 根据Bean名称获取实例
     *
     * @return bean实例
     * @throws BeansException BeansException
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 根据类型获取实例
     *
     * @param type 类型
     * @return bean实例
     * @throws BeansException BeansException
     */
    public static <T> T getBean(Class<T> type) throws BeansException {
        String beanName = StringUtils.uncapitalize(type.getSimpleName());
        return applicationContext.getBean(beanName, type);
    }

    /**
     * 根据类型获取实例，没有不抛出异常
     *
     * @param type 类型
     * @return bean实例
     */
    public static <T> T getBeanNullable(Class<T> type) {
        try {
            String beanName = StringUtils.uncapitalize(type.getSimpleName());
            return applicationContext.getBean(beanName, type);
        } catch (Exception ignore) {
            return null;
        }
    }

    /**
     * 根据类型获取Bean,可能存在多个实例,默认取第一个
     *
     * @param type 类型
     * @param <T>  泛型
     * @return bean实例
     * @throws BeansException BeansException
     */
    public static <T> T getBeanByType(Class<T> type) throws BeansException {
        Map<String, T> beanMap = applicationContext.getBeansOfType(type);
        if (beanMap.values().iterator().hasNext()) {
            return beanMap.values().iterator().next();
        }

        return null;
    }

    public static <T> Collection<T> getBeansByType(Class<T> type) throws BeansException {
        Map<String, T> beanMap = applicationContext.getBeansOfType(type);
        return beanMap.values();
    }

    /**
     * 根据注解获取Bean
     *
     * @param annotationType 注解类型
     * @return Bean map
     * @throws BeansException BeansException
     */
    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
        return applicationContext.getBeansWithAnnotation(annotationType);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        BeanFactory.applicationContext = applicationContext;
    }
}
