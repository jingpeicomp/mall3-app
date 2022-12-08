package com.bik.web3.mall3.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;

import static org.springframework.beans.BeanUtils.getPropertyDescriptor;
import static org.springframework.beans.BeanUtils.getPropertyDescriptors;

/**
 * 领域对象工具类
 *
 * @author Mingo.Liu
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class ObjectUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final TypeReference<Map<String, Object>> OBJECT_MAP_TYPE = new TypeReference<Map<String, Object>>() {
    };

    static {
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * 创建并复制源对象属性
     *
     * @param source    源对象
     * @param destClass 目的对象class
     * @param <T>       泛型
     * @return 目标对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T copy(Object source, Class<T> destClass) {
        T dest = (T) ReflectUtils.newInstance(destClass);
        return copy(source, dest, false);
    }

    /**
     * 复制对象属性
     *
     * @param source 源对象
     * @param dest   目标对象
     * @param <T>    泛型
     * @return 目标对象
     */
    public static <T> T copy(Object source, T dest) {
        return copy(source, dest, false);
    }

    /**
     * 复制对象属性
     *
     * @param source     源对象
     * @param dest       目标对象
     * @param ignoreNull 是否忽略null值
     * @param <T>        泛型
     * @return 目标对象
     */
    public static <T> T copy(Object source, T dest, boolean ignoreNull) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(dest, "Dest must not be null");

        Class<?> actualEditable = dest.getClass();
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (null != writeMethod) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (null != sourcePd) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (null != readMethod) {
                        ResolvableType sourceResolvableType = ResolvableType.forMethodReturnType(readMethod);
                        ResolvableType targetResolvableType = ResolvableType.forMethodParameter(writeMethod, 0);
                        if (targetResolvableType.isAssignableFrom(sourceResolvableType)) {
                            try {
                                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                    readMethod.setAccessible(true);
                                }
                                Object value = readMethod.invoke(source);
                                if (ignoreNull && null == value) {
                                    continue;
                                }
                                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                    writeMethod.setAccessible(true);
                                }
                                writeMethod.invoke(dest, value);
                            } catch (Throwable ex) {
                                throw new FatalBeanException(
                                        "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                            }
                        }
                    }
                }
            }
        }

        return dest;
    }

    /**
     * 从Map中复制对象属性
     *
     * @param map       map
     * @param destClass 对象属性
     * @param <T>       泛型
     * @return 目标对象
     */
    public static <T> T copyFromMap(Map<String, Object> map, Class<T> destClass) {
        return OBJECT_MAPPER.convertValue(map, destClass);
    }

    /**
     * 将Java对象转换为JSON字符串
     *
     * @param obj java对象
     * @return json字符串
     */
    public static String toJson(Object obj) {
        try {
            if (null == obj) {
                return "";
            }
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Convert object to json string error {}", obj, e);
        }

        return "";
    }

    /**
     * 从Json字符串中解析java对象
     *
     * @param json   JSON字符串
     * @param tClass java对象类型
     * @param <T>    泛型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> tClass) {
        try {
            if (StringUtils.isBlank(json)) {
                return null;
            }
            return OBJECT_MAPPER.readValue(json, tClass);
        } catch (IOException e) {
            log.error("Parse object from json error {} {}", json, tClass, e);
        }

        return null;
    }

    /**
     * 将java 对象转换为map
     *
     * @param t   java对象
     * @param <T> 泛型
     * @return map
     */
    public static <T> Map<String, Object> toMap(T t) {
        try {
            if (null == t) {
                return Collections.emptyMap();
            }
            return OBJECT_MAPPER.convertValue(t, OBJECT_MAP_TYPE);
        } catch (Exception e) {
            log.error("Convert object to map error {} ", t, e);
            return Collections.emptyMap();
        }
    }

    /**
     * 将map转换java对象
     *
     * @param map    map
     * @param tClass 对象类型
     * @param <T>    泛型
     * @return java对象
     */
    public static <T> T fromMap(Map<String, Object> map, Class<T> tClass) {
        try {
            return OBJECT_MAPPER.convertValue(map, tClass);
        } catch (Exception e) {
            log.error("Convert map to object error {} {} ", map, tClass, e);
        }
        return null;
    }
}
