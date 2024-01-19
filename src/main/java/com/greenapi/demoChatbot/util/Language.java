package com.greenapi.demoChatbot.util;

public enum Language {
    ENG("eng"),
    RU("ru");

    private final String value;

    Language(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
