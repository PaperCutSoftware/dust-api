/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.CacheBuilderSpec;

import javax.annotation.Nullable;

public class AuthConfig {

    @JsonProperty("clientId")
    private String clientId;

    @JsonProperty("hostedDomain")
    private String hostedDomain;

    private String authenticationCachePolicy;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    @Nullable
    public String getHostedDomain() {
        return hostedDomain;
    }

    public void setHostedDomain(final String hostedDomain) {
        this.hostedDomain = hostedDomain;
    }

    @JsonProperty("authenticationCachePolicy")
    public CacheBuilderSpec getAuthenticationCachePolicy() {
        return CacheBuilderSpec.parse(authenticationCachePolicy);
    }

    public void setAuthenticationCachePolicy(final String authenticationCachePolicy) {
        this.authenticationCachePolicy = authenticationCachePolicy;
    }
}
