package com.greenapi.demoChatbot.util;

import com.greenapi.client.pkg.models.notifications.MessageWebhook;

public class LogBuilder {
    public static String build(MessageWebhook webhook, String message) {
        return message +
            " messageId: " + webhook.getIdMessage() +
            " chatId: " + webhook.getSenderData().getChatId() +
            " senderId: " + webhook.getSenderData().getSender();
    }
}
