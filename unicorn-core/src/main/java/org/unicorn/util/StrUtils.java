package org.unicorn.util;

import org.springframework.util.StringUtils;

/**
 * @author czk
 */
public class StrUtils extends StringUtils {

    /**
     * 路径合理化
     *
     * @param path 请求路径
     * @return 合理化后的路径
     */
    public static String pathRationalize(String path) {
        if (path == null) {
            return null;
        }
        return path.replace("//", "/").replace("//", "/");
    }
}
