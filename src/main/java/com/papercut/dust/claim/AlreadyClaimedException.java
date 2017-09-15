/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.claim;

public class AlreadyClaimedException extends RuntimeException {

    public AlreadyClaimedException(final Claim claim) {
        this(claim.deviceName, claim.username);
    }

    public AlreadyClaimedException(final String deviceName, final String username) {
        super(username != null
                ? String.format("Device %s is already claimed by user %s", deviceName, username)
                : String.format("Device %s is already claimed", deviceName));
    }

}
