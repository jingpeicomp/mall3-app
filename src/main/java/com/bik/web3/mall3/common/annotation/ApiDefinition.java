package com.bik.web3.mall3.common.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

/**
 * HTTP Rest API接口定义
 *
 * @author Mingo.Liu
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping
public @interface ApiDefinition {
    /**
     * 菜单ID列表
     *
     * @return 菜单ID列表
     */
    long[] menuId() default {};

    /**
     * 接口是否需要登陆。true表示需要登陆，false表示不需要登陆
     *
     * @return 是否需要登陆
     */
    boolean requiredLogin() default true;

    /**
     * 接口是否需要权限校验。true表示需要校验，false表示不需要校验
     *
     * @return 是否需要权限校验
     */
    boolean requiredPermission() default false;

    /**
     * 请求的http方法(同RequestMapping)
     */
    @AliasFor(annotation = RequestMapping.class)
    RequestMethod[] method() default {};

    /**
     * Alias for RequestMapping.name.
     */
    @AliasFor(annotation = RequestMapping.class)
    String name() default "";

    /**
     * Alias for {@link RequestMapping#value}.
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] value() default {};

    /**
     * Alias for {@link RequestMapping#path}.
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] path() default {};

    /**
     * Alias for {@link RequestMapping#params}.
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] params() default {};

    /**
     * Alias for {@link RequestMapping#headers}.
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] headers() default {};

    /**
     * Alias for {@link RequestMapping#consumes}.
     *
     * @since 4.3.5
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] consumes() default {};

    /**
     * Alias for {@link RequestMapping#produces}.
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] produces() default {};
}
