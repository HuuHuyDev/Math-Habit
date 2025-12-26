package com.kidsapp.data.api;

import com.kidsapp.data.model.ActivityLog;
import com.kidsapp.data.model.Badge;
import com.kidsapp.data.model.Child;
import com.kidsapp.data.model.Parent;
import com.kidsapp.data.model.Task;
import com.kidsapp.data.model.WeeklyProgress;
import com.kidsapp.data.request.AiChatRequest;
import com.kidsapp.data.request.CreateTaskRequest;
import com.kidsapp.data.response.AiChatResponse;
import com.kidsapp.data.response.ChildSearchResponse;
import com.kidsapp.data.response.TaskResponse;
import com.kidsapp.data.websocket.ChatMessageDto;
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
     * GET /exercises?childId={childId}
     */
    @GET("exercises")
    Call<ApiResponseWrapper<List<com.kidsapp.data.model.ExerciseContent>>> getAllExercises(@Query("childId") String childId);
    
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
    
    @GET(ApiConfig.ENDPOINT_CHILDREN)
    Call<List<Child>> getChildren();
    
    @GET(ApiConfig.ENDPOINT_CHILDREN)
    Call<ApiResponseWrapper<List<ChildResponse>>> getParentChildren();
    
    @GET(ApiConfig.ENDPOINT_CHILD_DETAIL)
    Call<Child> getChildDetail(@Path("id") String childId);
    
    // Task APIs
    @GET(ApiConfig.ENDPOINT_TASKS)
    Call<List<Task>> getTasks(@Query("child_id") String childId);
    
    @GET(ApiConfig.ENDPOINT_TASKS_BY_CHILD)
    Call<ApiResponseWrapper<List<Task>>> getTasksByChild(@Path("childId") String childId);
    
    @GET(ApiConfig.ENDPOINT_TASKS_BY_STATUS)
    Call<ApiResponseWrapper<List<Task>>> getTasksByStatus(
            @Path("childId") String childId,
            @Query("status") String status
    );
    
    @GET(ApiConfig.ENDPOINT_TASKS_BY_TYPE)
    Call<ApiResponseWrapper<List<Task>>> getTasksByType(
            @Path("childId") String childId,
            @Query("type") String type
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
    
    @POST(ApiConfig.ENDPOINT_TASK_COMPLETE)
    Call<ApiResponseWrapper<Task>> completeTask(@Path("id") String taskId);
    
    @POST(ApiConfig.ENDPOINT_TASKS)
    Call<ApiResponseWrapper<TaskResponse>> createTask(@Body CreateTaskRequest request);
    
    @POST(ApiConfig.ENDPOINT_CREATE_TASK)
    Call<Task> createTask(@Body Task task);
    
    @PUT(ApiConfig.ENDPOINT_UPDATE_TASK)
    Call<Task> updateTask(@Path("id") String taskId, @Body Task task);
    
    @DELETE(ApiConfig.ENDPOINT_DELETE_TASK)
    Call<Void> deleteTask(@Path("id") String taskId);
    
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
    
    // ==================== FILE UPLOAD APIs ====================
    
    /**
     * Upload ảnh minh chứng
     * POST /upload/image
     */
    @Multipart
    @POST(ApiConfig.ENDPOINT_UPLOAD_IMAGE)
    Call<ApiResponseWrapper<UploadResponse>> uploadImage(@Part MultipartBody.Part file);
    
    /**
     * Upload video minh chứng
     * POST /upload/video
     */
    @Multipart
    @POST(ApiConfig.ENDPOINT_UPLOAD_VIDEO)
    Call<ApiResponseWrapper<UploadResponse>> uploadVideo(@Part MultipartBody.Part file);
    
    // Upload response
    class UploadResponse {
        public String url;
        public String filename;
        public String type; // IMAGE or VIDEO
    }
    
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
    Call<List<ActivityLog>> getActivityLogs(@Query("parent_id") String parentId);
    
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
        public int age;
        public Integer grade;
        public Integer level;
        public Integer totalPoints;
        public Integer currentStreak;
        public boolean isOnline;
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
}

