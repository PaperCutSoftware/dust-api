/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.user;

import com.papercut.dust.model.Validations;
import io.dropwizard.validation.Validated;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

import static java.util.stream.Collectors.toList;

/**
 * Resource to find user info, i.e watches for a user.
 */
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Path("/users")
@PermitAll
public class UserResource {

    @Inject
    private UserRepository userRepo;

    @POST
    public User createUser(@Validated(Validations.Create.class) final User device) {
        return userRepo.create(device);
    }

    @GET
    public Collection<User> findUsers(@QueryParam("keyword") final String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return userRepo.findAll();
        } else {
            return userRepo.findByKeyword(keyword);
        }
    }

    @GET
    @Path("/{id}")
    public User findUserById(@PathParam("id") final long id) {
        return userRepo.findById(id);
    }

    @PUT
    @Path("/{id}")
    public User updateUser(
            @PathParam("id") final long id,
            @Validated(Validations.Update.class) final User user
    ) {
        return userRepo.update(user);
    }

    @GET
    @Path("/{userId}/watches")
    public Collection<Watch> findWatches(@PathParam("userId") final long userId) {
        return userRepo.findWatchesOfUser(userId).stream().map(Watch::new).collect(toList());
    }

    @POST
    @Path("/{userId}/watches")
    public void watch(
            @PathParam("userId") final long userId,
            @Valid final Watch watch
    ) {
        userRepo.addWatch(watch.deviceId, userId);
    }

    @DELETE
    @Path("/{userId}/watches/{deviceId}")
    public void unwatch(
            @PathParam("userId") final long userId,
            @PathParam("deviceId") final long deviceId
    ) {
        userRepo.removeWatch(deviceId, userId);
    }
}
