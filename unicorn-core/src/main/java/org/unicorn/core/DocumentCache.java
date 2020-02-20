package org.unicorn.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 文件缓存
 *
 * @author czk
 */
public class DocumentCache {

    /**
     * 文档
     */
    private final static Map<String, Document> DOCUMENT_CACHE = Maps.newLinkedHashMap();

    /**
     * 出入参数类型
     */
    private final static Multimap<String, ModelInfo> MODEL_CACHE = ArrayListMultimap.create();

    public static void addDocument(String key, Document value) {
        DOCUMENT_CACHE.put(key, value);
    }

    public static Map<String, Document> getAll() {
        return DOCUMENT_CACHE;
    }

    public static void addModel(String key, ModelInfo value) {
        MODEL_CACHE.put(key, value);
    }

    public static void addModel(String key, List<ModelInfo> value) {
        MODEL_CACHE.putAll(key, value);
    }

    public static boolean containsModel(String key) {
        return MODEL_CACHE.containsKey(key);
    }

    public static Map<String, Collection<ModelInfo>> getAllModel() {
        return MODEL_CACHE.asMap();
    }
}
