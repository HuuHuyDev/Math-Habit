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
    public static final String ENDPOINT_FORGOT_PASSWORD = "auth/forgot-password";
    public static final String ENDPOINT_VERIFY_OTP = "auth/verify-otp";
    public static final String ENDPOINT_RESET_PASSWORD = "auth/reset-password";
    
    public static final String ENDPOINT_PARENT_PROFILE = "parent/profile";
    public static final String ENDPOINT_CHILDREN = "parent/children";
    public static final String ENDPOINT_CHILD_DETAIL = "parent/children/{id}";
    public static final String ENDPOINT_CREATE_CHILD = "parent/children";
    public static final String ENDPOINT_UPDATE_CHILD = "parent/children/{id}";
    public static final String ENDPOINT_DELETE_CHILD = "parent/children/{id}";
    
    // Task endpoints
    public static final String ENDPOINT_TASKS = "tasks";
    public static final String ENDPOINT_TASKS_BY_CHILD = "tasks/child/{childId}";
    public static final String ENDPOINT_TASKS_BY_STATUS = "tasks/child/{childId}/by-status";
    public static final String ENDPOINT_TASKS_BY_TYPE = "tasks/child/{childId}/by-type";
    public static final String ENDPOINT_TASK_DETAIL = "tasks/{id}";
    public static final String ENDPOINT_TASK_SUBMIT = "tasks/{id}/submit";
    public static final String ENDPOINT_TASK_COMPLETE = "tasks/{id}/complete";
    public static final String ENDPOINT_CREATE_TASK = "tasks/create";
    public static final String ENDPOINT_UPDATE_TASK = "tasks/{id}";
    public static final String ENDPOINT_DELETE_TASK = "tasks/{id}";
    
    // Upload endpoints
    public static final String ENDPOINT_UPLOAD_IMAGE = "upload/image";
    public static final String ENDPOINT_UPLOAD_VIDEO = "upload/video";
    
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
    
    // Habits
    public static final String ENDPOINT_HABITS = "habits";
    public static final String ENDPOINT_HABIT_DETAIL = "habits/{id}";
    public static final String ENDPOINT_HABITS_BY_CHILD = "habits/child/{childId}";
    public static final String ENDPOINT_HABIT_COMPLETE = "habits/{id}/complete";
    public static final String ENDPOINT_HABIT_APPROVE = "habits/logs/{id}/approve";
    public static final String ENDPOINT_HABIT_REJECT = "habits/logs/{id}/reject";
    public static final String ENDPOINT_HABIT_PENDING = "habits/pending-approvals";
    public static final String ENDPOINT_HABIT_TEMPLATES = "habits/templates";
    
    // Quizzes
    public static final String ENDPOINT_QUIZZES = "quizzes";
    public static final String ENDPOINT_QUIZ_DETAIL = "quizzes/{id}";
    public static final String ENDPOINT_QUIZZES_BY_CHILD = "quizzes/child/{childId}";
    public static final String ENDPOINT_QUIZZES_ASSIGNED = "quizzes/assigned";
    public static final String ENDPOINT_QUIZ_START = "quizzes/{id}/start";
    public static final String ENDPOINT_QUIZ_SUBMIT = "quizzes/{id}/submit";
    public static final String ENDPOINT_QUIZ_AVAILABLE_QUESTIONS = "quizzes/available-questions";
    public static final String ENDPOINT_QUIZ_SUGGEST_REWARD = "quizzes/suggest-reward";
    
    // Task Assignment (Giao bài tập/thói quen)
    public static final String ENDPOINT_TASK_SUBJECTS = "task-assignment/subjects";
    public static final String ENDPOINT_TASK_EXERCISES = "task-assignment/exercises";
    public static final String ENDPOINT_TASK_EXERCISES_FOR_CHILD = "task-assignment/exercises/for-child/{childId}";
    public static final String ENDPOINT_TASK_HABIT_CATEGORIES = "task-assignment/habit-categories";
    public static final String ENDPOINT_TASK_HABITS = "task-assignment/habits";
    public static final String ENDPOINT_TASK_HABITS_FOR_CHILD = "task-assignment/habits/for-child/{childId}";
    public static final String ENDPOINT_TASK_ASSIGN = "task-assignment/assign";
    public static final String ENDPOINT_TASK_ASSIGN_MULTIPLE = "task-assignment/assign-multiple";
    public static final String ENDPOINT_TASK_UPDATE = "task-assignment/tasks/{taskId}";
    public static final String ENDPOINT_TASK_DELETE = "task-assignment/tasks/{taskId}";
    public static final String ENDPOINT_TASK_GET = "task-assignment/tasks/{taskId}";
}

