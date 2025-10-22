package com.example.helphomebackend.enums;

import lombok.Getter;

import java.lang.reflect.Array;
import java.util.Arrays;

@Getter
public enum LanguageCategory {
    COMMON("common"),
    SUCCESS("success"),
    ERROR("error");

    private final String value;

    LanguageCategory(String value) {
        this.value = value;
    }

    public static boolean isValid(String category) {
        return Arrays.stream(values())
                .anyMatch(c -> c.value.equalsIgnoreCase(category));
    }

    public static LanguageCategory fromValue(String value) {
        return Arrays.stream(values())
                .filter(category -> category.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리: " + value));
    }
}
