/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.claim;

import com.papercut.dust.model.Validations;
import io.dropwizard.validation.Validated;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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

/**
 * Resource to find/update/remove claims.
 */
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Path("/claims")
@PermitAll
public class ClaimResource {

    @Inject
    private ClaimRepository claimRepo;

    @POST
    public Claim create(@Validated(Validations.Create.class) final Claim claim) {
        return claimRepo.create(claim);
    }

    @GET
    public Collection<Claim> find(
            @QueryParam("device_id") final Long deviceId,
            @QueryParam("user_id") final Long userId,
            @QueryParam("active") final Boolean active
    ) {
        return claimRepo.find(deviceId, userId, active);
    }

    @GET
    @Path("/{id}")
    public Claim findById(@PathParam("id") final long id) {
        return claimRepo.findById(id);
    }

    @PUT
    @Path("/{id}")
    public Claim update(
            @PathParam("id") final Long id,
            @Validated(Validations.Update.class) final Claim claim
    ) {
        return claimRepo.update(claim);
    }

    @DELETE
    @Path("/{id}")
    public Claim remove(@PathParam("id") final long id) {
        return claimRepo.delete(id);
    }
}
