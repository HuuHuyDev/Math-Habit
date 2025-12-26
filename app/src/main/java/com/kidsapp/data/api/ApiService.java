package com.kidsapp.data.api;

import com.kidsapp.data.model.ActivityLog;
import com.kidsapp.data.model.Badge;
import com.kidsapp.data.model.Child;
import com.kidsapp.data.model.Parent;
import com.kidsapp.data.model.Task;
import com.kidsapp.data.model.WeeklyProgress;
import com.kidsapp.data.request.AiChatRequest;
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
        
        public SocialLoginRequest(String accessToken) {
            this.accessToken = accessToken;
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
}

