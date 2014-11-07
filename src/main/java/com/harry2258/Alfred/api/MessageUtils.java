package com.harry2258.Alfred.api;


import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik at 1:35 PM on 10/31/2014.
 */
public class MessageUtils {

    public static void sendChannel(MessageEvent event, String message) {
        //message =Colors.removeColors(message);
        //Colors.removeFormatting(message);
        //Colors.removeFormattingAndColors(message);
        event.getChannel().send().message(message);
    }

    public static void sendPrivate(MessageEvent event, String message) {
        event.getUser().send().message(message);
    }

    public static void sendUserNotice(MessageEvent event, String message) {
        event.getUser().send().notice(message);
    }
}