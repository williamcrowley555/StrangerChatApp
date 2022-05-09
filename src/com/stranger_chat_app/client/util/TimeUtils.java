package com.stranger_chat_app.client.util;

public class TimeUtils {

    public static String convertToTimeString(long totalSecs) {
        int minutes = (int) ((totalSecs % 3600) / 60);
        int seconds = (int) (totalSecs % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }
}
