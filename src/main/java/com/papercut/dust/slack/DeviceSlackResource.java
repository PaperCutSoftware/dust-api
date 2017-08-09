/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.slack;

import allbegray.slack.rtm.Event;
import com.fasterxml.jackson.databind.JsonNode;
import com.papercut.dust.ConfigurationHolder;
import com.papercut.dust.claim.AlreadyClaimedException;
import com.papercut.dust.claim.Claim;
import com.papercut.dust.claim.ClaimRepository;
import com.papercut.dust.claim.NoSuchClaimException;
import com.papercut.dust.device.Device;
import com.papercut.dust.device.DeviceEvent;
import com.papercut.dust.device.DeviceRepository;
import com.papercut.dust.device.NoSuchUniqueDeviceException;
import com.papercut.dust.user.User;
import com.papercut.dust.user.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.stream.Collectors.toList;

/**
 * REST resource for the Slack side of integration.
 */
@Path("/devices/slack")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class DeviceSlackResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceSlackResource.class);

    private static final String UNCLAIM_COMMAND = "/unclaim";
    private static final String CLAIM_COMMAND = "/claim";
    private static final String LIST_COMMAND = "/list";
    private static final String SHOW_COMMAND = "/show";
    private static final String UNCLAIM_ALL_ARG = "all";

    private ExecutorService executor;

    @Inject @Any
    private Instance<SlackClient> slackAccessor;
    @Inject
    private DeviceRepository deviceRepository;
    @Inject
    private ClaimRepository claimRepository;
    @Inject
    private UserRepository userRepository;

    private SlackClient slack;

    @PostConstruct
    public void addSlackPinListeners() {
        if (slackConfig().configured()) {
            slack = slackAccessor.select(new ConfiguredAnnotationLiteral()).get();
        } else {
            slack = slackAccessor.select(new NoOpAnnotationLiteral()).get();
        }
        executor = Executors.newSingleThreadExecutor();
        slack.addListener(Event.PIN_ADDED, this::onPin);
        slack.addListener(Event.PIN_REMOVED, this::onUnpin);
    }

    /**
     * Slack command endpoint. This is where the application is receiving slash commands from Slack and returning
     * synchronous responses to them.
     *
     * @param params parameters posted from Slack
     * @return synchronous response for the command
     */
    @Path("/command")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public SlackCommandResponse receiveSlackCommand(final MultivaluedMap<String, String> params) {
        LOGGER.debug("Received Slack command: {}", params);
        final String token = params.getFirst("token");
        if (slackConfig().getCommandVerificationTokens().contains(token)) {
            final String command = params.getFirst("command");
            switch (command) {
                case LIST_COMMAND:
                    return listDevices();
                case SHOW_COMMAND:
                    return showDevice(params);
                case CLAIM_COMMAND:
                    return claim(params);
                case UNCLAIM_COMMAND:
                    return unclaim(params);
                default:
                    throw new IllegalArgumentException("Unknown command: " + command);
            }
        } else {
            throw new IllegalStateException("Token error");
        }
    }

    @Path("/message")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postMessage(final MultivaluedMap<String, String> params) {
        final String text = params.getFirst("text");
        final String who = params.getFirst("who");
        slack.postMessage(who, text);
        return Response.ok().build();
    }

    private SlackCommandResponse listDevices() {
        return ephemeralResponseWith(constructDeviceList());
    }

    /**
     * @param params parameters posted from Slack
     * @return an ephemeral info message for the sender
     */
    private SlackCommandResponse claim(final MultivaluedMap<String, String> params) {
        final String text = params.getFirst("text");
        if (StringUtils.isEmpty(text)) {
            final String command = params.getFirst("command");
            return help(command);
        } else {
            final String username = params.getFirst("user_name");
            return claimByText(text, username);
        }
    }

    private SlackCommandResponse showDevice(final MultivaluedMap<String, String> params) {
        final String text = params.getFirst("text");
        if (StringUtils.isEmpty(text)) {
            final String command = params.getFirst("command");
            return help(command);
        } else {
            return showByText(text);
        }
    }

    private SlackCommandResponse claimByText(final String text, final String username) {
        try {
            final Device d = deviceRepository.findUniqueByKeyword(text);
            final User u = userRepository.findBySlackUsername(username);
            claimRepository.create(Claim.startingNow(d, u));
            return ephemeralResponseWith("Device claimed: " + d.getNickname());
        } catch (NoSuchUniqueDeviceException | AlreadyClaimedException e) {
            return ephemeralResponseWith(e.getMessage());
        }
    }

    private SlackCommandResponse showByText(final String text) {
        try {
            final Device d = deviceRepository.findUniqueByKeyword(text);
            return ephemeralResponseWith("Device details: " + d);
        } catch (NoSuchUniqueDeviceException e) {
            return ephemeralResponseWith(e.getMessage());
        }
    }

    private String constructDeviceList() {
        final Collection<Device> devices = deviceRepository.findAll();
        final StringBuilder sb = new StringBuilder();
        devices.forEach(device -> {
            sb.append(device.getNickname());
            sb.append(device.getClaimedBy() != null ? " claimed" : " available");
            sb.append("\n");
        });
        return sb.toString();
    }

    private SlackCommandResponse unclaim(final MultivaluedMap<String, String> params) {
        final String text = params.getFirst("text");
        final String username = params.getFirst("user_name");

        if (StringUtils.isEmpty(text)) {
            final String command = params.getFirst("command");
            return help(command);
        } else if (UNCLAIM_ALL_ARG.equalsIgnoreCase(text)) {
            return unclaimAll(username);
        } else {
            return unclaimByText(text, username);
        }
    }

    private SlackCommandResponse unclaimByText(final String text, final String username) {
        try {
            final Device d = deviceRepository.findUniqueByKeyword(text);
            final User u = userRepository.findBySlackUsername(username);
            final Claim activeClaim = claimRepository.findActive(d.getId(), u.getId());
            claimRepository.delete(activeClaim.getId());
            return ephemeralResponseWith("Device unclaimed: " + d.getNickname());
        } catch (NoSuchUniqueDeviceException | NoSuchClaimException e) {
            return ephemeralResponseWith(e.getMessage());
        }
    }

    /**
     * Show command help.
     *
     * @return an ephemeral message showing the help
     */
    private SlackCommandResponse help(final String command) {
        final String formattedMessage
                = String.format(
                        "Type %s <device name> which can uniquely identify the device. E.g %s toshiba2050",
                        command,
                        command);
        return ephemeralResponseWith(formattedMessage);
    }

    private static SlackCommandResponse ephemeralResponseWith(final String text) {
        final SlackCommandResponse response = new SlackCommandResponse();
        response.setResponseType("ephemeral");
        response.setText(text);
        return response;
    }

    public void handleDeviceEvent(@Observes final DeviceEvent event) {
        switch (event.type) {
            case CLAIMED:
                sendNotifications(event.claim, true);
                break;
            case UNCLAIMED:
                sendNotifications(event.claim, false);
                break;
            default:
                throw new IllegalArgumentException("Unknown event: " + event.type);
        }
    }

    private void sendNotifications(final Claim claim, final boolean claimed) {
        final String action = claimed ? "claimed" : "unclaimed";
        final String notificationMessage = String.format("Device %s was %s by %s", claim.deviceName, action, claim.username);

        executor.submit(() -> {
            notifyChannel(notificationMessage);
            updatePinnedMessages(claimed, claim);
            notifyWatchers(claim, notificationMessage);
        });
    }

    private void notifyChannel(final String notificationMessage) {
        final String channel = slackConfig().getChannel();
        final boolean channelEnabled = channel != null && !channel.isEmpty();
        if (channelEnabled) {
            slack.postMessage(channel, notificationMessage);
        }
    }

    private void updatePinnedMessages(final boolean claimed, final Claim claim) {
        final String pinningChannel = slackConfig().getPinningChannel();
        final boolean pinningEnabled = pinningChannel != null && !pinningChannel.isEmpty();
        if (pinningEnabled) {
            if (claimed) {
                final String pinnedMessage = String.format("%s - %s", claim.deviceName, claim.username);
                final String pinTimestamp = slack.postMessage(pinningChannel, pinnedMessage);
                claimRepository.update(claim.withSlackTimestamp(pinTimestamp));
                slack.pinMessage(pinningChannel, pinTimestamp);
            } else {
                final String pinTimestamp = claim.slackTimestamp;
                final boolean hasTimestamp = pinTimestamp != null && !pinTimestamp.isEmpty();
                if (hasTimestamp) {
                    slack.unpinMessage(pinningChannel, pinTimestamp);
                }
            }
        }
    }

    private void notifyWatchers(final Claim claim, final String notificationMessage) {
        userRepository.findWatchersOfDevice(claim.deviceId).stream()
                .filter(u -> u.getId() != claim.userId && u.getSlackUsername().isPresent())
                .forEach(u -> {
                    slack.postMessage("@" + u.getSlackUsername().get(), notificationMessage);
                });
    }

    private SlackCommandResponse unclaimAll(String username) {
        final User u = userRepository.findBySlackUsername(username);
        final List<String> unclaimedDeviceNames = claimRepository
                .findActives(u.getId()).stream()
                .map(c -> {
                    claimRepository.delete(c.getId());
                    return c.deviceName;
                })
                .collect(toList());
        return ephemeralResponseWith("Devices unclaimed: " + unclaimedDeviceNames);
    }

    void handlePinning(final JsonNode node, final PinningHandler handler) {
        final String pinningChannel = slackConfig().getPinningChannel();
        final boolean pinningEnabled = pinningChannel != null && !pinningChannel.isEmpty();
        final String channelId = node.get("channel_id").asText();
        if (pinningEnabled && channelId.equals(pinningChannel)) {
            final String userId = node.get("user").asText();
            final String username = slack.getUsernameById(userId);
            final String botUsername = slackConfig().getBotUsername();
            if (botUsername != null && !botUsername.equals(username)) {
                final JsonNode item = node.get("item");
                final String itemType = item.get("type").asText();
                if ("message".equals(itemType)) {
                    final String text = item.get("message").get("text").asText();
                    handler.handle(username, text);
                }
            }
        }
    }

    void onPin(final JsonNode node) {
        handlePinning(node, (username, text) -> {
            LOGGER.info("Pinned message by {}: {} --> trying to claim", username, text);
            claimByText(text, username);
        });
    }

    void onUnpin(final JsonNode node) {
        handlePinning(node, (username, text) -> {
            LOGGER.info("Unpinned message by {}: {} --> trying to unclaim", username, text);
            unclaimByText(text, username);
        });
    }

    interface PinningHandler {

        void handle(final String username, final String text);
    }

    private static SlackConfig slackConfig() {
        return ConfigurationHolder.get().getSlackConfig();
    }

}
