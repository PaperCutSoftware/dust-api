package com.papercut.dust.slack;

import allbegray.slack.rtm.Event;
import allbegray.slack.rtm.EventListener;

/**
 * Base class to describe a slack client.
 */
public interface SlackClient {

     String postMessage(final String target, final String text);

     void pinMessage(final String channel, final String timestamp);

     void unpinMessage(final String channel, final String timestamp);

     void addListener(final Event event, final EventListener listener);

     String getUsernameById(final String userId);
}
