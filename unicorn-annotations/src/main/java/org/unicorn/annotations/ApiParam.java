package org.unicorn.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数
 *
 * @author czk
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiParam {

    /**
     * 描述.
     */
    String value() default "";

    /**
     * 字典名字
     */
    String name() default "";

    /**
     * 必填项.
     */
    boolean required() default false;

}
