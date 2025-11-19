package com.kidsapp.utils;

/**
 * Constants class for the application
 */
public class Constants {
    // API Base URL
    public static final String BASE_URL = "https://api.kidsapp.com/";
    
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

