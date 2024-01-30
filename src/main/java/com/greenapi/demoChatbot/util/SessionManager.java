package com.greenapi.demoChatbot.util;

import com.greenapi.chatbot.pkg.state.State;

import java.time.Duration;
import java.time.LocalTime;

public class SessionManager {
    public static boolean isSessionExpired(State currentState) {
        var timestamp = (LocalTime) currentState.getData().get("last_touch_timestamp");
        if (timestamp != null && Duration.between(timestamp, LocalTime.now()).toMinutes() > 2) {
            currentState.getData().put("last_touch_timestamp", LocalTime.now());
            return true;
        }
        currentState.getData().put("last_touch_timestamp", LocalTime.now());
        return false;
    }
}
