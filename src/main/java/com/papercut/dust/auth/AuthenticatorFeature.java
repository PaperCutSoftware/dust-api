/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.auth;

import com.codahale.metrics.MetricRegistry;
import com.papercut.dust.user.User;
import com.papercut.dust.user.UserRepository;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;

import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class AuthenticatorFeature implements Feature {

    private final MetricRegistry metricRegistry;
    private final AuthConfig authConfig;

    public AuthenticatorFeature(
            final MetricRegistry metricRegistry,
            final AuthConfig authConfig
    ) {
        this.metricRegistry = metricRegistry;
        this.authConfig = authConfig;
    }

    @Override
    public boolean configure(final FeatureContext featureContext) {
        final UserRepository userRepo = CDI.current().select(UserRepository.class).get();
        final Authenticator<String, User> authenticator = new GoogleAuthenticator(
                authConfig.getClientId(), userRepo, authConfig.getHostedDomain()
        );

        final Authenticator<String, User> cachingAuthenticator = new CachingAuthenticator<>(
                metricRegistry, authenticator, authConfig.getAuthenticationCachePolicy()
        );

        featureContext.register(new AuthDynamicFeature(
                new OAuthCredentialAuthFilter.Builder<User>()
                .setAuthenticator(cachingAuthenticator)
                .setPrefix("Bearer")
                .buildAuthFilter()));
        featureContext.register(new AuthValueFactoryProvider.Binder<>(User.class));

        return true;
    }
}
