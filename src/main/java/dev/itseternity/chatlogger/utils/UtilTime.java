package dev.itseternity.chatlogger.utils;

import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;

@UtilityClass
public class UtilTime {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yy HH:mm:ss z");

    public static String formatTime(long timestamp) {
        return SIMPLE_DATE_FORMAT.format(timestamp);
    }

}
