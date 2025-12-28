package com.kidsapp.data.api;

import com.kidsapp.data.model.Child;
import com.kidsapp.data.model.Parent;
import com.kidsapp.data.model.Task;
import com.kidsapp.data.model.WeeklyProgress;
import com.kidsapp.data.request.AiChatRequest;
import com.kidsapp.data.request.CreateChildRequest;
import com.kidsapp.data.request.CreateTaskRequest;
import com.kidsapp.data.response.AiChatResponse;
import com.kidsapp.data.response.ChildSearchResponse;
import com.kidsapp.data.response.TaskResponse;
import com.kidsapp.data.websocket.ChatMessageDto;
import com.kidsapp.data.websocket.ChatMessageRequest;
import com.kidsapp.data.websocket.ChatRoomDto;
import java.util.List;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * API Service interface for Retrofit
 */
public interface ApiService {
    
    // Auth APIs
    @POST(ApiConfig.ENDPOINT_LOGIN)
    Call<ApiResponseWrapper<AuthResponse>> login(@Body LoginRequest request);
    
    @POST(ApiConfig.ENDPOINT_REGISTER)
    Call<ApiResponseWrapper<AuthResponse>> register(@Body RegisterRequest request);
    
    @POST(ApiConfig.ENDPOINT_REFRESH_TOKEN)
    Call<ApiResponseWrapper<AuthResponse>> refreshToken(@Body RefreshTokenRequest request);
    
    @POST(ApiConfig.ENDPOINT_GOOGLE_LOGIN)
    Call<ApiResponseWrapper<AuthResponse>> loginWithGoogle(@Body SocialLoginRequest request);
    
    @POST(ApiConfig.ENDPOINT_FACEBOOK_LOGIN)
    Call<ApiResponseWrapper<AuthResponse>> loginWithFacebook(@Body SocialLoginRequest request);
    
    @DELETE(ApiConfig.ENDPOINT_LOGOUT)
    Call<Void> logout(@Path("refreshToken") String refreshToken);
    
    @POST(ApiConfig.ENDPOINT_FORGOT_PASSWORD)
    Call<ApiResponseWrapper<Void>> forgotPassword(@Body ForgotPasswordRequest request);
    
    @POST(ApiConfig.ENDPOINT_VERIFY_OTP)
    Call<ApiResponseWrapper<Boolean>> verifyOtp(@Body VerifyOtpRequest request);
    
    @POST(ApiConfig.ENDPOINT_RESET_PASSWORD)
    Call<ApiResponseWrapper<Void>> resetPassword(@Body ResetPasswordRequest request);
    // ==================== EXERCISE APIs ====================
    
    /**
     * Lấy tất cả bài tập
     * GET /tasks/exercises
     */
    @GET("tasks/exercises")
    Call<ApiResponseWrapper<List<com.kidsapp.data.model.ExerciseContent>>> getAllExercises(
            @Query("categoryId") String categoryId,
            @Query("gradeLevel") Integer gradeLevel);
    
    /**
     * Lấy bài tập phù hợp với bé
     * GET /tasks/exercises/for-child/{childId}
     */
    @GET("tasks/exercises/for-child/{childId}")
    Call<ApiResponseWrapper<List<com.kidsapp.data.model.ExerciseContent>>> getExercisesForChild(
            @Path("childId") String childId,
            @Query("categoryId") String categoryId);
    
    /**
     * Lấy bài tập theo lớp
     * GET /exercises/grade/{gradeLevel}?childId={childId}
     */
    @GET("exercises/grade/{gradeLevel}")
    Call<ApiResponseWrapper<List<com.kidsapp.data.model.ExerciseContent>>> getExercisesByGrade(
            @Path("gradeLevel") int gradeLevel,
            @Query("childId") String childId
    );
    
    /**
     * Lấy bài tập theo môn
     * GET /exercises/subject/{subject}?childId={childId}
     */
    @GET("exercises/subject/{subject}")
    Call<ApiResponseWrapper<List<com.kidsapp.data.model.ExerciseContent>>> getExercisesBySubject(
            @Path("subject") String subject,
            @Query("childId") String childId
    );
    
    /**
     * Lấy chi tiết bài tập
     * GET /exercises/{exerciseId}?childId={childId}
     */
    @GET("exercises/{exerciseId}")
    Call<ApiResponseWrapper<com.kidsapp.data.model.ExerciseContent>> getExerciseDetail(
            @Path("exerciseId") String exerciseId,
            @Query("childId") String childId
    );
    
    /**
     * Lấy danh sách câu hỏi
     * GET /exercises/{exerciseId}/questions
     */
    @GET("exercises/{exerciseId}/questions")
    Call<ApiResponseWrapper<List<com.kidsapp.data.model.QuestionResponse>>> getExerciseQuestions(
            @Path("exerciseId") String exerciseId
    );
    
    /**
     * Bắt đầu làm bài
     * POST /exercises/{exerciseId}/start?childId={childId}
     */
    @POST("exercises/{exerciseId}/start")
    Call<ApiResponseWrapper<Void>> startExercise(
            @Path("exerciseId") String exerciseId,
            @Query("childId") String childId
    );
    
    /**
     * Submit bài làm
     * POST /exercises/submit?childId={childId}
     */
    @POST("exercises/submit")
    Call<ApiResponseWrapper<com.kidsapp.data.model.ExerciseResult>> submitExercise(
            @Query("childId") String childId,
            @Body com.kidsapp.data.request.SubmitAnswerRequest request
    );
    
    // Parent APIs
    @GET(ApiConfig.ENDPOINT_PARENT_PROFILE)
    Call<Parent> getParentProfile();
    
    @GET("parent/profile")
    Call<ApiResponseWrapper<ParentProfileResponse>> getParentProfileApi();
    
    @PUT("parent/profile")
    Call<ApiResponseWrapper<ParentProfileResponse>> updateParentProfile(@Body UpdateParentProfileRequest request);
    
    @GET(ApiConfig.ENDPOINT_CHILDREN)
    Call<List<Child>> getChildren();
    
    @GET(ApiConfig.ENDPOINT_CHILDREN)
    Call<ApiResponseWrapper<List<ChildResponse>>> getParentChildren();
    
    @GET(ApiConfig.ENDPOINT_CHILD_DETAIL)
    Call<ApiResponseWrapper<ChildResponse>> getChildDetail(@Path("id") String childId);
    
    // Child CRUD APIs
    @POST(ApiConfig.ENDPOINT_CREATE_CHILD)
    Call<ApiResponseWrapper<ChildResponse>> createChild(@Body CreateChildRequest request);
    
    @PUT(ApiConfig.ENDPOINT_UPDATE_CHILD)
    Call<ApiResponseWrapper<ChildResponse>> updateChild(@Path("id") String childId, @Body UpdateChildRequest request);
    
    @DELETE(ApiConfig.ENDPOINT_DELETE_CHILD)
    Call<ApiResponseWrapper<Void>> deleteChild(@Path("id") String childId);
    
    // Task APIs
    @GET(ApiConfig.ENDPOINT_TASKS)
    Call<List<Task>> getTasks(@Query("child_id") String childId);
    
    @GET(ApiConfig.ENDPOINT_TASKS_BY_CHILD)
    Call<ApiResponseWrapper<List<Task>>> getTasksByChild(
            @Path("childId") String childId,
            @Query("status") String status,
            @Query("taskType") String taskType,
            @Query("date") String date
    );
    
    @GET(ApiConfig.ENDPOINT_TASK_DETAIL)
    Call<ApiResponseWrapper<Task>> getTaskDetail(@Path("id") String taskId);
    
    @POST(ApiConfig.ENDPOINT_TASK_SUBMIT)
    Call<ApiResponseWrapper<Task>> submitTaskProof(
            @Path("id") String taskId,
            @Body TaskSubmissionRequest request
    );
    
    /**
     * Submit minh chứng với file upload trực tiếp
     * POST /tasks/{id}/submit
     * Multipart: file + note (optional)
     */
    @Multipart
    @POST(ApiConfig.ENDPOINT_TASK_SUBMIT)
    Call<ApiResponseWrapper<TaskProofResponse>> submitTaskProofWithFile(
            @Path("id") String taskId,
            @Part MultipartBody.Part file,
            @Part("note") okhttp3.RequestBody note
    );
    
    @POST(ApiConfig.ENDPOINT_TASKS)
    Call<ApiResponseWrapper<TaskResponse>> createTask(@Body CreateTaskRequest request);
    
    @PUT(ApiConfig.ENDPOINT_UPDATE_TASK)
    Call<ApiResponseWrapper<TaskResponse>> updateTask(
            @Path("id") String taskId, 
            @Body com.kidsapp.data.request.UpdateTaskRequest request);
    
    @DELETE(ApiConfig.ENDPOINT_DELETE_TASK)
    Call<ApiResponseWrapper<Void>> deleteTask(@Path("id") String taskId);
    
    // Task approve/reject (Parent only)
    @POST(ApiConfig.ENDPOINT_TASK_APPROVE)
    Call<ApiResponseWrapper<TaskResponse>> approveTask(@Path("id") String taskId);
    
    @POST(ApiConfig.ENDPOINT_TASK_REJECT)
    Call<ApiResponseWrapper<TaskResponse>> rejectTask(
            @Path("id") String taskId,
            @Body String reason
    );
    
    // Task upcoming/overdue
    @GET(ApiConfig.ENDPOINT_TASKS_UPCOMING)
    Call<ApiResponseWrapper<List<Task>>> getUpcomingTasks(
            @Path("childId") String childId,
            @Query("days") int days
    );
    
    @GET(ApiConfig.ENDPOINT_TASKS_OVERDUE)
    Call<ApiResponseWrapper<List<Task>>> getOverdueTasks(@Path("childId") String childId);
    
    // Task submission request
    class TaskSubmissionRequest {
        public String proofUrl;
        public String proofType; // IMAGE or VIDEO
        public String note;
        
        public TaskSubmissionRequest(String proofUrl, String proofType, String note) {
            this.proofUrl = proofUrl;
            this.proofType = proofType;
            this.note = note;
        }
    }
    
    // ==================== USER AVATAR API ====================
    
    /**
     * Upload avatar cho user hiện tại
     * PUT /users/me/avatar
     */
    @Multipart
    @PUT(ApiConfig.ENDPOINT_USER_AVATAR)
    Call<ApiResponseWrapper<UserResponse>> updateAvatar(@Part MultipartBody.Part file);
    
    // TaskProof response
    class TaskProofResponse {
        public String id;
        public String taskId;
        public String proofUrl;
        public String proofType;
        public String note;
        public String submittedAt;
        public String status; // pending, approved, rejected
        public String reviewedBy;
        public String reviewedAt;
        public String rejectionReason;
        public Boolean isActive;
    }
    
    // Activity & Report APIs
    @GET(ApiConfig.ENDPOINT_ACTIVITY_LOGS)
    Call<ApiResponseWrapper<List<ActivityLogResponse>>> getActivities(@Query("limit") int limit);
    
    @GET(ApiConfig.ENDPOINT_CHILD_ACTIVITIES)
    Call<ApiResponseWrapper<List<ActivityLogResponse>>> getChildActivities(
            @Path("childId") String childId, 
            @Query("limit") int limit);
    
    @GET(ApiConfig.ENDPOINT_WEEKLY_PROGRESS)
    Call<ApiResponseWrapper<WeeklyProgress>> getWeeklyProgress(@Query("childId") String childId);
    
    @GET(ApiConfig.ENDPOINT_BADGES)
    Call<ApiResponseWrapper<List<BadgeResponse>>> getChildBadges(@Query("childId") String childId);
    
    @GET(ApiConfig.ENDPOINT_DETAIL_REPORT)
    Call<ApiResponseWrapper<DetailReportResponse>> getDetailReport(
            @Query("childId") String childId,
            @Query("filter") String filter);
    
    // AI Chat API
    @POST(ApiConfig.ENDPOINT_AI_CHAT)
    Call<AiChatResponse> sendChatMessage(@Body AiChatRequest request);
    
    // Children Search API (tìm bạn bè) - không cần currentChildId, BE lấy từ JWT
    @GET(ApiConfig.ENDPOINT_SEARCH_CHILDREN)
    Call<ApiResponseWrapper<List<ChildSearchResponse>>> searchChildren(
            @Query("keyword") String keyword
    );
    
    // Lấy danh sách phụ huynh của child
    @GET(ApiConfig.ENDPOINT_MY_PARENTS)
    Call<ApiResponseWrapper<List<ParentInfoResponse>>> getMyParents();
    
    // Lấy danh sách anh chị em (các con cùng phụ huynh)
    @GET(ApiConfig.ENDPOINT_MY_SIBLINGS)
    Call<ApiResponseWrapper<List<ChildSearchResponse>>> getMySiblings();
    
    // Lấy thông tin profile của child hiện tại
    @GET(ApiConfig.ENDPOINT_MY_PROFILE)
    Call<ApiResponseWrapper<Child>> getMyProfile();
    
    // ==================== NOTIFICATION APIs ====================
    
    // Lấy tất cả thông báo
    @GET(ApiConfig.ENDPOINT_NOTIFICATIONS)
    Call<ApiResponseWrapper<List<NotificationResponse>>> getNotifications();
    
    // Lấy thông báo chưa đọc
    @GET(ApiConfig.ENDPOINT_NOTIFICATIONS_UNREAD)
    Call<ApiResponseWrapper<List<NotificationResponse>>> getUnreadNotifications();
    
    // Đếm số thông báo chưa đọc
    @GET(ApiConfig.ENDPOINT_NOTIFICATIONS_UNREAD_COUNT)
    Call<ApiResponseWrapper<Long>> getUnreadCount();
    
    // Đánh dấu đã đọc
    @PUT(ApiConfig.ENDPOINT_NOTIFICATIONS_READ)
    Call<ApiResponseWrapper<Void>> markNotificationAsRead(@Path("id") String notificationId);
    
    // Đánh dấu tất cả đã đọc
    @PUT(ApiConfig.ENDPOINT_NOTIFICATIONS_READ_ALL)
    Call<ApiResponseWrapper<Void>> markAllNotificationsAsRead();
    
    // Xóa thông báo
    @DELETE(ApiConfig.ENDPOINT_NOTIFICATION_DELETE)
    Call<ApiResponseWrapper<Void>> deleteNotification(@Path("id") String notificationId);
    
    // ==================== CHAT APIs ====================
    
    // Lấy danh sách phòng chat
    @GET(ApiConfig.ENDPOINT_CHAT_ROOMS)
    Call<ApiResponseWrapper<List<ChatRoomDto>>> getChatRooms(@Query("userId") String userId);
    
    // Lấy phòng chat theo loại
    @GET(ApiConfig.ENDPOINT_CHAT_ROOMS_BY_TYPE)
    Call<ApiResponseWrapper<List<ChatRoomDto>>> getChatRoomsByType(
            @Query("userId") String userId,
            @Query("roomType") String roomType
    );
    
    // Lấy tin nhắn trong phòng chat
    @GET(ApiConfig.ENDPOINT_CHAT_MESSAGES)
    Call<ApiResponseWrapper<List<ChatMessageDto>>> getChatMessages(
            @Path("roomId") String roomId,
            @Query("userId") String userId,
            @Query("page") int page,
            @Query("size") int size
    );
    
    // Tạo hoặc lấy phòng chat
    @POST(ApiConfig.ENDPOINT_CHAT_CREATE_ROOM)
    Call<ApiResponseWrapper<ChatRoomDto>> createOrGetChatRoom(
            @Query("user1Id") String user1Id,
            @Query("user2Id") String user2Id,
            @Query("roomType") String roomType
    );
    
    // Gửi tin nhắn trong phòng chat
    @POST(ApiConfig.ENDPOINT_CHAT_SEND)
    Call<ApiResponseWrapper<ChatMessageDto>> sendMessage(
            @Query("senderId") String senderId,
            @Body ChatMessageRequest request
    );
    
    // Đánh dấu đã đọc
    @POST(ApiConfig.ENDPOINT_CHAT_MARK_READ)
    Call<ApiResponseWrapper<Void>> markMessagesAsRead(
            @Path("roomId") String roomId,
            @Query("userId") String userId
    );
    
    // Đếm tin nhắn chưa đọc
    @GET(ApiConfig.ENDPOINT_CHAT_UNREAD_COUNT)
    Call<ApiResponseWrapper<Integer>> getChatUnreadCount(@Query("userId") String userId);
    
    // ==================== FCM TOKEN APIs ====================
    
    /**
     * Đăng ký FCM token
     * POST /device/fcm-token
     */
    @POST(ApiConfig.ENDPOINT_FCM_TOKEN)
    Call<ApiResponseWrapper<Void>> registerFcmToken(@Body java.util.Map<String, String> request);
    
    /**
     * Xóa FCM token (khi logout)
     * DELETE /device/fcm-token
     */
    @DELETE(ApiConfig.ENDPOINT_FCM_TOKEN)
    Call<ApiResponseWrapper<Void>> removeFcmToken();
    
    // Wrapper class cho API response
    class ApiResponseWrapper<T> {
        public boolean success;
        public String message;
        public T data;
    }
    
    // Request/Response classes
    class LoginRequest {
        public String email;
        public String password;
        
        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
    
    class RegisterRequest {
        public String email;
        public String password;
        public String fullName;
        public String role; // PARENT, CHILD
        
        public RegisterRequest(String email, String password, String fullName, String role) {
            this.email = email;
            this.password = password;
            this.fullName = fullName;
            this.role = role;
        }
    }
    
    class RefreshTokenRequest {
        public String refreshToken;
        
        public RefreshTokenRequest(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
    
    class SocialLoginRequest {
        public String accessToken;
        public String role;
        
        public SocialLoginRequest(String accessToken) {
            this.accessToken = accessToken;
            this.role = "PARENT"; // Default role for social login
        }
        
        public SocialLoginRequest(String accessToken, String role) {
            this.accessToken = accessToken;
            this.role = role;
        }
    }
    
    class ForgotPasswordRequest {
        public String email;
        
        public ForgotPasswordRequest(String email) {
            this.email = email;
        }
    }
    
    class VerifyOtpRequest {
        public String email;
        public String otp;
        
        public VerifyOtpRequest(String email, String otp) {
            this.email = email;
            this.otp = otp;
        }
    }
    
    class ResetPasswordRequest {
        public String email;
        public String otp;
        public String newPassword;
        
        public ResetPasswordRequest(String email, String otp, String newPassword) {
            this.email = email;
            this.otp = otp;
            this.newPassword = newPassword;
        }
    }
    
    class AuthResponse {
        public String accessToken;
        public String refreshToken;
        public String tokenType;
        public Long expiresIn;
        public UserResponse user;
    }
    
    class UserResponse {
        public String id;
        public String email;
        public String fullName;
        public String avatarUrl;
        public String role; // PARENT, CHILD, ADMIN
        public ParentResponse parentProfile;
        public ChildResponse childProfile;
    }
    
    class ParentResponse {
        public String id;
        public String userId;
    }
    
    class ParentProfileResponse {
        public String id;
        public String userId;
        public String fullName;
        public String email;
        public String phone;
        public String avatarUrl;
        public String address;
        public String occupation;
        public Boolean notificationEnabled;
        public Boolean dailyReportEnabled;
        public Boolean emailVerified;
        public int childrenCount;
    }
    
    class UpdateParentProfileRequest {
        public String fullName;
        public String phone;
        public String avatarUrl;
        public String address;
        public String occupation;
        public Boolean notificationEnabled;
        public Boolean dailyReportEnabled;
        public String newPassword;
        
        public UpdateParentProfileRequest() {}
        
        public UpdateParentProfileRequest(String fullName, String phone, String avatarUrl,
                                          String address, String occupation,
                                          Boolean notificationEnabled, Boolean dailyReportEnabled,
                                          String newPassword) {
            this.fullName = fullName;
            this.phone = phone;
            this.avatarUrl = avatarUrl;
            this.address = address;
            this.occupation = occupation;
            this.notificationEnabled = notificationEnabled;
            this.dailyReportEnabled = dailyReportEnabled;
            this.newPassword = newPassword;
        }
    }
    
    class ChildResponse {
        public String id;
        public String userId;
        public String parentId;
        public String name;
        public String nickname;
        public String avatarUrl;
        public String birthDate;
        public Integer grade;
        public String school;
        public Boolean gender;
        public Integer level;
        public String username;  // Email/username của child
        public Integer totalXp;
        public Integer coins;
        public Integer currentLevel;
        public Integer xpToNextLevel;
        public Integer currentStreak;
        public Integer longestStreak;
        public Integer totalTasksCompleted;
        public Integer totalStudyTimeMinutes;
        public Integer dailyGoalMinutes;
        public Integer dailyGoalExercises;
        public Float dailyProgress;
        public boolean isOnline;
    }
    
    class UpdateChildRequest {
        public String name;
        public String nickname;
        public String birthDate;
        public Integer grade;
        public String school;
        public String avatarUrl;
        public Boolean gender;
        public String newPassword;
        
        public UpdateChildRequest(String name, String nickname, String birthDate,
                                  Integer grade, String school, String avatarUrl,
                                  Boolean gender, String newPassword) {
            this.name = name;
            this.nickname = nickname;
            this.birthDate = birthDate;
            this.grade = grade;
            this.school = school;
            this.avatarUrl = avatarUrl;
            this.gender = gender;
            this.newPassword = newPassword;
        }
    }
    
    class ParentInfoResponse {
        public String id;
        public String userId;
        public String name;
        public String avatarUrl;
        public String phone;
        public boolean isOnline;
    }
    
    class NotificationResponse {
        public String id;
        public String type;
        public String title;
        public String message;
        public boolean isRead;
        public String readAt;
        public String referenceId;
        public String referenceType;
        public String iconUrl;
        public String createdAt;
        public String timeAgo;
    }
    
    class BadgeResponse {
        public String id;
        public String name;
        public String description;
        public String iconUrl;
        public String badgeType;
        public String rarity;
        public Integer xpReward;
        public Integer coinsReward;
        public boolean earned;
        public String earnedAt;
        public Integer requirementValue;
        public Integer progressValue;
        public Integer progressPercent;
    }
    
    class DetailReportResponse {
        public int totalHabits;
        public int totalExercises;
        public String totalTime;
        public List<ChartData> chartData;
        public List<AchievementData> achievements;
    }
    
    class ChartData {
        public String label;
        public int habitCount;
        public int exerciseCount;
    }
    
    class AchievementData {
        public String id;
        public String name;
        public String icon;
        public int count;
    }
    
    class ActivityLogResponse {
        public String id;
        public String childId;
        public String childName;
        public String childAvatar;
        public String activityType;
        public String description;
        public Integer xpEarned;
        public String iconUrl;
        public String createdAt;
        public String timeAgo;
    }
    
    // ==================== HABIT Request/Response ====================
    
    class CreateHabitRequest {
        public String childId;
        public String title;
        public String description;
        public String category; // learning, health, lifestyle, responsibility, self_discipline
        public String goalType; // count, time, streak
        public Integer goalValue;
        public Boolean requireApproval;
        public String proofType; // none, photo, audio, note
        public String iconName;
        public String colorHex;
        public Integer xpReward;
        public Integer coinReward;
        public String reminderTime;
        
        public CreateHabitRequest(String childId, String title, String description,
                                  String category, String goalType, Integer goalValue,
                                  Boolean requireApproval, String proofType,
                                  String iconName, String colorHex,
                                  Integer xpReward, Integer coinReward, String reminderTime) {
            this.childId = childId;
            this.title = title;
            this.description = description;
            this.category = category;
            this.goalType = goalType;
            this.goalValue = goalValue;
            this.requireApproval = requireApproval;
            this.proofType = proofType;
            this.iconName = iconName;
            this.colorHex = colorHex;
            this.xpReward = xpReward;
            this.coinReward = coinReward;
            this.reminderTime = reminderTime;
        }
    }
    
    class CompleteHabitRequest {
        public String proofUrl;
        public String note;
        
        public CompleteHabitRequest(String proofUrl, String note) {
            this.proofUrl = proofUrl;
            this.note = note;
        }
    }
    
    class RejectHabitRequest {
        public String reason;
        
        public RejectHabitRequest(String reason) {
            this.reason = reason;
        }
    }
    
    class HabitResponse {
        public String id;
        public String childId;
        public String childName;
        public String title;
        public String description;
        public String category;
        public String categoryLabel;
        public String goalType;
        public Integer goalValue;
        public String goalDescription;
        public Boolean requireApproval;
        public String proofType;
        public String iconName;
        public String colorHex;
        public Integer xpReward;
        public Integer coinReward;
        public Integer currentStreak;
        public Integer longestStreak;
        public String reminderTime;
        public Boolean isActive;
        public Boolean completedToday;
        public Integer todayProgress;
        public String todayStatus;
        public String createdAt;
    }
    
    // ==================== QUIZ Request/Response ====================
    
    class CreateQuizRequest {
        public String childId;
        public String title;
        public String description;
        public String subject; // math, english, logic, science, reading
        public String level; // easy, medium, hard
        public Integer questionCount;
        public String deadline; // ISO datetime
        public Integer xpReward;
        public Integer coinReward;
        public Boolean allowRetry;
        public Integer maxRetries;
        
        public CreateQuizRequest(String childId, String title, String description,
                                 String subject, String level, Integer questionCount,
                                 String deadline, Integer xpReward, Integer coinReward,
                                 Boolean allowRetry, Integer maxRetries) {
            this.childId = childId;
            this.title = title;
            this.description = description;
            this.subject = subject;
            this.level = level;
            this.questionCount = questionCount;
            this.deadline = deadline;
            this.xpReward = xpReward;
            this.coinReward = coinReward;
            this.allowRetry = allowRetry;
            this.maxRetries = maxRetries;
        }
    }
    
    class SubmitAnswerRequest {
        public String questionId;
        public String selectedOptionId;
        
        public SubmitAnswerRequest(String questionId, String selectedOptionId) {
            this.questionId = questionId;
            this.selectedOptionId = selectedOptionId;
        }
    }
    
    class QuizResponse {
        public String id;
        public String childId;
        public String childName;
        public String title;
        public String description;
        public String subject;
        public String subjectLabel;
        public String level;
        public String levelLabel;
        public Integer questionCount;
        public String deadline;
        public Boolean isExpired;
        public Integer xpReward;
        public Integer coinReward;
        public Boolean allowRetry;
        public Integer maxRetries;
        public Integer attemptCount;
        public String status;
        public String statusLabel;
        public Integer bestScore;
        public String completedAt;
        public String createdAt;
    }
    
    class AvailableQuestionsResponse {
        public Integer count;
    }
    
    class RewardSuggestionResponse {
        public Integer xpReward;
        public Integer coinReward;
    }
    
    // ==================== TASK ASSIGNMENT APIs ====================
    
    // Lấy danh sách thói quen mẫu
    @GET(ApiConfig.ENDPOINT_TASK_HABIT_TEMPLATES)
    Call<ApiResponseWrapper<List<HabitTemplateResponse>>> getHabitTemplates(
            @Query("categoryId") String categoryId);
    
    // Lấy danh sách thói quen phù hợp với bé
    @GET(ApiConfig.ENDPOINT_TASK_HABIT_TEMPLATES_FOR_CHILD)
    Call<ApiResponseWrapper<List<HabitTemplateResponse>>> getHabitTemplatesForChild(
            @Path("childId") String childId,
            @Query("categoryId") String categoryId);
    
    // Response classes for Task Assignment
    class HabitTemplateResponse {
        public String id;
        public String name;
        public String description;
        public String instructions;
        public String categoryId;
        public String categoryName;
        public String subcategory;
        public String iconName;
        public String colorHex;
        public String suggestedTime;
        public Integer estimatedMinutes;
        public Integer xpReward;
        public Integer coinsReward;
        public Integer minGrade;
        public Integer maxGrade;
        public Boolean isFeatured;
        public Integer usageCount;
        public Double completionRate;
    }
    
    // ==================== CHALLENGE QUEUE APIs (Quick Match) ====================
    
    /**
     * Lấy danh sách category cho Challenge
     * GET /api/exercise-categories?type=CHALLENGE
     */
    @GET(ApiConfig.ENDPOINT_CHALLENGE_CATEGORIES)
    Call<ApiResponseWrapper<List<com.kidsapp.data.model.Category>>> getChallengeCategories(
            @Query("type") String type
    );
    
    /**
     * Tham gia hàng đợi thách đấu nhanh
     * POST /api/challenge-queue/join
     */
    @POST(ApiConfig.ENDPOINT_CHALLENGE_QUEUE_JOIN)
    Call<ApiResponseWrapper<com.kidsapp.data.response.MatchFoundResponse>> joinQuickMatch(
            @Body com.kidsapp.data.request.JoinQueueRequest request
    );
    
    /**
     * Lấy trạng thái hàng đợi (polling)
     * GET /api/challenge-queue/status/{childId}
     */
    @GET(ApiConfig.ENDPOINT_CHALLENGE_QUEUE_STATUS)
    Call<ApiResponseWrapper<com.kidsapp.data.response.QueueResponse>> getQueueStatus(
            @Path("childId") String childId
    );
    
    /**
     * Rời khỏi hàng đợi
     * DELETE /api/challenge-queue/leave/{childId}
     */
    @DELETE(ApiConfig.ENDPOINT_CHALLENGE_QUEUE_LEAVE)
    Call<ApiResponseWrapper<String>> leaveQueue(
            @Path("childId") String childId
    );
    
    // ==================== CHALLENGE INVITE APIs (Invite Friend) ====================
    
    /**
     * Tạo Challenge mới và mời bạn
     * POST /api/challenges/create
     */
    @POST("challenges/create")
    Call<ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>> createChallenge(
            @Body com.kidsapp.data.request.CreateChallengeRequest request
    );
    
    /**
     * Mời child tham gia challenge
     * POST /api/challenges/{challengeId}/invite?creatorId={creatorId}
     */
    @POST("challenges/{challengeId}/invite")
    Call<ApiResponseWrapper<com.kidsapp.data.response.ChallengeInviteResponse>> inviteChild(
            @Path("challengeId") String challengeId,
            @Query("creatorId") String creatorId,
            @Body com.kidsapp.data.request.InviteChildRequest request
    );
    
    /**
     * Lấy danh sách bạn bè để mời
     * GET /api/challenges/friends?childId={childId}
     */
    @GET("challenges/friends")
    Call<ApiResponseWrapper<List<com.kidsapp.data.response.FriendResponse>>> getFriends(
            @Query("childId") String childId
    );
    
    /**
     * Lấy danh sách lời mời của tôi
     * GET /api/challenges/invites/my-invites?childId={childId}
     */
    @GET("challenges/invites/my-invites")
    Call<ApiResponseWrapper<List<com.kidsapp.data.response.ChallengeInviteResponse>>> getMyInvites(
            @Query("childId") String childId
    );
    
    /**
     * Chấp nhận lời mời
     * POST /api/challenges/invites/{inviteId}/accept?childId={childId}
     */
    @POST("challenges/invites/{inviteId}/accept")
    Call<ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>> acceptInvite(
            @Path("inviteId") String inviteId,
            @Query("childId") String childId
    );
    
    /**
     * Từ chối lời mời
     * POST /api/challenges/invites/{inviteId}/decline?childId={childId}
     */
    @POST("challenges/invites/{inviteId}/decline")
    Call<ApiResponseWrapper<String>> declineInvite(
            @Path("inviteId") String inviteId,
            @Query("childId") String childId
    );
}
