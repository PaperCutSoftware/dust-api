/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.mapdb;

import com.papercut.dust.model.TransactionalMap;
import org.mapdb.DB;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MapDbTransactionalMap<T> implements TransactionalMap<T> {

    private final Map<Long, T> map;
    private final DB db;

    public MapDbTransactionalMap(final DB db, final String name) {
        this.db = db;
        this.map = (Map<Long, T>) db.hashMap(name).createOrOpen();
    }

    @Override
    public void commit() {
        db.commit();
    }

    @Override
    public void rollback() {
        db.rollback();
    }

    // delegated methods
    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public T get(Object key) {
        return map.get(key);
    }

    @Override
    public T put(Long key, T value) {
        return map.put(key, value);
    }

    @Override
    public T remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends Long, ? extends T> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<Long> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<T> values() {
        return map.values();
    }

    @Override
    public Set<Entry<Long, T>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return map.equals(o);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public T getOrDefault(Object key, T defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super Long, ? super T> action) {
        map.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super Long, ? super T, ? extends T> function) {
        map.replaceAll(function);
    }

    @Override
    public T putIfAbsent(Long key, T value) {
        return map.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return map.remove(key, value);
    }

    @Override
    public boolean replace(Long key, T oldValue, T newValue) {
        return map.replace(key, oldValue, newValue);
    }

    @Override
    public T replace(Long key, T value) {
        return map.replace(key, value);
    }

    @Override
    public T computeIfAbsent(Long key, Function<? super Long, ? extends T> mappingFunction) {
        return map.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public T computeIfPresent(Long key, BiFunction<? super Long, ? super T, ? extends T> remappingFunction) {
        return map.computeIfPresent(key, remappingFunction);
    }

    @Override
    public T compute(Long key, BiFunction<? super Long, ? super T, ? extends T> remappingFunction) {
        return map.compute(key, remappingFunction);
    }

    @Override
    public T merge(Long key, T value, BiFunction<? super T, ? super T, ? extends T> remappingFunction) {
        return map.merge(key, value, remappingFunction);
    }

}
