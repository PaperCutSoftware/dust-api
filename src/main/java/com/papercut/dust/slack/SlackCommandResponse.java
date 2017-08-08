/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.slack;

import allbegray.slack.type.Attachment;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Response to a Slack slash command.
 */
public class SlackCommandResponse {

    @JsonProperty("response_type")
    private String responseType;
    @JsonProperty("text")
    private String text;
    @JsonProperty("attachments")
    private List<Attachment> attachments;

    public SlackCommandResponse() {
    }

    public SlackCommandResponse(final String responseType, final String text, final List<Attachment> attachments) {
        this.responseType = responseType;
        this.text = text;
        this.attachments = attachments;
    }

    /**
     * @return response type
     */
    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(final String responseType) {
        this.responseType = responseType;
    }

    /**
     * @return response text
     */
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return list of attachments
     */
    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(final List<Attachment> attachments) {
        this.attachments = attachments;
    }

}
