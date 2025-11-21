package com.kidsapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Date utility class
 */
public class DateUtils {
    private static final SimpleDateFormat dateTimeFormat = 
        new SimpleDateFormat(Constants.DATETIME_FORMAT, Locale.getDefault());

    /**
     * Get time ago string (e.g., "5 phút trước")
     */
    public static String getTimeAgo(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return "";
        }
        
        try {
            Date date = dateTimeFormat.parse(dateTime);
            if (date == null) {
                return "";
            }
            
            long diff = System.currentTimeMillis() - date.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            
            if (days > 0) {
                return days + " ngày trước";
            } else if (hours > 0) {
                return hours + " giờ trước";
            } else if (minutes > 0) {
                return minutes + " phút trước";
            } else {
                return "Vừa xong";
            }
        } catch (ParseException e) {
            return "";
        }
    }
}

