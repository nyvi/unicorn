package org.unicorn.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 模型属性
 *
 * @author czk
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiModelProperty {

    /**
     * 描述.
     */
    String value() default "";

    /**
     * 必填项.
     */
    boolean required() default false;

    /**
     * 是否隐藏.
     */
    boolean hidden() default false;
}
