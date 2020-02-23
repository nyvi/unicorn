package org.unicorn.util;

import com.google.common.collect.Maps;
import org.springframework.context.ApplicationContext;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author czk
 */
public class ReflectionUtils {

    private static final Map<String, String> SIMPLE_NAME_CACHE = new HashMap<>();

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

    /**
     * 判断是否泛型类
     *
     * @param type 泛型Type
     * @return 如果是泛型返回true
     */
    public static boolean isGenericType(@Nonnull Type type) {
        if (ReflectionUtils.isJavaType(type.getClass())) {
            return false;
        }
        return !type.getTypeName().contains(".");
    }


    /**
     * 获取类型简化名称
     *
     * @param clazz 类型class
     * @return 简化名称
     */
    public static String getSimpleTypeName(@Nonnull Class<?> clazz) {
        String typeName = clazz.getTypeName();
        String simpleName = StrUtils.substringAfterLast(typeName, ".");
        if (isJavaType(clazz)) {
            return simpleName;
        }
        String pathName = SIMPLE_NAME_CACHE.get(simpleName);
        if (pathName == null) {
            SIMPLE_NAME_CACHE.put(simpleName, typeName);
            return simpleName;
        }
        if (typeName.equals(pathName)) {
            return simpleName;
        }
        return typeName;
    }

    /**
     * 获取泛型简化类型名称
     *
     * @param type 泛型Type
     * @return 简化名称
     */
    public static String getSimpleTypeName(@Nonnull Type type) {
        String typeName = type.getTypeName();
        return typeName.replace("java.lang.", "")
                .replace("java.util.", "");
    }

    private ReflectionUtils() {
    }
}
