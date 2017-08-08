/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.user;

import io.dropwizard.auth.Auth;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Path("/profile")
public class ProfileResource {

    @Inject
    private UserRepository userRepo;

    @GET
    public User profile(@Auth final User authUser) {
        return authUser;
    }

    @GET
    @Path("/watches")
    public Collection<Watch> profileWatches(@Auth final User authUser) {
        return userRepo.findWatchesOfUser(authUser.getId()).stream().map(Watch::new).collect(toList());
    }

    @POST
    @Path("/watches")
    public void addProfileWatch(@Auth final User authUser, @Valid final Watch watch) {
        userRepo.addWatch(watch.deviceId, authUser.getId());
    }

    @DELETE
    @Path("/watches/{deviceId}")
    public void removeProfileWatch(@Auth final User authUser, @PathParam("deviceId") final long deviceId) {
        userRepo.removeWatch(deviceId, authUser.getId());
    }
}
