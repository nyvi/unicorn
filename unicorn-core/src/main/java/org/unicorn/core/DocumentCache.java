package org.unicorn.core;

import org.unicorn.model.Document;
import org.unicorn.model.ModelInfo;
import org.unicorn.util.HashMultiValueMap;
import org.unicorn.util.MultiValueMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件缓存
 *
 * @author czk
 */
public class DocumentCache {

    /**
     * 文档缓存
     */
    private final static Map<String, Document> DOCUMENT_CACHE = new LinkedHashMap<>();

    /**
     * 模型缓存
     */
    private final static MultiValueMap<String, ModelInfo> MODEL_CACHE = new HashMultiValueMap<>();

    public static void addDocument(String key, Document value) {
        DOCUMENT_CACHE.put(key, value);
    }

    public static Map<String, Document> getAllDocs() {
        return DOCUMENT_CACHE;
    }

    public static boolean notExistModel(String key) {
        return !MODEL_CACHE.containsKey(key);
    }

    public static void addModel(String key, List<ModelInfo> value) {
        MODEL_CACHE.addAll(key, value);
    }

    public static Map<String, List<ModelInfo>> getAllModel() {
        return MODEL_CACHE;
    }

    public static void clear() {
        DOCUMENT_CACHE.clear();
        MODEL_CACHE.clear();
    }
}
