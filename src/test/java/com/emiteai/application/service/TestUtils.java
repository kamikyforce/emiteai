package com.emiteai.application.service;

import java.lang.reflect.Field;

public final class TestUtils {

    private TestUtils() {}

    public static void setStaticField(Class<?> type, String name, Object value) {
        try {
            Field field = type.getDeclaredField(name);
            field.setAccessible(true);
            field.set(null, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
