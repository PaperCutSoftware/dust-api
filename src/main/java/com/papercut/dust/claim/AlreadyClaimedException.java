/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.claim;

import javax.annotation.Nullable;

public class AlreadyClaimedException extends RuntimeException {

    private final String deviceName;
    @Nullable
    private String username;

    public AlreadyClaimedException(final Claim claim) {
        this(claim.deviceName);
        username = claim.username;
    }

    public AlreadyClaimedException(final String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public String getMessage() {
        return username != null
                ? String.format("Device %s is already claimed by user %s", deviceName, username)
                : String.format("Device %s is already claimed", deviceName);
    }

}
