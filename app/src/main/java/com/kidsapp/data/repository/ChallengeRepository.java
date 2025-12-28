package com.kidsapp.data.repository;

import android.content.Context;

import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.Child;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository xử lý các API liên quan đến Challenge/Battle
 */
public class ChallengeRepository {
    
    private final ApiService apiService;
    private final SharedPref sharedPref;

    public ChallengeRepository(Context context) {
        this.sharedPref = new SharedPref(context);
        this.apiService = RetrofitClient.getInstance(sharedPref).getApiService();
        
        // Debug: Check token and childId
        String token = sharedPref.getAuthToken();
        String childId = sharedPref.getChildId();
        boolean isLoggedIn = sharedPref.isLoggedIn();
        
        android.util.Log.d("ChallengeRepository", "=== INIT ===");
        android.util.Log.d("ChallengeRepository", "Token: " + (token != null ? "EXISTS (length=" + token.length() + ")" : "NULL"));
        android.util.Log.d("ChallengeRepository", "ChildId: " + (childId != null ? childId : "NULL"));
        android.util.Log.d("ChallengeRepository", "IsLoggedIn: " + isLoggedIn);
        
        // Show on screen if possible
        if (token == null) {
            android.util.Log.e("ChallengeRepository", "WARNING: Token is NULL!");
        }
        if (childId == null) {
            android.util.Log.e("ChallengeRepository", "WARNING: ChildId is NULL!");
        }
    }

    /**
     * Lấy danh sách Category cho Challenge
     */
    public void getChallengeCategories(ResultCallback<List<com.kidsapp.data.model.Category>> callback) {
        android.util.Log.d("ChallengeRepository", "=== getChallengeCategories START ===");
        android.util.Log.d("ChallengeRepository", "Token: " + (sharedPref.getAuthToken() != null ? "EXISTS" : "NULL"));
        android.util.Log.d("ChallengeRepository", "Calling API with type=CHALLENGE");
        
        apiService.getChallengeCategories("CHALLENGE").enqueue(new Callback<ApiService.ApiResponseWrapper<List<com.kidsapp.data.model.Category>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<com.kidsapp.data.model.Category>>> call, 
                                 Response<ApiService.ApiResponseWrapper<List<com.kidsapp.data.model.Category>>> response) {
                android.util.Log.d("ChallengeRepository", "Response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("ChallengeRepository", "Response body: " + response.body());
                    
                    if (response.body().data != null) {
                        android.util.Log.d("ChallengeRepository", "Categories count: " + response.body().data.size());
                        callback.onSuccess(response.body().data);
                    } else {
                        android.util.Log.e("ChallengeRepository", "Response data is null");
                        callback.onError("Không có dữ liệu chủ đề");
                    }
                } else {
                    String errorMsg = "HTTP " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += ": " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        android.util.Log.e("ChallengeRepository", "Error reading error body", e);
                    }
                    android.util.Log.e("ChallengeRepository", "API Error: " + errorMsg);
                    callback.onError("Không thể tải danh sách chủ đề");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<com.kidsapp.data.model.Category>>> call, Throwable t) {
                android.util.Log.e("ChallengeRepository", "API call failed", t);
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
    }
    
    /**
     * Tham gia hàng đợi thách đấu nhanh
     */
    public void joinQuickMatch(String childId, String categoryId, int difficultyLevel, 
                              ResultCallback<com.kidsapp.data.response.MatchFoundResponse> callback) {
        android.util.Log.d("ChallengeRepository", "=== joinQuickMatch START ===");
        android.util.Log.d("ChallengeRepository", "Token: " + (sharedPref.getAuthToken() != null ? "EXISTS" : "NULL"));
        android.util.Log.d("ChallengeRepository", "ChildId: " + childId);
        android.util.Log.d("ChallengeRepository", "CategoryId: " + categoryId);
        android.util.Log.d("ChallengeRepository", "DifficultyLevel: " + difficultyLevel);
        
        com.kidsapp.data.request.JoinQueueRequest request = new com.kidsapp.data.request.JoinQueueRequest();
        request.setChildId(childId);
        request.setCategoryId(categoryId);
        request.setDifficultyLevel(difficultyLevel);
        
        apiService.joinQuickMatch(request).enqueue(new Callback<ApiService.ApiResponseWrapper<com.kidsapp.data.response.MatchFoundResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.MatchFoundResponse>> call, 
                                 Response<ApiService.ApiResponseWrapper<com.kidsapp.data.response.MatchFoundResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể tham gia hàng đợi");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.MatchFoundResponse>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
    }
    
    /**
     * Lấy trạng thái hàng đợi (polling)
     */
    public void getQueueStatus(String childId, ResultCallback<com.kidsapp.data.response.QueueResponse> callback) {
        apiService.getQueueStatus(childId).enqueue(new Callback<ApiService.ApiResponseWrapper<com.kidsapp.data.response.QueueResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.QueueResponse>> call, 
                                 Response<ApiService.ApiResponseWrapper<com.kidsapp.data.response.QueueResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể lấy trạng thái hàng đợi");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.QueueResponse>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
    }
    
    /**
     * Rời khỏi hàng đợi
     */
    public void leaveQueue(String childId, ResultCallback<Void> callback) {
        apiService.leaveQueue(childId).enqueue(new Callback<ApiService.ApiResponseWrapper<String>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<String>> call, 
                                 Response<ApiService.ApiResponseWrapper<String>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Không thể rời khỏi hàng đợi");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<String>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
    }

    /**
     * Lấy danh sách bạn bè có thể mời thách đấu
     */
    public void getFriendsList(ResultCallback<List<Child>> callback) {
        String currentChildId = sharedPref.getChildId();
        
        android.util.Log.d("ChallengeRepository", "=== getFriendsList START ===");
        android.util.Log.d("ChallengeRepository", "Token: " + (sharedPref.getAuthToken() != null ? "EXISTS" : "NULL"));
        android.util.Log.d("ChallengeRepository", "ChildId: " + currentChildId);
        
        if (currentChildId == null || currentChildId.isEmpty()) {
            android.util.Log.e("ChallengeRepository", "ChildId is NULL - User not logged in!");
            callback.onError("Chưa đăng nhập. Vui lòng đăng nhập lại.");
            return;
        }
        
        android.util.Log.d("ChallengeRepository", "Calling API getFriends with childId: " + currentChildId);
        
        Call<ApiService.ApiResponseWrapper<List<com.kidsapp.data.response.FriendResponse>>> call = 
            apiService.getFriends(currentChildId);
        
        android.util.Log.d("ChallengeRepository", "Call created: " + call.request().url());
        
        call.enqueue(new Callback<ApiService.ApiResponseWrapper<List<com.kidsapp.data.response.FriendResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<com.kidsapp.data.response.FriendResponse>>> call, 
                                 Response<ApiService.ApiResponseWrapper<List<com.kidsapp.data.response.FriendResponse>>> response) {
                android.util.Log.d("ChallengeRepository", "=== getFriends RESPONSE RECEIVED ===");
                android.util.Log.d("ChallengeRepository", "Response code: " + response.code());
                android.util.Log.d("ChallengeRepository", "Response message: " + response.message());
                android.util.Log.d("ChallengeRepository", "Response body: " + (response.body() != null ? "NOT NULL" : "NULL"));
                
                if (response.isSuccessful()) {
                    android.util.Log.d("ChallengeRepository", "Response is successful");
                    
                    try {
                        if (response.body() != null) {
                            android.util.Log.d("ChallengeRepository", "Response body exists");
                            android.util.Log.d("ChallengeRepository", "Response success: " + response.body().success);
                            android.util.Log.d("ChallengeRepository", "Response message: " + response.body().message);
                            android.util.Log.d("ChallengeRepository", "Response data: " + (response.body().data != null ? "NOT NULL" : "NULL"));
                            
                            if (response.body().data != null) {
                                android.util.Log.d("ChallengeRepository", "Friends count: " + response.body().data.size());
                                
                                try {
                                    // Convert FriendResponse to Child model
                                    List<Child> friends = new ArrayList<>();
                                    for (com.kidsapp.data.response.FriendResponse friend : response.body().data) {
                                        android.util.Log.d("ChallengeRepository", "Processing friend:");
                                        android.util.Log.d("ChallengeRepository", "  - childId: " + friend.getChildId());
                                        android.util.Log.d("ChallengeRepository", "  - name: " + friend.getName());
                                        android.util.Log.d("ChallengeRepository", "  - nickname: " + friend.getNickname());
                                        android.util.Log.d("ChallengeRepository", "  - level: " + friend.getLevel());
                                        android.util.Log.d("ChallengeRepository", "  - getId(): " + friend.getId());
                                        android.util.Log.d("ChallengeRepository", "  - getCurrentLevel(): " + friend.getCurrentLevel());
                                        
                                        Child child = new Child();
                                        child.setId(friend.getId());
                                        child.setNickname(friend.getNickname());
                                        
                                        // Handle Integer to int conversion safely
                                        Integer friendLevel = friend.getCurrentLevel();
                                        child.setCurrentLevel(friendLevel != null ? friendLevel : 1);
                                        
                                        friends.add(child);
                                        
                                        android.util.Log.d("ChallengeRepository", "Child created: " + child.getNickname());
                                    }
                                    
                                    android.util.Log.d("ChallengeRepository", "Calling callback.onSuccess with " + friends.size() + " friends");
                                    callback.onSuccess(friends);
                                    android.util.Log.d("ChallengeRepository", "callback.onSuccess called");
                                    
                                } catch (Exception e) {
                                    android.util.Log.e("ChallengeRepository", "Error processing friends data", e);
                                    callback.onError("Lỗi xử lý dữ liệu bạn bè: " + e.getMessage());
                                }
                            } else {
                                android.util.Log.e("ChallengeRepository", "Response data is NULL");
                                callback.onError("Không có dữ liệu bạn bè");
                            }
                        } else {
                            android.util.Log.e("ChallengeRepository", "Response body is NULL");
                            callback.onError("Response body is NULL");
                        }
                    } catch (Exception e) {
                        android.util.Log.e("ChallengeRepository", "Error parsing response", e);
                        callback.onError("Lỗi phân tích dữ liệu: " + e.getMessage());
                    }
                } else {
                    String errorMsg = "HTTP " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMsg += ": " + errorBody;
                            android.util.Log.e("ChallengeRepository", "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("ChallengeRepository", "Error reading error body", e);
                    }
                    android.util.Log.e("ChallengeRepository", "API Error: " + errorMsg);
                    callback.onError("Không thể tải danh sách bạn bè. " + errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<com.kidsapp.data.response.FriendResponse>>> call, Throwable t) {
                android.util.Log.e("ChallengeRepository", "=== getFriends FAILURE ===");
                android.util.Log.e("ChallengeRepository", "Error type: " + t.getClass().getName());
                android.util.Log.e("ChallengeRepository", "Error message: " + t.getMessage(), t);
                callback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
        
        android.util.Log.d("ChallengeRepository", "API call enqueued");
    }

    /**
     * Tạo Challenge và gửi lời mời
     */
    public void createChallengeAndInvite(String friendId, String categoryId, String categoryName, 
                                        int difficultyLevel, ResultCallback<String> callback) {
        String childId = sharedPref.getChildId();
        
        android.util.Log.d("ChallengeRepository", "=== createChallengeAndInvite START ===");
        android.util.Log.d("ChallengeRepository", "Token: " + (sharedPref.getAuthToken() != null ? "EXISTS" : "NULL"));
        android.util.Log.d("ChallengeRepository", "childId: " + childId);
        android.util.Log.d("ChallengeRepository", "friendId: " + friendId);
        android.util.Log.d("ChallengeRepository", "categoryId: " + categoryId);
        android.util.Log.d("ChallengeRepository", "categoryName: " + categoryName);
        android.util.Log.d("ChallengeRepository", "difficultyLevel: " + difficultyLevel);
        
        if (childId == null || childId.isEmpty()) {
            android.util.Log.e("ChallengeRepository", "childId is NULL!");
            callback.onError("Chưa đăng nhập");
            return;
        }
        
        // 1. Tạo Challenge
        com.kidsapp.data.request.CreateChallengeRequest createRequest = new com.kidsapp.data.request.CreateChallengeRequest();
        createRequest.setChildId(childId);
        createRequest.setCategoryId(categoryId);
        createRequest.setTitle("Thách đấu - " + categoryName);
        createRequest.setDescription("Thách đấu với độ khó " + difficultyLevel + " sao");
        createRequest.setDifficultyLevel(difficultyLevel);
        createRequest.setTimeLimitMinutes(10);
        createRequest.setTotalQuestions(10);
        
        android.util.Log.d("ChallengeRepository", "Calling createChallenge API...");
        android.util.Log.d("ChallengeRepository", "Request: " + createRequest.toString());
        
        apiService.createChallenge(createRequest).enqueue(new Callback<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>> call,
                                 Response<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>> response) {
                android.util.Log.d("ChallengeRepository", "=== createChallenge RESPONSE ===");
                android.util.Log.d("ChallengeRepository", "Response code: " + response.code());
                android.util.Log.d("ChallengeRepository", "Response message: " + response.message());
                
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                        String challengeId = response.body().data.getId();
                        android.util.Log.d("ChallengeRepository", "Challenge created successfully! ID: " + challengeId);
                        
                        // 2. Gửi lời mời
                        com.kidsapp.data.request.InviteChildRequest inviteRequest = new com.kidsapp.data.request.InviteChildRequest(friendId);
                        
                        android.util.Log.d("ChallengeRepository", "Calling inviteChild API...");
                        android.util.Log.d("ChallengeRepository", "ChallengeId: " + challengeId);
                        android.util.Log.d("ChallengeRepository", "CreatorId: " + childId);
                        android.util.Log.d("ChallengeRepository", "InvitedChildId: " + friendId);
                        
                        apiService.inviteChild(challengeId, childId, inviteRequest).enqueue(new Callback<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeInviteResponse>>() {
                            @Override
                            public void onResponse(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeInviteResponse>> call,
                                                 Response<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeInviteResponse>> response) {
                                android.util.Log.d("ChallengeRepository", "=== inviteChild RESPONSE ===");
                                android.util.Log.d("ChallengeRepository", "Response code: " + response.code());
                                
                                if (response.isSuccessful() && response.body() != null) {
                                    android.util.Log.d("ChallengeRepository", "Invite sent successfully!");
                                    callback.onSuccess(challengeId);
                                } else {
                                    String errorMsg = "HTTP " + response.code();
                                    try {
                                        if (response.errorBody() != null) {
                                            String errorBody = response.errorBody().string();
                                            errorMsg += ": " + errorBody;
                                            android.util.Log.e("ChallengeRepository", "inviteChild error body: " + errorBody);
                                        }
                                    } catch (Exception e) {
                                        android.util.Log.e("ChallengeRepository", "Error reading inviteChild error body", e);
                                    }
                                    android.util.Log.e("ChallengeRepository", "inviteChild failed: " + errorMsg);
                                    callback.onError("Không thể gửi lời mời: " + errorMsg);
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeInviteResponse>> call, Throwable t) {
                                android.util.Log.e("ChallengeRepository", "inviteChild API call failed", t);
                                callback.onError("Lỗi kết nối khi gửi lời mời: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
                            }
                        });
                    } else {
                        String errorMsg = "HTTP " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                errorMsg += ": " + errorBody;
                                android.util.Log.e("ChallengeRepository", "createChallenge error body: " + errorBody);
                            }
                        } catch (Exception e) {
                            android.util.Log.e("ChallengeRepository", "Error reading createChallenge error body", e);
                        }
                        android.util.Log.e("ChallengeRepository", "createChallenge failed: " + errorMsg);
                        callback.onError("Không thể tạo thách đấu: " + errorMsg);
                    }
                } catch (Exception e) {
                    android.util.Log.e("ChallengeRepository", "Exception processing createChallenge response", e);
                    callback.onError("Lỗi xử lý phản hồi: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>> call, Throwable t) {
                android.util.Log.e("ChallengeRepository", "createChallenge API call failed", t);
                callback.onError("Lỗi kết nối khi tạo thách đấu: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }

    /**
     * Hủy lời mời thách đấu
     */
    public void cancelChallengeInvite(String inviteId, ResultCallback<Void> callback) {
        // TODO: Implement API call
        callback.onSuccess(null);
    }

    /**
     * Kiểm tra trạng thái lời mời
     */
    public void checkInviteStatus(String inviteId, ResultCallback<InviteStatus> callback) {
        // TODO: Implement API call
        callback.onSuccess(InviteStatus.PENDING);
    }

    /**
     * Callback interface cho các API calls
     */
    public interface ResultCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    /**
     * Trạng thái lời mời
     */
    public enum InviteStatus {
        PENDING,
        ACCEPTED,
        DECLINED,
        EXPIRED
    }
}
