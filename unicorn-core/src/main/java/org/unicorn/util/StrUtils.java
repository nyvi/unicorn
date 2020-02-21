package org.unicorn.util;

import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;

/**
 * @author czk
 */
public class StrUtils extends StringUtils {

    private final static int INDEX_NOT_FOUND = -1;

    public static final String EMPTY = "";

    /**
     * 路径合理化
     *
     * @param path 请求路径
     * @return 合理化后的路径
     */
    public static String pathRationalize(@Nonnull String path) {
        return path.replace("//", "/").replace("//", "/");
    }

    /**
     * 截取到某个字符串之前
     *
     * @param str       待截取的字符串
     * @param separator 分割符
     * @return 截取后的字符串
     */
    public static String substringBefore(final String str, final String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.isEmpty()) {
            return str;
        }
        final int pos = str.indexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * 从最后面开始截取
     *
     * @param str       待截取字符串
     * @param separator 截取字符串
     * @return 截取后的字符串
     */
    public static String substringAfterLast(final String str, final String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(separator)) {
            return EMPTY;
        }
        final int pos = str.lastIndexOf(separator);
        if (pos == INDEX_NOT_FOUND || pos == str.length() - separator.length()) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }
}
