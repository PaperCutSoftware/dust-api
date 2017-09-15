/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.device;

public class NoSuchUniqueDeviceException extends RuntimeException {

    public NoSuchUniqueDeviceException(final String message) {
        super(message);
    }

    public static NoSuchUniqueDeviceException none(final String keyword) {
        return new NoSuchUniqueDeviceException("No unique matching device for '" + keyword + "'");
    }

    public static NoSuchUniqueDeviceException tooMany(
            final String keyword,
            final String foundDeviceNames
    ) {
        return new NoSuchUniqueDeviceException("We found too many matching devices for '"
                + keyword + "': " + foundDeviceNames);
    }
}
