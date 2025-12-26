package com.kidsapp.data.api;

import com.kidsapp.utils.Constants;

/**
 * API Configuration class
 */
public class ApiConfig {
    public static final String BASE_URL = Constants.BASE_URL;
    public static final String API_VERSION = "v1";
    
    // API Endpoints
    public static final String ENDPOINT_LOGIN = "auth/token";
    public static final String ENDPOINT_REGISTER = "auth/register";
    public static final String ENDPOINT_REFRESH_TOKEN = "auth/token/refresh";
    public static final String ENDPOINT_LOGOUT = "auth/tokens/{refreshToken}";
    public static final String ENDPOINT_GOOGLE_LOGIN = "auth/google";
    public static final String ENDPOINT_FACEBOOK_LOGIN = "auth/facebook";
    
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
    
    // AI Chat
    public static final String ENDPOINT_AI_CHAT = "chat";
    
    // Children (tìm bạn bè)
    public static final String ENDPOINT_SEARCH_CHILDREN = "children/search";
    public static final String ENDPOINT_MY_PROFILE = "children/my-profile";
    public static final String ENDPOINT_MY_PARENTS = "children/my-parents";
    public static final String ENDPOINT_MY_SIBLINGS = "children/my-siblings";
    
    // Notifications
    public static final String ENDPOINT_NOTIFICATIONS = "notifications";
    public static final String ENDPOINT_NOTIFICATIONS_UNREAD = "notifications/unread";
    public static final String ENDPOINT_NOTIFICATIONS_UNREAD_COUNT = "notifications/unread/count";
    public static final String ENDPOINT_NOTIFICATIONS_READ = "notifications/{id}/read";
    public static final String ENDPOINT_NOTIFICATIONS_READ_ALL = "notifications/read-all";
}

