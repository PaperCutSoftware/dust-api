/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.claim;

public class NoSuchClaimException extends RuntimeException {

    public NoSuchClaimException(final long deviceId, final long userId) {
        super(String.format("There is no claim for device %s and user %s", deviceId, userId));
    }

}
