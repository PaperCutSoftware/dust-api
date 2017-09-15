/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.papercut.dust.model.Identifiable;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 * A user watching the changes of the status of a specific device.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Watch implements Serializable, Identifiable<Watch> {

    @JsonProperty
    @NotNull
    Long deviceId;

    public Watch(@JsonProperty("device_id") final Long deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.deviceId);
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
        final Watch other = (Watch) obj;
        if (!Objects.equals(this.deviceId, other.deviceId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Watch{" + "deviceId=" + deviceId + '}';
    }

    @Override
    public Long getId() {
        return this.deviceId;
    }

    @Override
    public Watch withId(long newId) {
        return new Watch(newId);
    }

}
