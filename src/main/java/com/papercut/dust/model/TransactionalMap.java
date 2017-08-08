/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.model;

import java.util.Map;

public interface TransactionalMap<T> extends Map<Long, T> {

    void commit();

    void rollback();
}
