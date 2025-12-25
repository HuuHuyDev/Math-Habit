package com.kidsapp.utils;

/**
 * Constants class for the application
 */
public class Constants {
    // API Base URL
    // Thiết bị thật: dùng IP máy tính (cùng WiFi với điện thoại)
//     Emulator: đổi thành http://10.0.2.2:8080/api/
    // IP nhà: 192.168.1.35 | IP hiện tại: 192.168.34.177
//    public static final String BASE_URL = "http://192.168.34.177:8080/api/";
    public static final String BASE_URL = "http://192.168.1.35:8080/api/";

    // WebSocket URL
    public static final String WS_URL = "ws://192.168.1.35:8080/api/ws";
    
    // SharedPreferences Keys
    public static final String PREF_NAME = "KidsAppPrefs";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_ROLE = "user_role";
    public static final String KEY_AUTH_TOKEN = "auth_token";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    
    // User Roles
    public static final String ROLE_PARENT = "parent";
    public static final String ROLE_CHILD = "child";
    
    // Task Status
    public static final String TASK_STATUS_PENDING = "pending";
    public static final String TASK_STATUS_IN_PROGRESS = "in_progress";
    public static final String TASK_STATUS_COMPLETED = "completed";
    
    // Date Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
}

