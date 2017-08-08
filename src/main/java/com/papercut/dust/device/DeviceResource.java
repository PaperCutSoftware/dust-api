/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.device;

import com.papercut.dust.model.Validations;
import io.dropwizard.validation.Validated;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

/**
 * Resource to view/change a specific device.
 */
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Path("/devices")
@PermitAll
public class DeviceResource {

    private static final String DEFAULT_BASE_URL = "https://device-mgmt.papercut.software";

    @Inject
    private DeviceRepository deviceRepo;

    @POST
    public Device create(@Validated(Validations.Create.class) final Device device) {
        return deviceRepo.create(device);
    }

    @DELETE
    @Path("/{id:\\d+}")
    public Device delete(@PathParam("deviceId") final long deviceId) {
        return deviceRepo.delete(deviceId);
    }

    @DELETE
    @Path("/{nickname}")
    public Device deleteByName(@PathParam("nickname") final String nickname) {
        final Device device = deviceRepo.findByName(nickname);
        return deviceRepo.delete(device.id);
    }

    @GET
    public Collection<Device> find(@QueryParam("keyword") final String keyword) {
        final Collection<Device> devices;
        if (keyword == null || keyword.isEmpty()) {
            devices = deviceRepo.findAll();
        } else {
            devices = deviceRepo.findByKeyword(keyword);
        }
        return devices;
    }

    @GET
    @Path("/{id:\\d+}")
    public Device findById(@PathParam("id") final Long id) {
        return deviceRepo.findById(id);
    }

    @GET
    @Path("/{nickname}")
    public Device findByName(@PathParam("nickname") final String nickname) {
        return deviceRepo.findByName(nickname);
    }

    private byte[] qrCodeAsPng(final String baseUrl, final String nickname, final String suffix) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        QRCode.from(baseUrl + "/devices/" + nickname + suffix).withSize(500, 500).to(ImageType.PNG).writeTo(baos);
        final byte[] imageData = baos.toByteArray();
        return imageData;
    }

    @GET
    @Path("/{id:\\d+}/details-qr")
    @Produces("image/png")
    public Response deviceDetailsLinkQrCode(
            @PathParam("id") final Long id,
            @QueryParam("baseUrl") @DefaultValue(DEFAULT_BASE_URL) final String baseUrl
    ) throws IOException {
        final Device device = deviceRepo.findById(id);
        final byte[] detailsQr = qrCodeAsPng(baseUrl, device.nickname, "");
        return Response.ok(new ByteArrayInputStream(detailsQr)).build();
    }

    @GET
    @Path("/{nickname}/details-qr")
    @Produces("image/png")
    public Response deviceDetailsLinkQrCode(
            @PathParam("nickname") final String nickname,
            @QueryParam("baseUrl") @DefaultValue(DEFAULT_BASE_URL) final String baseUrl
    ) throws IOException {
        final byte[] detailsQr = qrCodeAsPng(baseUrl, nickname, "");
        return Response.ok(new ByteArrayInputStream(detailsQr)).build();
    }

    @GET
    @Path("/{id:\\d+}/claim-qr")
    @Produces("image/png")
    public Response directClaimLinkQrCode(
            @PathParam("id") final Long id,
            @QueryParam("baseUrl") @DefaultValue(DEFAULT_BASE_URL) final String baseUrl
    ) throws IOException {
        final Device device = deviceRepo.findById(id);
        final byte[] claimQr = qrCodeAsPng(baseUrl, device.nickname, "?claim=true");
        return Response.ok(new ByteArrayInputStream(claimQr)).build();
    }

    @GET
    @Path("/{nickname}/claim-qr")
    @Produces("image/png")
    public Response directClaimLinkQrCode(
            @PathParam("nickname") final String nickname,
            @QueryParam("baseUrl") @DefaultValue(DEFAULT_BASE_URL) final String baseUrl
    ) throws IOException {
        final byte[] claimQr = qrCodeAsPng(baseUrl, nickname, "?claim=true");
        return Response.ok(new ByteArrayInputStream(claimQr)).build();
    }

    @GET
    @Path("/{id:\\d+}/sticker")
    @Produces("image/png")
    public Response showSticker(
            @PathParam("id") final Long id,
            @QueryParam("baseUrl") @DefaultValue(DEFAULT_BASE_URL) final String baseUrl
    ) throws IOException {
        final Device device = deviceRepo.findById(id);
        return sticker(baseUrl, device);
    }

    @GET
    @Path("/{nickname}/sticker")
    @Produces("image/png")
    public Response showSticker(
            @PathParam("nickname") final String nickname,
            @QueryParam("baseUrl") @DefaultValue(DEFAULT_BASE_URL) final String baseUrl
    ) throws IOException, NoSuchUniqueDeviceException {
        final Device device = deviceRepo.findByName(nickname);
        return sticker(baseUrl, device);
    }

    private Response sticker(final String baseUrl, final Device device) throws IOException {
        final BufferedImage detailsQr = ImageIO.read(new ByteArrayInputStream(qrCodeAsPng(baseUrl, device.nickname, "")));
        final BufferedImage claimQr = ImageIO.read(new ByteArrayInputStream(qrCodeAsPng(baseUrl, device.nickname, "?claim=true")));

        final BufferedImage result = new BufferedImage(1080, 720, BufferedImage.TYPE_INT_RGB);
        final Graphics g = result.getGraphics();

        g.setColor(Color.WHITE);

        final int border = 2;
        g.fillRect(border, border, result.getWidth() - 2 * border, result.getHeight() - 2 * border);

        // QRs
        g.drawImage(detailsQr, border, border, null);
        g.drawImage(claimQr, 500 + 2 * border, border, null);
        // labels
        g.setColor(Color.BLACK);
        g.setFont(new Font("monospace", Font.BOLD, 60));
        g.drawString("DETAILS", 100, 520);
        g.drawString("CLAIM", 650, 520);

        g.fillRect(0, 560, result.getWidth(), 2);

        // device details
        g.setFont(new Font("monospace", Font.BOLD, 24));

        g.drawString(device.brand + " " + device.model, 40, 600);

        g.drawString("IP: " + device.ip, 40, 650);
        g.drawString("Name: " + device.nickname, 400, 650);

        g.drawString("Hostname: " + device.hostname, 40, 700);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(result, "png", baos);

        final byte[] imageData = baos.toByteArray();

        return Response.ok(new ByteArrayInputStream(imageData)).build();
    }

    @PUT
    @Path("/{id:\\d+}")
    public Device update(
            @PathParam("id") final Long id,
            @Validated(Validations.Update.class) final Device device
    ) {
        return deviceRepo.update(device);
    }

    @POST
    @Path("/test")
    public void insertTestData() {
        deviceRepo.loadTestData(false);
    }

    @PUT
    @Path("/test")
    public void updateTestData() {
        deviceRepo.loadTestData(true);
    }

}
