package com.papercut.dust.slack;

import allbegray.slack.rtm.Event;
import allbegray.slack.rtm.EventListener;

import javax.enterprise.context.Dependent;

/**
 * Slack client that does nothing.
 */
@NoOp
@Dependent
public class SlackNoOp implements SlackClient {

    @Override
    public String postMessage(String target, String text) {
        return null;
    }

    @Override
    public void pinMessage(String channel, String timestamp) {

    }

    @Override
    public void unpinMessage(String channel, String timestamp) {

    }

    @Override
    public void addListener(Event event, EventListener listener) {

    }

    @Override
    public String getUsernameById(String userId) {
        return null;
    }
}
