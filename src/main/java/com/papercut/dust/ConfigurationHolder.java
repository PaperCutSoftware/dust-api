/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust;

public final class ConfigurationHolder {

    private static DeviceUsageTrackerConfig config;

    private ConfigurationHolder() {
    }

    static void set(DeviceUsageTrackerConfig config) {
        ConfigurationHolder.config = config;
    }

    public static DeviceUsageTrackerConfig get() {
        return config;
    }
}
