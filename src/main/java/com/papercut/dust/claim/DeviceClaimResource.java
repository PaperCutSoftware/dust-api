/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.claim;

import com.papercut.dust.device.Device;
import com.papercut.dust.device.DeviceRepository;
import com.papercut.dust.user.User;
import io.dropwizard.auth.Auth;

import java.util.Collection;
import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Resource to view/change a specific device.
 */
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Path("/devices")
@PermitAll
public class DeviceClaimResource {

    @Inject
    private ClaimRepository claimRepo;
    @Inject
    private DeviceRepository deviceRepo;

    @GET
    @Path("{deviceId:\\d+}/claims")
    public Collection<Claim> findActiveClaimsOnDevice(
            @PathParam("deviceId") final long deviceId
    ) {
        return claimRepo.find(deviceId, null, true);
    }

    @GET
    @Path("{nickname}/claims")
    public Collection<Claim> findActiveClaimsOnDevice(
            @PathParam("nickname") final String nickname
    ) {
        final Device device = deviceRepo.findByName(nickname);
        return claimRepo.find(device.getId(), null, true);
    }

    @POST
    @Path("{deviceId:\\d+}/claims")
    public Claim claimDevice(
            @PathParam("deviceId") final long deviceId,
            @Auth User user
    ) {
        final Device device = deviceRepo.findById(deviceId);
        final Claim newClaim = Claim.startingNow(device, user);
        return claimRepo.create(newClaim);
    }

    @POST
    @Path("{nickname}/claims")
    public Claim claimDevice(
            @PathParam("nickname") final String nickname,
            @Auth User user
    ) {
        final Device device = deviceRepo.findByName(nickname);
        final Claim newClaim = Claim.startingNow(device, user);
        return claimRepo.create(newClaim);
    }

    @DELETE
    @Path("{deviceId:\\d+}/claims")
    public Claim unclaimDevice(
            @PathParam("deviceId") final Long deviceId,
            @Auth User user
    ) {
        return claimRepo.delete(claimRepo.findActive(deviceId, user.getId()).id);
    }

    @DELETE
    @Path("{nickname}/claims")
    public Claim unclaimDevice(
            @PathParam("nickname") final String nickname,
            @Auth User user
    ) {
        final Device device = deviceRepo.findByName(nickname);
        return unclaimDevice(device.getId(), user);
    }

}
