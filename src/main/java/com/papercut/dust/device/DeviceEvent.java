/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.device;

import com.papercut.dust.claim.Claim;

import java.io.Serializable;

public class DeviceEvent implements Serializable {

    public enum DeviceEventType {
        CLAIMED, UNCLAIMED
    };

    public final Claim claim;
    public final DeviceEventType type;

    public DeviceEvent(final Claim claim, final DeviceEventType type) {
        this.claim = claim;
        this.type = type;
    }

}
