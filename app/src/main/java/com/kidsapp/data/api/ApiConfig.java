package com.kidsapp.data.api;

import com.kidsapp.utils.Constants;

/**
 * API Configuration class
 */
public class ApiConfig {
    public static final String BASE_URL = Constants.BASE_URL;
    public static final String API_VERSION = "v1";
    
    // API Endpoints
    public static final String ENDPOINT_LOGIN = "auth/login";
    public static final String ENDPOINT_REGISTER = "auth/register";
    public static final String ENDPOINT_FORGOT_PASSWORD = "auth/forgot-password";
    public static final String ENDPOINT_LOGOUT = "auth/logout";
    
    public static final String ENDPOINT_PARENT_PROFILE = "parent/profile";
    public static final String ENDPOINT_CHILDREN = "parent/children";
    public static final String ENDPOINT_CHILD_DETAIL = "parent/child/{id}";
    
    public static final String ENDPOINT_TASKS = "tasks";
    public static final String ENDPOINT_TASK_DETAIL = "tasks/{id}";
    public static final String ENDPOINT_CREATE_TASK = "tasks/create";
    public static final String ENDPOINT_UPDATE_TASK = "tasks/{id}";
    public static final String ENDPOINT_DELETE_TASK = "tasks/{id}";
    
    public static final String ENDPOINT_ACTIVITY_LOGS = "activities";
    public static final String ENDPOINT_WEEKLY_PROGRESS = "reports/weekly";
    public static final String ENDPOINT_REPORTS = "reports";
}

