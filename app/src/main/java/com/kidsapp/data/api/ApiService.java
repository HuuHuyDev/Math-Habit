package com.kidsapp.data.api;

import com.kidsapp.data.model.ActivityLog;
import com.kidsapp.data.model.Badge;
import com.kidsapp.data.model.Child;
import com.kidsapp.data.model.Parent;
import com.kidsapp.data.model.Task;
import com.kidsapp.data.model.WeeklyProgress;
import com.kidsapp.data.request.AiChatRequest;
import com.kidsapp.data.request.CreateChildRequest;
import com.kidsapp.data.response.AiChatResponse;
import com.kidsapp.data.response.ChildSearchResponse;
import com.kidsapp.data.websocket.ChatMessageDto;
import com.kidsapp.data.websocket.ChatRoomDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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
    
    // Parent APIs
    @GET(ApiConfig.ENDPOINT_PARENT_PROFILE)
    Call<Parent> getParentProfile();
    
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
    Call<ApiResponseWrapper<List<TaskAssignmentResponse>>> getTasksByChild(@Path("childId") String childId);
    
    @GET(ApiConfig.ENDPOINT_TASK_DETAIL)
    Call<Task> getTaskDetail(@Path("id") String taskId);
    
    @POST(ApiConfig.ENDPOINT_CREATE_TASK)
    Call<Task> createTask(@Body Task task);
    
    @PUT(ApiConfig.ENDPOINT_UPDATE_TASK)
    Call<Task> updateTask(@Path("id") String taskId, @Body Task task);
    
    @DELETE(ApiConfig.ENDPOINT_DELETE_TASK)
    Call<Void> deleteTask(@Path("id") String taskId);
    
    // Activity & Report APIs
    @GET(ApiConfig.ENDPOINT_ACTIVITY_LOGS)
    Call<ApiResponseWrapper<List<ActivityLogResponse>>> getActivities(@Query("limit") int limit);
    
    @GET("activities/child/{childId}")
    Call<ApiResponseWrapper<List<ActivityLogResponse>>> getChildActivities(
            @Path("childId") String childId, 
            @Query("limit") int limit);
    
    @GET(ApiConfig.ENDPOINT_WEEKLY_PROGRESS)
    Call<WeeklyProgress> getWeeklyProgress(@Query("child_id") String childId);
    
    @GET(ApiConfig.ENDPOINT_REPORTS)
    Call<List<WeeklyProgress>> getReports(@Query("parent_id") String parentId);
    
    // AI Chat API
    @POST(ApiConfig.ENDPOINT_AI_CHAT)
    Call<AiChatResponse> sendChatMessage(@Body AiChatRequest request);
    
    // Children Search API (tìm bạn bè)
    @GET(ApiConfig.ENDPOINT_SEARCH_CHILDREN)
    Call<ApiResponseWrapper<List<ChildSearchResponse>>> searchChildren(
            @Query("currentChildId") String currentChildId,
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
    @DELETE("notifications/{id}")
    Call<ApiResponseWrapper<Void>> deleteNotification(@Path("id") String notificationId);
    
    // ==================== CHAT APIs ====================
    
    // Lấy danh sách phòng chat
    @GET("chat/rooms")
    Call<ApiResponseWrapper<List<ChatRoomDto>>> getChatRooms(@Query("userId") String userId);
    
    // Lấy phòng chat theo loại
    @GET("chat/rooms/type")
    Call<ApiResponseWrapper<List<ChatRoomDto>>> getChatRoomsByType(
            @Query("userId") String userId,
            @Query("roomType") String roomType
    );
    
    // Lấy tin nhắn trong phòng chat
    @GET("chat/rooms/{roomId}/messages")
    Call<ApiResponseWrapper<List<ChatMessageDto>>> getChatMessages(
            @Path("roomId") String roomId,
            @Query("userId") String userId,
            @Query("page") int page,
            @Query("size") int size
    );
    
    // Tạo hoặc lấy phòng chat
    @POST("chat/rooms/create")
    Call<ApiResponseWrapper<ChatRoomDto>> createOrGetChatRoom(
            @Query("user1Id") String user1Id,
            @Query("user2Id") String user2Id,
            @Query("roomType") String roomType
    );
    
    // Đánh dấu đã đọc
    @POST("chat/rooms/{roomId}/read")
    Call<ApiResponseWrapper<Void>> markMessagesAsRead(
            @Path("roomId") String roomId,
            @Query("userId") String userId
    );
    
    // Đếm tin nhắn chưa đọc
    @GET("chat/unread-count")
    Call<ApiResponseWrapper<Integer>> getUnreadCount(@Query("userId") String userId);
    
    // ==================== HABIT APIs ====================
    
    // Tạo thói quen mới
    @POST(ApiConfig.ENDPOINT_HABITS)
    Call<ApiResponseWrapper<HabitResponse>> createHabit(@Body CreateHabitRequest request);
    
    // Lấy danh sách thói quen của bé
    @GET(ApiConfig.ENDPOINT_HABITS_BY_CHILD)
    Call<ApiResponseWrapper<List<HabitResponse>>> getHabitsByChild(
            @Path("childId") String childId,
            @Query("category") String category);
    
    // Lấy chi tiết thói quen
    @GET(ApiConfig.ENDPOINT_HABIT_DETAIL)
    Call<ApiResponseWrapper<HabitResponse>> getHabitDetail(@Path("id") String habitId);
    
    // Cập nhật thói quen
    @PUT(ApiConfig.ENDPOINT_HABIT_DETAIL)
    Call<ApiResponseWrapper<HabitResponse>> updateHabit(
            @Path("id") String habitId, 
            @Body CreateHabitRequest request);
    
    // Xóa thói quen
    @DELETE(ApiConfig.ENDPOINT_HABIT_DETAIL)
    Call<ApiResponseWrapper<Void>> deleteHabit(@Path("id") String habitId);
    
    // Bé đánh dấu hoàn thành
    @POST(ApiConfig.ENDPOINT_HABIT_COMPLETE)
    Call<ApiResponseWrapper<HabitResponse>> completeHabit(
            @Path("id") String habitId,
            @Body CompleteHabitRequest request);
    
    // Parent duyệt thói quen
    @POST(ApiConfig.ENDPOINT_HABIT_APPROVE)
    Call<ApiResponseWrapper<HabitResponse>> approveHabitLog(@Path("id") String habitLogId);
    
    // Parent từ chối thói quen
    @POST(ApiConfig.ENDPOINT_HABIT_REJECT)
    Call<ApiResponseWrapper<HabitResponse>> rejectHabitLog(
            @Path("id") String habitLogId,
            @Body RejectHabitRequest request);
    
    // Lấy danh sách cần duyệt
    @GET(ApiConfig.ENDPOINT_HABIT_PENDING)
    Call<ApiResponseWrapper<List<HabitResponse>>> getPendingApprovals();
    
    // Lấy mẫu thói quen
    @GET(ApiConfig.ENDPOINT_HABIT_TEMPLATES)
    Call<ApiResponseWrapper<List<HabitResponse>>> getHabitTemplates();
    
    // ==================== QUIZ APIs ====================
    
    // Tạo bài tập trắc nghiệm
    @POST(ApiConfig.ENDPOINT_QUIZZES)
    Call<ApiResponseWrapper<QuizResponse>> createQuiz(@Body CreateQuizRequest request);
    
    // Lấy danh sách bài tập của bé
    @GET(ApiConfig.ENDPOINT_QUIZZES_BY_CHILD)
    Call<ApiResponseWrapper<List<QuizResponse>>> getQuizzesByChild(
            @Path("childId") String childId,
            @Query("status") String status);
    
    // Lấy danh sách bài tập đã giao
    @GET(ApiConfig.ENDPOINT_QUIZZES_ASSIGNED)
    Call<ApiResponseWrapper<List<QuizResponse>>> getAssignedQuizzes();
    
    // Lấy chi tiết bài tập
    @GET(ApiConfig.ENDPOINT_QUIZ_DETAIL)
    Call<ApiResponseWrapper<QuizResponse>> getQuizDetail(@Path("id") String quizId);
    
    // Xóa bài tập
    @DELETE(ApiConfig.ENDPOINT_QUIZ_DETAIL)
    Call<ApiResponseWrapper<Void>> deleteQuiz(@Path("id") String quizId);
    
    // Bắt đầu làm bài
    @POST(ApiConfig.ENDPOINT_QUIZ_START)
    Call<ApiResponseWrapper<QuizResponse>> startQuiz(@Path("id") String quizId);
    
    // Nộp bài
    @POST(ApiConfig.ENDPOINT_QUIZ_SUBMIT)
    Call<ApiResponseWrapper<QuizResponse>> submitQuiz(
            @Path("id") String quizId,
            @Body List<SubmitAnswerRequest> answers);
    
    // Kiểm tra số câu hỏi có sẵn
    @GET(ApiConfig.ENDPOINT_QUIZ_AVAILABLE_QUESTIONS)
    Call<ApiResponseWrapper<AvailableQuestionsResponse>> countAvailableQuestions(
            @Query("subject") String subject,
            @Query("level") String level);
    
    // Gợi ý phần thưởng
    @GET(ApiConfig.ENDPOINT_QUIZ_SUGGEST_REWARD)
    Call<ApiResponseWrapper<RewardSuggestionResponse>> suggestReward(
            @Query("level") String level,
            @Query("questionCount") int questionCount);
    
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
        public Integer totalPoints;
        public Integer currentStreak;
        public Integer longestStreak;
        public Integer totalExercisesCompleted;
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
    
    // Lấy danh sách môn học có sẵn
    @GET(ApiConfig.ENDPOINT_TASK_SUBJECTS)
    Call<ApiResponseWrapper<List<String>>> getAvailableSubjects();
    
    // Lấy danh sách bài tập theo môn và lớp
    @GET(ApiConfig.ENDPOINT_TASK_EXERCISES)
    Call<ApiResponseWrapper<List<ExerciseTemplateResponse>>> getExerciseTemplates(
            @Query("subject") String subject,
            @Query("gradeLevel") Integer gradeLevel);
    
    // Lấy danh sách bài tập phù hợp với bé
    @GET(ApiConfig.ENDPOINT_TASK_EXERCISES_FOR_CHILD)
    Call<ApiResponseWrapper<List<ExerciseTemplateResponse>>> getExerciseTemplatesForChild(
            @Path("childId") String childId,
            @Query("subject") String subject);
    
    // Lấy danh sách category thói quen
    @GET(ApiConfig.ENDPOINT_TASK_HABIT_CATEGORIES)
    Call<ApiResponseWrapper<List<String>>> getAvailableHabitCategories();
    
    // Lấy danh sách thói quen theo category
    @GET(ApiConfig.ENDPOINT_TASK_HABITS)
    Call<ApiResponseWrapper<List<HabitTemplateResponse>>> getHabitTemplates(
            @Query("category") String category);
    
    // Lấy danh sách thói quen phù hợp với bé
    @GET(ApiConfig.ENDPOINT_TASK_HABITS_FOR_CHILD)
    Call<ApiResponseWrapper<List<HabitTemplateResponse>>> getHabitTemplatesForChild(
            @Path("childId") String childId,
            @Query("category") String category);
    
    // Giao bài tập/thói quen cho con
    @POST(ApiConfig.ENDPOINT_TASK_ASSIGN)
    Call<ApiResponseWrapper<TaskAssignmentResponse>> assignTask(@Body AssignTaskRequest request);
    
    // Giao nhiều bài tập cùng lúc
    @POST(ApiConfig.ENDPOINT_TASK_ASSIGN_MULTIPLE)
    Call<ApiResponseWrapper<List<TaskAssignmentResponse>>> assignMultipleTasks(
            @Body List<AssignTaskRequest> requests);
    
    // Lấy chi tiết task assignment
    @GET(ApiConfig.ENDPOINT_TASK_GET)
    Call<ApiResponseWrapper<TaskAssignmentResponse>> getTaskAssignmentDetail(@Path("taskId") String taskId);
    
    // Cập nhật task
    @PUT(ApiConfig.ENDPOINT_TASK_UPDATE)
    Call<ApiResponseWrapper<TaskAssignmentResponse>> updateTaskAssignment(
            @Path("taskId") String taskId,
            @Body UpdateTaskRequest request);
    
    // Xóa task
    @DELETE(ApiConfig.ENDPOINT_TASK_DELETE)
    Call<ApiResponseWrapper<Void>> deleteTaskAssignment(@Path("taskId") String taskId);
    
    // Response classes for Task Assignment
    class ExerciseTemplateResponse {
        public String id;
        public String title;
        public String description;
        public String subject;
        public String subjectName;
        public Integer gradeLevel;
        public String topic;
        public Integer difficultyLevel;
        public Integer estimatedTimeMinutes;
        public Integer pointsReward;
        public Integer totalQuestions;
    }
    
    class HabitTemplateResponse {
        public String id;
        public String name;
        public String description;
        public String category;
        public String categoryName;
        public String iconName;
        public String colorHex;
        public String suggestedTime;
        public Integer pointsReward;
        public Integer minGrade;
        public Integer maxGrade;
    }
    
    class TaskAssignmentResponse {
        public String id;
        public String childId;
        public String assignedBy;
        public String title;
        public String description;
        public String taskType;
        public String exerciseId; // Changed from Long to String (UUID)
        public String dueDate;
        public String dueTime;
        public String status;
        public Integer pointsReward;
        public Boolean isRecurring;
        public String recurrencePattern;
        public Integer priority;
        public String createdAt;
        public String updatedAt;
    }
    
    class AssignTaskRequest {
        public String childId;
        public String taskType;
        public String subject;
        public Integer difficultyLevel;
        public String exerciseId;
        public String habitCategory;
        public String habitTemplateId;
        public String dueDate;
        public String dueTime;
        public Boolean isRecurring;
        public String recurrencePattern;
        public Integer pointsReward;
        
        public AssignTaskRequest() {}
        
        public static AssignTaskRequest createExercise(String childId, String subject, 
                Integer difficultyLevel, String dueDate) {
            AssignTaskRequest req = new AssignTaskRequest();
            req.childId = childId;
            req.taskType = "exercise";
            req.subject = subject;
            req.difficultyLevel = difficultyLevel;
            req.dueDate = dueDate;
            req.isRecurring = false;
            return req;
        }
        
        public static AssignTaskRequest createHabit(String childId, String habitCategory, 
                String dueDate, boolean isRecurring) {
            AssignTaskRequest req = new AssignTaskRequest();
            req.childId = childId;
            req.taskType = "habit";
            req.habitCategory = habitCategory;
            req.dueDate = dueDate;
            req.isRecurring = isRecurring;
            req.recurrencePattern = isRecurring ? "daily" : null;
            return req;
        }
        
        public static AssignTaskRequest createHousework(String childId, String description, 
                String dueDate, Integer pointsReward) {
            AssignTaskRequest req = new AssignTaskRequest();
            req.childId = childId;
            req.taskType = "housework";
            req.habitCategory = description;
            req.dueDate = dueDate;
            req.pointsReward = pointsReward;
            req.isRecurring = false;
            return req;
        }
    }
    
    // Request để cập nhật task
    class UpdateTaskRequest {
        public String dueDate;
        public String dueTime;
        public Integer pointsReward;
        public Boolean isRecurring;
        public String recurrencePattern;
        
        public UpdateTaskRequest() {}
        
        public UpdateTaskRequest(String dueDate, String dueTime, Integer pointsReward,
                                 Boolean isRecurring, String recurrencePattern) {
            this.dueDate = dueDate;
            this.dueTime = dueTime;
            this.pointsReward = pointsReward;
            this.isRecurring = isRecurring;
            this.recurrencePattern = recurrencePattern;
        }
    }
}

