/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.papercut.dust.user.User;
import com.papercut.dust.user.UserRepository;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import javax.annotation.Nullable;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

public class GoogleAuthenticator implements Authenticator<String, User> {

    private static final String ISSUER = "accounts.google.com";

    private final UserRepository userRepo;
    private final GoogleIdTokenVerifier tokenVerifier;

    @Nullable
    private final String hostedDomain;

    public GoogleAuthenticator(
            final String clientId,
            final UserRepository userRepo,
            @Nullable final String hostedDomain
    ) {
        this.userRepo = userRepo;
        tokenVerifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singleton(clientId))
                .setIssuer(ISSUER)
                .build();
        this.hostedDomain = hostedDomain;        
    }

    @Override
    public Optional<User> authenticate(String idToken) throws AuthenticationException {
        try {
            return Optional.ofNullable(tokenVerifier.verify(idToken))
                    .map(GoogleIdToken::getPayload)
                    .filter(payload -> hostedDomain == null || payload.getHostedDomain().equals(hostedDomain))
                    .map(this::payloadToUser);
        } catch (GeneralSecurityException | IOException e) {
            throw new AuthenticationException(e);
        }
    }

    private User payloadToUser(final GoogleIdToken.Payload payload) {
        final User user = new User(null, payload.getSubject(), (String) payload.get("name"), null);
        return userRepo.createUser(user);
    }
}
