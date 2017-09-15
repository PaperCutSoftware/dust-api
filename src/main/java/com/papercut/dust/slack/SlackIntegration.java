/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.slack;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.rtm.Event;
import allbegray.slack.rtm.EventListener;
import allbegray.slack.rtm.SlackRealTimeMessagingClient;
import allbegray.slack.webapi.SlackWebApiClient;
import com.papercut.dust.ConfigurationHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;

/**
 * Slack client integration.
 */
@Configured
@Dependent
public class SlackIntegration implements SlackClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackIntegration.class);

    private SlackWebApiClient webClient;
    private SlackRealTimeMessagingClient rtmClient;

    @PostConstruct
    public void init() {
        webClient = webClient();
        rtmClient = rtmClient();
        LOGGER.info("Web and RTM clients created.");
        startListening();
    }

    @PreDestroy
    public void destroy() {
        rtmClient.close();
        LOGGER.info("Closed RTM client.");
    }

    private void startListening() {
        rtmClient.connect();
        LOGGER.info("PIN_ADDED/PIN_REMOVED listeners added.");
    }

    /**
     * Convenience method to create an authorized web API client.
     *
     * @return Slack web client
     */
    private SlackWebApiClient webClient() {
        return SlackClientFactory.createWebApiClient(
                ConfigurationHolder.get().getSlackConfig().getBotToken());
    }

    /**
     * Convenience method to create an authorized RTM client.
     *
     * @return Slack RTM client
     */
    private SlackRealTimeMessagingClient rtmClient() {
        return SlackClientFactory.createSlackRealTimeMessagingClient(
                ConfigurationHolder.get().getSlackConfig().getBotToken());
    }

    /**
     * Post a message to a Slack channel as a bot.
     *
     * @param target user or channel to send the message to
     * @param text the text
     * @return Slack timestamp of the posted message
     */
    @Override
    public String postMessage(final String target, final String text) {
        LOGGER.info("Posting to Slack channel/user: {}, message: {}", target, text);
        return webClient.postMessage(target, text,
                ConfigurationHolder.get().getSlackConfig().getBotUsername(), false);
    }

    @Override
    public void pinMessage(final String channel, final String timestamp) {
        LOGGER.info("Pinning to Slack channel: {}, timestamp: {}", channel, timestamp);
        webClient.pinMessage(channel, timestamp);
    }

    @Override
    public void unpinMessage(final String channel, final String timestamp) {
        LOGGER.info("Unpinning from Slack channel: {}, timestamp: {}", channel, timestamp);
        webClient.unpinMessage(channel, timestamp);
    }

    @Override
    public void addListener(final Event event, final EventListener listener) {
        rtmClient.addListener(event, listener);
    }

    @Override
    public String getUsernameById(final String userId) {
        return webClient.getUserInfo(userId).getName();
    }

}
