/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.papercut.dust.device.NoSuchUniqueDeviceException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Repository base class.
 *
 * @param <T> type of the entity
 */
public abstract class Repository<T extends Identifiable<T>> {

    protected final Map<Long, T> store;
    private final Class<T> entityClass;

    protected Repository(final Class<T> entityClass, final Map<Long, T> store) {
        this.entityClass = entityClass;
        this.store = store;
    }

    protected void load(final String dataDir, final boolean update) {
        try {
            final File dir = new File("data/fixtures/" + dataDir);
            final File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));

            if (files == null) {
                throw new IOException("No files found in " + dir.getAbsolutePath());
            }

            final ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            for (File jsonFile : files) {
                final T entity = mapper.readValue(jsonFile, entityClass);
                if (update && entity.getId() != null) {
                    update(entity);
                } else {
                    create(entity);
                }
            }
        } catch (IOException ioex) {
            throw new RuntimeException(ioex);
        }

    }

    protected T create(final T entity) {
        final Long newId = maxId() + 1;
        final T newEntity = entity.withId(newId);
        final T existing = store.putIfAbsent(newId, newEntity);
        if (existing != null) {
            throw new IllegalArgumentException("Entity already exists");
        }
        return newEntity;
    }

    protected T update(final T entity) {
        if (!store.containsKey(entity.getId())) {
            throw new IllegalArgumentException("Cannot update entity, it does not exist");
        }
        return store.put(entity.getId(), entity);
    }

    protected T delete(final long id) {
        return store.remove(id);
    }

    protected Collection<T> findAll() {
        return store.values();
    }

    protected T findById(final long id) {
        return Optional.ofNullable(store.get(id))
                .orElseThrow(() -> new NoSuchUniqueDeviceException("No unique matching device for '" + id + "'"));
    }

    protected Collection<T> findByKeyword(final String keyword) {
        return store.values().stream().filter(e -> hasKeyword(e, keyword)).collect(toList());
    }

    protected abstract boolean hasKeyword(final T entity, final String keyword);

    protected static boolean containsKeyword(final String text, final String keyword) {
        return text != null && keyword != null && text.toLowerCase().contains(keyword.toLowerCase());
    }

    private long maxId() {
        return store.keySet().stream().mapToLong(x -> x).max().orElse(0L);
    }
}
