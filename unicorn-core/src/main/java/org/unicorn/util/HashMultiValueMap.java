package org.unicorn.util;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * HashMap 实现
 *
 * @author czk
 */
public class HashMultiValueMap<K, V> implements MultiValueMap<K, V>, Serializable, Cloneable {

    private static final long serialVersionUID = -2315487738404028940L;

    private final Map<K, List<V>> targetMap;

    public HashMultiValueMap() {
        this.targetMap = new HashMap<>();
    }

    public HashMultiValueMap(int initialCapacity) {
        this.targetMap = new HashMap<>(Math.max((int) (initialCapacity / 0.75F) + 1, 16));
    }

    public HashMultiValueMap(@NonNull Map<K, List<V>> otherMap) {
        this.targetMap = new HashMap<>(otherMap);
    }

    @Override
    @Nullable
    public V getFirst(@NonNull K key) {
        List<V> values = this.targetMap.get(key);
        return (values != null && !values.isEmpty() ? values.get(0) : null);
    }

    @Override
    public void add(@NonNull K key, V value) {
        List<V> values = this.targetMap.computeIfAbsent(key, k -> new ArrayList<>());
        values.add(value);
    }

    @Override
    public void addAll(@NonNull K key, @NonNull List<? extends V> values) {
        List<V> currentValues = this.targetMap.computeIfAbsent(key, k -> new LinkedList<>());
        currentValues.addAll(values);
    }

    @Override
    public void addAll(MultiValueMap<K, V> values) {
        for (Entry<K, List<V>> entry : values.entrySet()) {
            addAll(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void set(@NonNull K key, V value) {
        List<V> values = new ArrayList<>();
        values.add(value);
        this.targetMap.put(key, values);
    }

    @Override
    public void setAll(Map<K, V> values) {
        for (Entry<K, V> entry : values.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    @Override
    @NonNull
    public Map<K, V> toSingleValueMap() {
        Map<K, V> singleValueMap = new HashMap<>(this.targetMap.size());
        this.targetMap.forEach((key, values) -> {
            if (values != null && !values.isEmpty()) {
                singleValueMap.put(key, values.get(0));
            }
        });
        return singleValueMap;
    }

    // Map 接口实现

    @Override
    public int size() {
        return this.targetMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.targetMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.targetMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.targetMap.containsValue(value);
    }

    @Override
    public List<V> get(Object key) {
        List<V> values = this.targetMap.get(key);
        return values == null ? new ArrayList<>() : values;
    }

    @Override
    public List<V> put(K key, List<V> value) {
        return this.targetMap.put(key, value);
    }

    @Override
    public List<V> remove(Object key) {
        return this.targetMap.remove(key);
    }

    @Override
    public void putAll(@NonNull Map<? extends K, ? extends List<V>> map) {
        this.targetMap.putAll(map);
    }

    @Override
    public void clear() {
        this.targetMap.clear();
    }

    @Override
    @NonNull
    public Set<K> keySet() {
        return this.targetMap.keySet();
    }

    @Override
    @NonNull
    public Collection<List<V>> values() {
        return this.targetMap.values();
    }

    @Override
    @NonNull
    public Set<Entry<K, List<V>>> entrySet() {
        return this.targetMap.entrySet();
    }

    /**
     * 深度复制
     *
     * @return a copy of this Map
     */
    public HashMultiValueMap<K, V> deepCopy() {
        HashMultiValueMap<K, V> copy = new HashMultiValueMap<>(this.targetMap.size());
        for (Map.Entry<K, List<V>> entry : this.targetMap.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

    @Override
    public boolean equals(@NonNull Object obj) {
        if (obj instanceof Map) {
            return this.targetMap.equals(obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.targetMap.hashCode();
    }

    @Override
    public String toString() {
        return this.targetMap.toString();
    }
}
