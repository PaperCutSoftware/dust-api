/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust;

import com.papercut.dust.claim.AlreadyClaimedException;
import com.papercut.dust.claim.NoSuchClaimException;
import com.papercut.dust.device.NoSuchUniqueDeviceException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(final Exception exception) {
        if (exception instanceof AlreadyClaimedException) {
            return withStatus(exception, Response.Status.CONFLICT);
        } else if (exception instanceof NoSuchClaimException) {
            return withStatus(exception, Response.Status.CONFLICT);
        } else if (exception instanceof NoSuchUniqueDeviceException) {
            return withStatus(exception, Response.Status.NOT_FOUND);
        } else {
            return withStatus(exception, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private static Response withStatus(final Exception exception, final Response.Status status) {
        return Response.status(status)
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }

}
