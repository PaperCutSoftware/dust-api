/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.claim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.papercut.dust.device.Device;
import com.papercut.dust.model.Identifiable;
import com.papercut.dust.model.Validations;
import com.papercut.dust.user.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Claim implements Identifiable<Claim>, Serializable {

    @JsonProperty
    @Null(groups = Validations.Create.class)
    @NotNull(groups = Validations.Update.class)
    final Long id;

    @JsonProperty
    @NotNull
    final public long userId;

    @JsonProperty
    @NotNull
    final public String username;

    @JsonProperty
    @NotNull
    final public long deviceId;

    @JsonProperty
    @NotNull
    final public String deviceName;

    @JsonProperty
    @NotNull
    final LocalDateTime startDate;

    @JsonProperty
    final LocalDateTime endDate;

    @JsonProperty
    final public String slackTimestamp;

    public Claim(
            @JsonProperty("id") Long id,
            @JsonProperty("user_id") long userId,
            @JsonProperty("username") String username,
            @JsonProperty("device_id") long deviceId,
            @JsonProperty("device_name") String deviceName,
            @JsonProperty("start_date") LocalDateTime startDate,
            @JsonProperty("end_date") LocalDateTime endDate,
            @JsonProperty("slack_timestamp") String slackTimestamp
    ) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.slackTimestamp = slackTimestamp;
    }

    @Override
    public Long getId() {
        return id;
    }

    Claim startingNow() {
        return new Claim(id, userId, username, deviceId, deviceName, LocalDateTime.now(), endDate, slackTimestamp);
    }

    @Override
    public Claim withId(final long newId) {
        return new Claim(newId, userId, username, deviceId, deviceName, startDate, endDate, slackTimestamp);
    }

    public static Claim startingNow(final Device device, final User user) {
        return new Claim(null, user.getId(), user.getDisplayName(), device.getId(), device.getNickname(), null, null, null).startingNow();
    }

    public Claim withSlackTimestamp(final String timestamp) {
        return new Claim(id, userId, username, deviceId, deviceName, startDate, endDate, timestamp);
    }

    @Override
    public String toString() {
        return "Claim{" + "id=" + id + ", userId=" + userId + ", username=" + username + ", deviceId=" + deviceId + ", deviceName=" + deviceName + ", startDate=" + startDate + ", endDate=" + endDate + ", slackTimestamp=" + slackTimestamp + '}';
    }

}
