package org.task.crypto.enums;

import java.util.HashMap;
import java.util.Map;

public enum PriceType {
    MIN, MAX;

    private static final Map<String, PriceType> STRING_TO_ENUM = new HashMap<>();

    static {
        for (PriceType type : values()) {
            STRING_TO_ENUM.put(type.name().toLowerCase(), type);
        }
    }

    public static PriceType fromString(String typeStr) {
        PriceType type = STRING_TO_ENUM.get(typeStr.toLowerCase());
        if (type == null) {
            throw new IllegalArgumentException("Unknown price type: " + typeStr);
        }
        return type;
    }
}
