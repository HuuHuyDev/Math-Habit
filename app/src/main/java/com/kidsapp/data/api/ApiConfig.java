package com.kidsapp.data.api;

import com.kidsapp.utils.Constants;

/**
 * API Configuration class
 * Cập nhật theo BE endpoints
 */
public class ApiConfig {
    public static final String BASE_URL = Constants.BASE_URL;
    public static final String API_VERSION = "v1";
    
    // ==================== AUTH ====================
    public static final String ENDPOINT_LOGIN = "auth/token";
    public static final String ENDPOINT_REGISTER = "auth/register";
    public static final String ENDPOINT_REFRESH_TOKEN = "auth/token/refresh";
    public static final String ENDPOINT_LOGOUT = "auth/tokens/{refreshToken}";
    public static final String ENDPOINT_GOOGLE_LOGIN = "auth/google";
    public static final String ENDPOINT_FACEBOOK_LOGIN = "auth/facebook";
    public static final String ENDPOINT_FORGOT_PASSWORD = "auth/forgot-password";
    public static final String ENDPOINT_VERIFY_OTP = "auth/verify-otp";
    public static final String ENDPOINT_RESET_PASSWORD = "auth/reset-password";
    
    // ==================== USER ====================
    public static final String ENDPOINT_USER_ME = "users/me";
    public static final String ENDPOINT_USER_AVATAR = "users/me/avatar";
    public static final String ENDPOINT_USER_PARENT_PROFILE = "users/me/parent";
    public static final String ENDPOINT_USER_CHILD_PROFILE = "users/me/child";
    
    // ==================== PARENT ====================
    public static final String ENDPOINT_PARENT_PROFILE = "parent/profile";
    public static final String ENDPOINT_CHILDREN = "parent/children";
    public static final String ENDPOINT_CHILD_DETAIL = "parent/children/{id}";
    public static final String ENDPOINT_CREATE_CHILD = "parent/children";
    public static final String ENDPOINT_UPDATE_CHILD = "parent/children/{id}";
    public static final String ENDPOINT_DELETE_CHILD = "parent/children/{id}";
    
    // ==================== CHILD (for child user) ====================
    public static final String ENDPOINT_MY_PROFILE = "children/me";
    public static final String ENDPOINT_MY_PARENTS = "children/me/parents";
    public static final String ENDPOINT_MY_SIBLINGS = "children/me/siblings";
    public static final String ENDPOINT_SEARCH_CHILDREN = "children/search";
    
    // ==================== TASK ====================
    public static final String ENDPOINT_TASKS = "tasks";
    public static final String ENDPOINT_TASKS_BY_CHILD = "tasks/child/{childId}";
    public static final String ENDPOINT_TASK_DETAIL = "tasks/{id}";
    public static final String ENDPOINT_UPDATE_TASK = "tasks/{id}";
    public static final String ENDPOINT_DELETE_TASK = "tasks/{id}";
    public static final String ENDPOINT_TASK_SUBMIT = "tasks/{id}/submit";
    public static final String ENDPOINT_TASK_APPROVE = "tasks/{id}/approve";
    public static final String ENDPOINT_TASK_REJECT = "tasks/{id}/reject";
    public static final String ENDPOINT_TASKS_UPCOMING = "tasks/child/{childId}/upcoming";
    public static final String ENDPOINT_TASKS_OVERDUE = "tasks/child/{childId}/overdue";
    
    // Task Templates
    public static final String ENDPOINT_TASK_HABIT_TEMPLATES = "tasks/habit-templates";
    public static final String ENDPOINT_TASK_HABIT_TEMPLATES_FOR_CHILD = "tasks/habit-templates/for-child/{childId}";
    public static final String ENDPOINT_TASK_EXERCISES = "tasks/exercises";
    public static final String ENDPOINT_TASK_EXERCISES_FOR_CHILD = "tasks/exercises/for-child/{childId}";
    
    // ==================== REPORT ====================
    public static final String ENDPOINT_WEEKLY_PROGRESS = "reports/weekly";
    public static final String ENDPOINT_BADGES = "reports/badges";
    public static final String ENDPOINT_DETAIL_REPORT = "reports/detail";
    
    // ==================== ACTIVITY LOG ====================
    public static final String ENDPOINT_ACTIVITY_LOGS = "activities";
    public static final String ENDPOINT_CHILD_ACTIVITIES = "activities/child/{childId}";
    
    // ==================== NOTIFICATION ====================
    public static final String ENDPOINT_NOTIFICATIONS = "notifications";
    public static final String ENDPOINT_NOTIFICATIONS_UNREAD = "notifications/unread";
    public static final String ENDPOINT_NOTIFICATIONS_UNREAD_COUNT = "notifications/unread/count";
    public static final String ENDPOINT_NOTIFICATIONS_READ = "notifications/{id}/read";
    public static final String ENDPOINT_NOTIFICATIONS_READ_ALL = "notifications/read-all";
    public static final String ENDPOINT_NOTIFICATION_DELETE = "notifications/{id}";
    
    // ==================== CHAT ====================
    public static final String ENDPOINT_CHAT_ROOMS = "chat/rooms";
    public static final String ENDPOINT_CHAT_ROOMS_BY_TYPE = "chat/rooms/type";
    public static final String ENDPOINT_CHAT_MESSAGES = "chat/rooms/{roomId}/messages";
    public static final String ENDPOINT_CHAT_SEND = "chat/send";
    public static final String ENDPOINT_CHAT_MARK_READ = "chat/rooms/{roomId}/read";
    public static final String ENDPOINT_CHAT_UNREAD_COUNT = "chat/unread-count";
    public static final String ENDPOINT_CHAT_CREATE_ROOM = "chat/rooms/create";
    
    // ==================== DEVICE (FCM) ====================
    public static final String ENDPOINT_FCM_TOKEN = "device/fcm-token";
    
    // ==================== AI CHAT ====================
    public static final String ENDPOINT_AI_CHAT = "chat";
}
