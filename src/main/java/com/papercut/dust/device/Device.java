/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.device;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.papercut.dust.model.Validations;
import com.papercut.dust.model.Identifiable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Device implements Identifiable<Device>, Serializable {

    @JsonProperty
    @Null(groups = Validations.Create.class)
    @NotNull(groups = Validations.Update.class)
    final Long id;

    @NotNull
    @Size(min = 2, max = 50)
    @JsonProperty
    final String brand;

    @NotNull
    @Size(min = 2, max = 50)
    @JsonProperty
    final String model;

    @Size(min = 2, max = 50)
    @JsonProperty
    final String hostname;

    @NotNull
    @Size(min = 2, max = 20)
    @JsonProperty
    final String nickname;

    @NotNull
    @Size(min = 2, max = 15)
    @JsonProperty
    final String ip;

    @JsonProperty
    final String adminUrl;

    @JsonProperty
    final String adminUsername;

    @JsonProperty
    final String adminPassword;

    @JsonProperty
    final String wikiUrl;

    @JsonProperty
    final String manualUrl;

    @JsonProperty
    final String photoUrl;

    @JsonProperty
    final String comment;

    @JsonProperty
    final String location;

    @JsonProperty
    @Null(groups = {Validations.Create.class, Validations.Update.class})
    final Long claimedById;

    @JsonProperty
    @Null(groups = {Validations.Create.class, Validations.Update.class})
    final String claimedBy;

    public Device(
            @JsonProperty("id") Long id,
            @JsonProperty("brand") String brand,
            @JsonProperty("model") String model,
            @JsonProperty("hostname") String hostname,
            @JsonProperty("nickname") String nickname,
            @JsonProperty("ip") String ip,
            @JsonProperty("admin_url") String adminUrl,
            @JsonProperty("admin_username") String adminUsername,
            @JsonProperty("admin_password") String adminPassword,
            @JsonProperty("wiki_url") String wikiUrl,
            @JsonProperty("manual_url") String manualUrl,
            @JsonProperty("photo_url") String photoUrl,
            @JsonProperty("comment") String comment,
            @JsonProperty("location") String location,
            @JsonProperty("claimed_by_id") Long claimedById,
            @JsonProperty("claimed_by") String claimedBy
    ) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.hostname = hostname;
        this.nickname = nickname;
        this.ip = ip;
        this.adminUrl = adminUrl;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.wikiUrl = wikiUrl;
        this.manualUrl = manualUrl;
        this.photoUrl = photoUrl;
        this.comment = comment;
        this.location = location;
        this.claimedById = claimedById;
        this.claimedBy = claimedBy;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + Objects.hashCode(this.id);
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
        final Device other = (Device) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getClaimedBy() {
        return claimedBy;
    }

    @Override
    public Device withId(final long newId) {
        return new Device(newId, brand, model, hostname, nickname, ip, adminUrl, adminUsername, adminPassword, wikiUrl, manualUrl, photoUrl, comment, location, claimedById, claimedBy);
    }

    Device claimedBy(final Long claimedById, final String claimedBy) {
        return new Device(id, brand, model, hostname, nickname, ip, adminUrl, adminUsername, adminPassword, wikiUrl, manualUrl, photoUrl, comment, location, claimedById, claimedBy);
    }

    @Override
    public String toString() {
        return "Device{" + "id=" + id + ", brand=" + brand + ", model=" + model + ", hostname=" + hostname + ", nickname=" + nickname + ", ip=" + ip + ", location=" + location + ", claimedById=" + claimedById + ", claimedBy=" + claimedBy + '}';
    }

}
