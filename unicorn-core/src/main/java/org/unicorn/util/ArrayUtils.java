package org.unicorn.util;

/**
 * @author czk
 */
public class ArrayUtils {

    /**
     * 判断数组是否为空
     *
     * @param array 数组
     * @param <T>   数组类型
     * @return 数组为空时返回 true
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否为空
     *
     * @param array 数组
     * @param <T>   数组类型
     * @return 数组非空时返回 true
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    /**
     * 当数组为空时,使用默认的
     *
     * @param array        数组
     * @param defaultArray 默认数组
     * @param <T>          数组类型
     * @return 当数组为空时, 返回默认的
     */
    public static <T> T[] defaultIfEmpty(T[] array, T[] defaultArray) {
        return isEmpty(array) ? defaultArray : array;
    }
}
