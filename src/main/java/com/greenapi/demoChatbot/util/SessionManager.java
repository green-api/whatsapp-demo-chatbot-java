package com.greenapi.demoChatbot.util;

import com.greenapi.chatbot.pkg.state.State;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SessionManager {
    public static boolean isSessionExpired(State currentState) {
        var timestamp = (LocalDateTime) currentState.getData().get("last_touch_timestamp");

        if (timestamp != null && Duration.between(timestamp, LocalDateTime.now()).toMinutes() >= 1) {
            currentState.getData().put("last_touch_timestamp", LocalDateTime.now());
            return true;
        }

        currentState.getData().put("last_touch_timestamp", LocalDateTime.now());
        return false;
    }
}
