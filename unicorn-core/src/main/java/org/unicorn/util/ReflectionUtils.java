package org.unicorn.util;

import com.google.common.collect.Maps;
import org.springframework.context.ApplicationContext;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**
     * 获取该类的所有属性列表
     *
     * @param clazz 反射类
     * @return 属性列表
     */
    public static List<Field> getFieldList(@Nonnull Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> fieldList = new ArrayList<>(fields.length);
        for (Field field : fields) {
            // 过滤静态属性
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            // 过滤 transient关键字修饰的属性
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            fieldList.add(field);
        }
        // 处理父类字段
        Class<?> superClass = clazz.getSuperclass();
        if (superClass == null || Object.class.equals(superClass)) {
            return fieldList;
        }
        // 排除重载属性
        return excludeOverrideSuperField(fieldList, getFieldList(superClass));
    }

    /**
     * 排序重置父类属性
     *
     * @param fieldList      子类属性
     * @param superFieldList 父类属性
     * @return 去重后属性
     */
    public static List<Field> excludeOverrideSuperField(@Nonnull List<Field> fieldList, @Nonnull List<Field> superFieldList) {
        // 子类属性
        Map<String, Field> fieldMap = Maps.newHashMapWithExpectedSize(fieldList.size() + superFieldList.size());
        for (Field field : fieldList) {
            fieldMap.put(field.getName(), field);
        }
        for (Field superField : superFieldList) {
            if (!fieldMap.containsKey(superField.getName())) {
                // 加入重置父类属性
                fieldList.add(superField);
            }
        }
        return fieldList;
    }

    /**
     * 是否java类型数据
     *
     * @param clazz 类型class
     * @return 如果是java的数据类型返回true
     */
    public static boolean isJavaType(@Nonnull Class<?> clazz) {
        String typeName = clazz.getTypeName();
        return StrUtils.startsWithIgnoreCase(typeName, "java.lang.") ||
                StrUtils.startsWithIgnoreCase(typeName, "java.util.");
    }

    public static String getSimpleTypeName(@Nonnull Class<?> clazz) {
        String typeName = clazz.getTypeName();
        if (isJavaType(clazz)) {
            return StrUtils.substringAfterLast(typeName, ".");
        }
        return typeName;
    }

    private ReflectionUtils() {
    }
}
