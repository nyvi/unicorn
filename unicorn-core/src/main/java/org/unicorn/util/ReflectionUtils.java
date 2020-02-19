package org.unicorn.util;

import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author czk
 */
public class ReflectionUtils {

    /**
     * 获取指定注解的Bean的class
     *
     * @param context    spring上下文
     * @param annotation 注解
     * @return 注解的Class
     */
    @SafeVarargs
    public static List<Class<?>> getAnnotationClass(ApplicationContext context, Class<? extends Annotation>... annotation) {
        List<Class<?>> result = new ArrayList<>();
        for (Class<? extends Annotation> cls : annotation) {
            String[] beanNameArray = context.getBeanNamesForAnnotation(cls);
            for (String beanName : beanNameArray) {
                Object beanInstance = context.getBean(beanName);
                result.add(beanInstance.getClass());
            }
        }
        return result;
    }

    private ReflectionUtils() {
    }
}
