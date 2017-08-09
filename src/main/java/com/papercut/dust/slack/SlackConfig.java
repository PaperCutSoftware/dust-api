/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.slack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Configuration for the Slack side.
 */
public class SlackConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackConfig.class);

    @JsonProperty("botToken")
    private String botToken;

    @JsonProperty("commandVerificationTokens")
    private List<String> commandVerificationTokens;

    @JsonProperty("help")
    private String help;

    @JsonProperty("botUsername")
    private String botUsername;

    @JsonProperty("channel")
    private String channel;

    @JsonProperty("pinningChannel")
    private String pinningChannel;

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(final String slackBotAuthToken) {
        this.botToken = slackBotAuthToken;
    }

    public List<String> getCommandVerificationTokens() {
        return commandVerificationTokens;
    }

    public void setCommandVerificationTokens(final List<String> commandVerificationTokens) {
        this.commandVerificationTokens = commandVerificationTokens;
    }

    public String getBotUsername() {
        return botUsername;
    }

    public void setBotUsername(final String botUsername) {
        this.botUsername = botUsername;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPinningChannel() {
        return pinningChannel;
    }

    public void setPinningChannel(String pinningChannel) {
        this.pinningChannel = pinningChannel;
    }

    public boolean configured() {
        return StringUtils.isNotBlank(getBotToken()) && commandVerificationTokens != null && !commandVerificationTokens.isEmpty();
    }

}
