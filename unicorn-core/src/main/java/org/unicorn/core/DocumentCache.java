package org.unicorn.core;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 文件缓存
 *
 * @author czk
 */
public class DocumentCache {

    private final static Map<String, Document> CACHE = Maps.newLinkedHashMap();

    public static void addDocument(String key, Document value) {
        CACHE.put(key, value);
    }

    public static Map<String, Document> getAll() {
        return CACHE;
    }
}
