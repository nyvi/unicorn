package org.unicorn.util;

import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;

/**
 * @author czk
 */
public class StrUtils extends StringUtils {

    private final static int INDEX_NOT_FOUND = -1;

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
}
