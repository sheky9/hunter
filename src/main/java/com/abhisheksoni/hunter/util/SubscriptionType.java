package com.abhisheksoni.hunter.util;

import java.util.HashSet;
import java.util.Set;

public enum SubscriptionType {
    COLLABORATIONS("collabs"),
    MENTIONS("mentions"),
    QUESTIONS("questions");

    String description;

    SubscriptionType(String description) {
        this.description = description;
    }

    public static Set<String> getChannelMap() {
        Set<String> channels = new HashSet<>();
        for (SubscriptionType value : values()) {
            channels.add(value.toString());
        }
        return channels;
    }

    @Override
    public String toString() {
        return description;
    }
}
