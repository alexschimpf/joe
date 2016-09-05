package com.tendersaucer.joe.util;

/**
 * Created by Alex on 8/14/2016.
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isEmpty(String str) {
        return str == null || str.equals("");
    }

    public static boolean equalsAny(String str, String... others) {
        for (String other : others) {
            if (str.equals(other)) {
                return true;
            }
        }

        return false;
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
