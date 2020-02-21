package org.unicorn.util;

/**
 * @author czk
 */
public class ClassUtils {

    /**
     * 判断是否为代理对象
     *
     * @param clazz 反射对象
     * @return 是否为代理对象
     */
    public static boolean isProxy(Class<?> clazz) {
        if (clazz != null) {
            for (Class<?> cls : clazz.getInterfaces()) {
                String interfaceName = cls.getName();
                if ("net.sf.cglib.proxy.Factory".equals(interfaceName)
                        || "org.springframework.cglib.proxy.Factory".equals(interfaceName)
                        || "javassist.util.proxy.ProxyObject".equals(interfaceName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前对象的class
     *
     * @param clazz 反射对象
     * @return 当前对象的class
     */
    public static Class<?> getCurrentClass(Class<?> clazz) {
        return isProxy(clazz) ? clazz.getSuperclass() : clazz;
    }

}
