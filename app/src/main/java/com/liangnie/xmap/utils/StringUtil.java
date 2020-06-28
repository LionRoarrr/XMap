package com.liangnie.xmap.utils;

public class StringUtil {
    public static boolean isEmptyOrNull(String text) {
        return text == null || text.trim().length() == 0;
    }
}
