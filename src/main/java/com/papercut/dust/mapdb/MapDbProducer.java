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
import com.papercut.dust.model.TransactionalMapProducer;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MapDbProducer implements TransactionalMapProducer {

    private DB db;

    @PostConstruct
    public void init() {
        db = DBMaker
                .fileDB("data/persistence/device-mgmt.db")
                .fileMmapEnable()
                .make();
    }

    @PreDestroy
    public void destroy() {
        db.close();
    }

    @Override
    public <T> TransactionalMap<T> createStore(final String name) {
        return new MapDbTransactionalMap<>(db, name);
    }

}
