/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust;

import com.papercut.dust.auth.AuthConfig;
import com.papercut.dust.slack.SlackConfig;
import io.dropwizard.Configuration;

/**
 * Configuration wrapper class.
 */
public class DeviceUsageTrackerConfig extends Configuration {

    private AuthConfig authConfig;
    private SlackConfig slackConfig;

    public SlackConfig getSlackConfig() {
        return slackConfig;
    }

    public void setSlackConfig(final SlackConfig slackConfig) {
        this.slackConfig = slackConfig;
    }

    public AuthConfig getAuthConfig() {
        return authConfig;
    }

    public void setAuthConfig(final AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

}
