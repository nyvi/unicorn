package org.unicorn.util;

import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
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
    public static List<Class<?>> getBeanClassForAnnotation(ApplicationContext context, Class<? extends Annotation> annotation) {
        String[] beanNameArray = context.getBeanNamesForAnnotation(annotation);
        List<Class<?>> result = new ArrayList<>(beanNameArray.length);
        for (String beanName : beanNameArray) {
            Object beanInstance = context.getBean(beanName);
            result.add(beanInstance.getClass());
        }
        return result;
    }

    /**
     * 获取该类的所有属性列表
     *
     * @param clazz 反射类
     * @return 属性列表
     */
    public static List<Field> getFieldList(@NonNull Class<?> clazz) {
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
    public static List<Field> excludeOverrideSuperField(@NonNull List<Field> fieldList, @NonNull List<Field> superFieldList) {
        // 子类属性
        int initialCapacity = fieldList.size() + superFieldList.size();
        Map<String, Field> fieldMap = new HashMap<>(Math.max((int) (initialCapacity / 0.75F) + 1, 16));
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
     * @param typeName 类名称
     * @return 如果是java的数据类型返回true
     */
    public static boolean isJavaType(@NonNull String typeName) {
        return StrUtils.startsWithIgnoreCase(typeName, "java.lang.") ||
                StrUtils.startsWithIgnoreCase(typeName, "java.util.");
    }

    /**
     * 判断是否泛型类
     *
     * @param type 泛型Type
     * @return 如果是泛型返回true
     */
    public static boolean isGenericType(@NonNull Type type) {
        if (ReflectionUtils.isJavaType(type.getClass().getTypeName())) {
            return false;
        }
        return !type.getTypeName().contains(".");
    }

    /**
     * 获取类型简化名称
     *
     * @param typeName 类型 typeName
     * @return 简化名称
     */
    public static String getSimpleTypeName(@NonNull String typeName) {
        String simpleName = SIMPLE_NAME_CACHE.get(typeName);
        if (simpleName != null) {
            return simpleName;
        }
        if (isJavaType(typeName)) {
            simpleName = StrUtils.substringAfterLast(typeName, ".");
            SIMPLE_NAME_CACHE.put(typeName, simpleName);
            return simpleName;
        }
        simpleName = StrUtils.substringAfterLast(typeName, ".");
        if (SIMPLE_NAME_CACHE.containsValue(simpleName)) {
            simpleName = typeName;
        }
        SIMPLE_NAME_CACHE.put(typeName, simpleName);
        return simpleName;
    }

    /**
     * 获取泛型简化类型名称
     *
     * @param type 泛型Type
     * @return 简化名称
     */
    public static String getSimpleTypeName(@NonNull Type type) {
        String typeName = type.getTypeName();
        String simpleName = SIMPLE_NAME_CACHE.get(typeName);
        if (simpleName != null) {
            return simpleName;
        }
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            for (Type actualTypeArgument : actualTypeArguments) {
                String simpleTypeName;
                if (actualTypeArgument instanceof ParameterizedType) {
                    simpleTypeName = getSimpleTypeName(actualTypeArgument);
                } else {
                    simpleTypeName = ReflectionUtils.getSimpleTypeName(actualTypeArgument.getTypeName());
                }
                typeName = typeName.replace(actualTypeArgument.getTypeName(), simpleTypeName);
            }
            Type rawType = ((ParameterizedType) type).getRawType();
            String simpleTypeName = ReflectionUtils.getSimpleTypeName(rawType.getTypeName());
            typeName = typeName.replace(rawType.getTypeName(), simpleTypeName);
            SIMPLE_NAME_CACHE.put(type.getTypeName(), typeName);
            return typeName;
        } else {
            return ReflectionUtils.getSimpleTypeName(type.getTypeName());
        }
    }

    /**
     * 清理后期没用的数据
     */
    public static void clear() {
        SIMPLE_NAME_CACHE.clear();
    }

    private ReflectionUtils() {
    }

}
