package com.greenapi.demoChatbot.util;

public enum Language {
    ENG("en"),
    RU("ru"),
    HE("he"),
    ES("es"),
    AR("ar"),
    KZ("kz");

    private final String value;

    Language(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
