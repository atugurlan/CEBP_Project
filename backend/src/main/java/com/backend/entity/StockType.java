package com.backend.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StockType {
    APPLE,
    MICROSOFT,
    GOOGLE,
    AMAZON,
    META,
    TESLA,
    NVIDIA;

    @JsonCreator
    public static StockType forValue(String value) {
        return StockType.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
