/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.papercut.dust.model.Identifiable;
import com.papercut.dust.model.Validations;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.security.Principal;
import java.util.Objects;
import java.util.Optional;

/**
 * A user of the system.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Principal, Identifiable<User>, Serializable {

    @JsonProperty
    @Null(groups = Validations.Create.class)
    @NotNull(groups = Validations.Update.class)
    final Long id;

    @JsonProperty
    @NotNull
    final String googleUserId;

    @JsonProperty
    @Nullable
    final String slackUsername;

    @JsonProperty
    @NotNull
    final String displayName;

    public User(
            @JsonProperty("id") @Nullable final Long id,
            @JsonProperty("google_user_id") final String googleUserId,
            @JsonProperty("display_name") final String displayName,
            @JsonProperty("slack_username") @Nullable final String slackUsername
    ) {
        this.id = id;
        this.googleUserId = googleUserId;
        this.slackUsername = slackUsername;
        this.displayName = displayName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getGoogleUserId() {
        return googleUserId;
    }

    public Optional<String> getSlackUsername() {
        return Optional.ofNullable(slackUsername);
    }

    @JsonIgnore
    @Override
    public String getName() {
        return getGoogleUserId();
    }

    @Override
    public User withId(final long newId) {
        return new User(newId, googleUserId, displayName, slackUsername);
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", googleUserId=" + googleUserId + ", slackUsername=" + slackUsername + ", displayName=" + displayName + '}';
    }

}
