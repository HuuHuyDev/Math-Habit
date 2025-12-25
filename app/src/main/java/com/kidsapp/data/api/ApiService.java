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
    Call<AuthResponse> login(@Body LoginRequest request);
    
    @POST(ApiConfig.ENDPOINT_REGISTER)
    Call<AuthResponse> register(@Body RegisterRequest request);
    
    @POST(ApiConfig.ENDPOINT_FORGOT_PASSWORD)
    Call<Void> forgotPassword(@Body ForgotPasswordRequest request);
    
    // Parent APIs
    @GET(ApiConfig.ENDPOINT_PARENT_PROFILE)
    Call<Parent> getParentProfile();
    
    @GET(ApiConfig.ENDPOINT_CHILDREN)
    Call<List<Child>> getChildren();
    
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
        public int code;
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
        public String name;
        public String email;
        public String password;
        public String role;
        
        public RegisterRequest(String name, String email, String password, String role) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.role = role;
        }
    }
    
    class ForgotPasswordRequest {
        public String email;
        
        public ForgotPasswordRequest(String email) {
            this.email = email;
        }
    }
    
    class AuthResponse {
        public String token;
        public String userId;
        public String role;
        public Parent parent;
        public Child child;
    }
}

