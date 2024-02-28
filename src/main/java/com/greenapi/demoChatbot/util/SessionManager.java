package com.greenapi.demoChatbot.util;

import com.greenapi.chatbot.pkg.state.State;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SessionManager {
    public static boolean isSessionExpired(State currentState) {
        var timestamp = (LocalDateTime) currentState.getData().get("last_touch_timestamp");

        boolean i = timestamp != null && Duration.between(timestamp, LocalDateTime.now()).toMinutes() >= 1;
        System.out.println(timestamp);
        System.out.println(i);
        if (timestamp != null) {
            System.out.println(Duration.between(timestamp, LocalDateTime.now()).toMinutes());
        }
        if (timestamp != null && Duration.between(timestamp, LocalDateTime.now()).toMinutes() >= 1) {
            currentState.getData().put("last_touch_timestamp", LocalDateTime.now());
            return true;
        }

        currentState.getData().put("last_touch_timestamp", LocalDateTime.now());
        return false;
    }
}
