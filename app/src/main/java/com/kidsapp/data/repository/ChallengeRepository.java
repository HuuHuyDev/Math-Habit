package com.kidsapp.data.repository;

import android.content.Context;

import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.Child;
import com.kidsapp.data.response.ChallengeResultResponse;

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
     * @deprecated Sử dụng createChallenge và inviteChild riêng biệt
     */
    @Deprecated
    public void createChallengeAndInvite(String friendId, String categoryId, String categoryName, 
                                        int difficultyLevel, ResultCallback<String> callback) {
        // Chuyển sang sử dụng method mới
        createChallenge(categoryId, categoryName, new ResultCallback<String>() {
            @Override
            public void onSuccess(String challengeId) {
                inviteChild(challengeId, friendId, callback);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    /**
     * Tạo Challenge mới (Bước 1)
     */
    public void createChallenge(String categoryId, String categoryName, 
                               ResultCallback<String> callback) {
        createChallenge(categoryId, categoryName, callback, new ResultCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // This won't be called
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    private void createChallenge(String categoryId, String categoryName,
                                ResultCallback<String> successCallback, 
                                ResultCallback<String> errorCallback) {
        String childId = sharedPref.getChildId();
        
        android.util.Log.d("ChallengeRepository", "=== createChallenge START ===");
        android.util.Log.d("ChallengeRepository", "childId: " + childId);
        android.util.Log.d("ChallengeRepository", "categoryId: " + categoryId);
        android.util.Log.d("ChallengeRepository", "categoryName: " + categoryName);
        
        if (childId == null || childId.isEmpty()) {
            errorCallback.onError("Chưa đăng nhập");
            return;
        }
        
        com.kidsapp.data.request.CreateChallengeRequest request = new com.kidsapp.data.request.CreateChallengeRequest();
        request.setChildId(childId);
        request.setCategoryId(categoryId);
        request.setTitle("Thách đấu - " + categoryName);
        request.setDescription("Thách đấu câu đố mẹo với anh chị em");
        request.setDifficultyLevel(1); // Mặc định = 1 cho câu đố mẹo
        request.setTimeLimitMinutes(10);
        request.setTotalQuestions(10);
        
        apiService.createChallenge(request).enqueue(new Callback<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>> call,
                                 Response<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    String challengeId = response.body().data.getId();
                    android.util.Log.d("ChallengeRepository", "Challenge created: " + challengeId);
                    successCallback.onSuccess(challengeId);
                } else {
                    String errorMsg = "HTTP " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += ": " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        android.util.Log.e("ChallengeRepository", "Error reading error body", e);
                    }
                    errorCallback.onError("Không thể tạo thách đấu: " + errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>> call, Throwable t) {
                errorCallback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }
    
    /**
     * Mời child tham gia challenge (Bước 2)
     */
    public void inviteChild(String challengeId, String friendId, ResultCallback<String> callback) {
        String creatorId = sharedPref.getChildId();
        
        android.util.Log.d("ChallengeRepository", "=== inviteChild START ===");
        android.util.Log.d("ChallengeRepository", "challengeId: " + challengeId);
        android.util.Log.d("ChallengeRepository", "creatorId: " + creatorId);
        android.util.Log.d("ChallengeRepository", "friendId: " + friendId);
        
        if (creatorId == null || creatorId.isEmpty()) {
            callback.onError("Chưa đăng nhập");
            return;
        }
        
        com.kidsapp.data.request.InviteChildRequest request = new com.kidsapp.data.request.InviteChildRequest(friendId);
        
        apiService.inviteChild(challengeId, creatorId, request).enqueue(new Callback<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeInviteResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeInviteResponse>> call,
                                 Response<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeInviteResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("ChallengeRepository", "Invite sent successfully");
                    callback.onSuccess("Đã gửi lời mời thành công");
                } else {
                    String errorMsg = "HTTP " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += ": " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        android.util.Log.e("ChallengeRepository", "Error reading error body", e);
                    }
                    callback.onError("Không thể gửi lời mời: " + errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeInviteResponse>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }
    
    /**
     * Lấy danh sách lời mời của tôi
     */
    public void getMyInvites(ResultCallback<List<com.kidsapp.data.response.ChallengeInviteResponse>> callback) {
        String childId = sharedPref.getChildId();
        
        if (childId == null || childId.isEmpty()) {
            callback.onError("Chưa đăng nhập");
            return;
        }
        
        apiService.getMyInvites(childId).enqueue(new Callback<ApiService.ApiResponseWrapper<List<com.kidsapp.data.response.ChallengeInviteResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<com.kidsapp.data.response.ChallengeInviteResponse>>> call,
                                 Response<ApiService.ApiResponseWrapper<List<com.kidsapp.data.response.ChallengeInviteResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể tải danh sách lời mời");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<com.kidsapp.data.response.ChallengeInviteResponse>>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }
    
    /**
     * Chấp nhận lời mời (Bước 3a)
     */
    public void acceptInvite(String inviteId, ResultCallback<com.kidsapp.data.response.ChallengeResponse> callback) {
        String childId = sharedPref.getChildId();
        
        android.util.Log.d("ChallengeRepository", "=== acceptInvite START ===");
        android.util.Log.d("ChallengeRepository", "inviteId: " + inviteId);
        android.util.Log.d("ChallengeRepository", "childId: " + childId);
        
        if (childId == null || childId.isEmpty()) {
            android.util.Log.e("ChallengeRepository", "ChildId is null or empty");
            callback.onError("Chưa đăng nhập");
            return;
        }
        
        if (inviteId == null || inviteId.isEmpty()) {
            android.util.Log.e("ChallengeRepository", "InviteId is null or empty");
            callback.onError("ID lời mời không hợp lệ");
            return;
        }
        
        apiService.acceptInvite(inviteId, childId).enqueue(new Callback<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>> call,
                                 Response<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>> response) {
                
                android.util.Log.d("ChallengeRepository", "=== acceptInvite RESPONSE ===");
                android.util.Log.d("ChallengeRepository", "Response code: " + response.code());
                android.util.Log.d("ChallengeRepository", "Response successful: " + response.isSuccessful());
                
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("ChallengeRepository", "Response body: " + response.body());
                    android.util.Log.d("ChallengeRepository", "Response data: " + response.body().data);
                    
                    if (response.body().data != null) {
                        android.util.Log.d("ChallengeRepository", "SUCCESS - Challenge data received");
                        callback.onSuccess(response.body().data);
                    } else {
                        android.util.Log.e("ChallengeRepository", "Response data is null");
                        callback.onError("Dữ liệu phản hồi không hợp lệ");
                    }
                } else {
                    String errorMsg = "Không thể chấp nhận lời mời";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            android.util.Log.e("ChallengeRepository", "Error body: " + errorBody);
                            errorMsg = "Lỗi " + response.code() + ": " + errorBody;
                        }
                    } catch (Exception e) {
                        android.util.Log.e("ChallengeRepository", "Error reading error body", e);
                    }
                    android.util.Log.e("ChallengeRepository", "ERROR: " + errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>> call, Throwable t) {
                android.util.Log.e("ChallengeRepository", "=== acceptInvite FAILURE ===");
                android.util.Log.e("ChallengeRepository", "Error: " + t.getMessage(), t);
                callback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }
    
    /**
     * Lấy câu hỏi cho thách đấu
     */
    public void getChallengeQuestions(String challengeId, ResultCallback<List<com.kidsapp.data.response.QuestionResponse>> callback) {
        android.util.Log.d("ChallengeRepository", "=== getChallengeQuestions START ===");
        android.util.Log.d("ChallengeRepository", "challengeId: " + challengeId);
        
        apiService.getChallengeQuestions(challengeId).enqueue(new Callback<ApiService.ApiResponseWrapper<List<com.kidsapp.data.response.QuestionResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<com.kidsapp.data.response.QuestionResponse>>> call,
                                 Response<ApiService.ApiResponseWrapper<List<com.kidsapp.data.response.QuestionResponse>>> response) {
                
                android.util.Log.d("ChallengeRepository", "=== getChallengeQuestions RESPONSE ===");
                android.util.Log.d("ChallengeRepository", "Response code: " + response.code());
                android.util.Log.d("ChallengeRepository", "Response successful: " + response.isSuccessful());
                
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    List<com.kidsapp.data.response.QuestionResponse> questions = response.body().data;
                    android.util.Log.d("ChallengeRepository", "SUCCESS - Received " + questions.size() + " questions");
                    
                    // Log first question for debugging
                    if (!questions.isEmpty()) {
                        com.kidsapp.data.response.QuestionResponse firstQ = questions.get(0);
                        android.util.Log.d("ChallengeRepository", "First question: " + firstQ.getQuestionText());
                        android.util.Log.d("ChallengeRepository", "Options count: " + (firstQ.getOptions() != null ? firstQ.getOptions().size() : 0));
                        android.util.Log.d("ChallengeRepository", "Category: " + firstQ.getCategoryName());
                        android.util.Log.d("ChallengeRepository", "Time limit: " + firstQ.getTimeLimit());
                    }
                    
                    callback.onSuccess(questions);
                } else {
                    String errorMsg = "Không thể tải câu hỏi";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            android.util.Log.e("ChallengeRepository", "Error body: " + errorBody);
                            errorMsg = "Lỗi " + response.code() + ": " + errorBody;
                        }
                    } catch (Exception e) {
                        android.util.Log.e("ChallengeRepository", "Error reading error body", e);
                    }
                    android.util.Log.e("ChallengeRepository", "ERROR: " + errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<com.kidsapp.data.response.QuestionResponse>>> call, Throwable t) {
                android.util.Log.e("ChallengeRepository", "=== getChallengeQuestions FAILURE ===");
                android.util.Log.e("ChallengeRepository", "Error: " + t.getMessage(), t);
                callback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }
    
    /**
     * Gửi câu trả lời
     */
    public void submitAnswer(String challengeId, String questionId, int selectedAnswer, long timeSpent, 
                           ResultCallback<String> callback) {
        String childId = sharedPref.getChildId();
        
        android.util.Log.d("ChallengeRepository", "=== submitAnswer START ===");
        android.util.Log.d("ChallengeRepository", "challengeId: " + challengeId);
        android.util.Log.d("ChallengeRepository", "questionId: " + questionId);
        android.util.Log.d("ChallengeRepository", "selectedAnswer: " + selectedAnswer);
        android.util.Log.d("ChallengeRepository", "timeSpent: " + timeSpent + "ms");
        android.util.Log.d("ChallengeRepository", "childId: " + childId);
        
        if (childId == null || childId.isEmpty()) {
            android.util.Log.e("ChallengeRepository", "ChildId is null or empty");
            callback.onError("Chưa đăng nhập");
            return;
        }
        
        com.kidsapp.data.request.SubmitAnswerRequest request = new com.kidsapp.data.request.SubmitAnswerRequest(
                childId, questionId, selectedAnswer, timeSpent);
        
        apiService.submitAnswer(challengeId, request).enqueue(new Callback<ApiService.ApiResponseWrapper<String>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<String>> call,
                                 Response<ApiService.ApiResponseWrapper<String>> response) {
                
                android.util.Log.d("ChallengeRepository", "=== submitAnswer RESPONSE ===");
                android.util.Log.d("ChallengeRepository", "Response code: " + response.code());
                android.util.Log.d("ChallengeRepository", "Response successful: " + response.isSuccessful());
                
                if (response.isSuccessful() && response.body() != null) {
                    String result = response.body().data != null ? response.body().data : "OK";
                    android.util.Log.d("ChallengeRepository", "SUCCESS - Answer submitted: " + result);
                    callback.onSuccess(result);
                } else {
                    String errorMsg = "Không thể gửi câu trả lời";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            android.util.Log.e("ChallengeRepository", "Error body: " + errorBody);
                            errorMsg = "Lỗi " + response.code() + ": " + errorBody;
                        }
                    } catch (Exception e) {
                        android.util.Log.e("ChallengeRepository", "Error reading error body", e);
                    }
                    android.util.Log.e("ChallengeRepository", "ERROR: " + errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<String>> call, Throwable t) {
                android.util.Log.e("ChallengeRepository", "=== submitAnswer FAILURE ===");
                android.util.Log.e("ChallengeRepository", "Error: " + t.getMessage(), t);
                callback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }/**
     * Từ chối lời mời (Bước 3b)
     */
    public void declineInvite(String inviteId, ResultCallback<String> callback) {
        String childId = sharedPref.getChildId();
        
        if (childId == null || childId.isEmpty()) {
            callback.onError("Chưa đăng nhập");
            return;
        }
        
        apiService.declineInvite(inviteId, childId).enqueue(new Callback<ApiService.ApiResponseWrapper<String>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<String>> call,
                                 Response<ApiService.ApiResponseWrapper<String>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Đã từ chối lời mời");
                } else {
                    callback.onError("Không thể từ chối lời mời");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<String>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }
    
    /**
     * Lấy danh sách challenge đang active
     */
    public void getActiveChallenges(ResultCallback<List<com.kidsapp.data.response.ChallengeResponse>> callback) {
        String childId = sharedPref.getChildId();
        
        if (childId == null || childId.isEmpty()) {
            callback.onError("Chưa đăng nhập");
            return;
        }
        
        apiService.getActiveChallenges(childId).enqueue(new Callback<ApiService.ApiResponseWrapper<List<com.kidsapp.data.response.ChallengeResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<com.kidsapp.data.response.ChallengeResponse>>> call,
                                 Response<ApiService.ApiResponseWrapper<List<com.kidsapp.data.response.ChallengeResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể tải danh sách thách đấu");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<com.kidsapp.data.response.ChallengeResponse>>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }
    
    /**
     * Tham gia challenge đang active (Bước 5)
     */
    public void joinActiveChallenge(String challengeId, ResultCallback<com.kidsapp.data.response.ChallengeResponse> callback) {
        String childId = sharedPref.getChildId();
        
        if (childId == null || childId.isEmpty()) {
            callback.onError("Chưa đăng nhập");
            return;
        }
        
        apiService.joinActiveChallenge(challengeId, childId).enqueue(new Callback<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>> call,
                                 Response<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể tham gia thách đấu");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
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
     * Hủy challenge (chỉ creator)
     */
    public void cancelChallenge(String challengeId, ResultCallback<String> callback) {
        String creatorId = sharedPref.getChildId();
        
        if (creatorId == null || creatorId.isEmpty()) {
            callback.onError("Chưa đăng nhập");
            return;
        }
        
        apiService.cancelChallenge(challengeId, creatorId).enqueue(new Callback<ApiService.ApiResponseWrapper<String>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<String>> call,
                                 Response<ApiService.ApiResponseWrapper<String>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Đã hủy thách đấu");
                } else {
                    callback.onError("Không thể hủy thách đấu");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<String>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }
    
    /**
     * Lấy chi tiết lời mời
     */
    public void getInviteDetail(String inviteId, ResultCallback<com.kidsapp.data.response.ChallengeInviteResponse> callback) {
        apiService.getInviteDetail(inviteId).enqueue(new Callback<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeInviteResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeInviteResponse>> call,
                                 Response<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeInviteResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể tải chi tiết lời mời");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeInviteResponse>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }
    
    /**
     * Lấy thông tin challenge để tham gia
     */
    public void getChallengeForParticipant(String challengeId, ResultCallback<com.kidsapp.data.response.ChallengeResponse> callback) {
        String childId = sharedPref.getChildId();
        
        if (childId == null || childId.isEmpty()) {
            callback.onError("Chưa đăng nhập");
            return;
        }
        
        apiService.getChallengeForParticipant(challengeId, childId).enqueue(new Callback<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>> call,
                                 Response<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể tải thông tin thách đấu");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeResponse>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }

    /**
     * Kiểm tra trạng thái lời mời
     */
    public void checkInviteStatus(String inviteId, ResultCallback<InviteStatus> callback) {
        // TODO: Implement API call
        callback.onSuccess(InviteStatus.PENDING);
    }

    /**
     * Lấy kết quả thách đấu
     */
    public void getChallengeResult(String challengeId, String childId, ResultCallback<ChallengeResultResponse> callback) {
        android.util.Log.d("ChallengeRepository", "Getting challenge result for challengeId: " + challengeId + ", childId: " + childId);
        
        apiService.getChallengeResult(challengeId, childId).enqueue(new Callback<ApiService.ApiResponseWrapper<ChallengeResultResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<ChallengeResultResponse>> call,
                                 Response<ApiService.ApiResponseWrapper<ChallengeResultResponse>> response) {
                android.util.Log.d("ChallengeRepository", "Challenge result response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponseWrapper<ChallengeResultResponse> wrapper = response.body();
                    if (wrapper.isSuccess() && wrapper.getData() != null) {
                        android.util.Log.d("ChallengeRepository", "Challenge result loaded successfully");
                        callback.onSuccess(wrapper.getData());
                    } else {
                        android.util.Log.w("ChallengeRepository", "Challenge result API success but no data: " + wrapper.getMessage());
                        callback.onError(wrapper.getMessage() != null ? wrapper.getMessage() : "Chưa có kết quả");
                    }
                } else {
                    android.util.Log.e("ChallengeRepository", "Challenge result API failed: " + response.code());
                    callback.onError("Không thể tải kết quả thách đấu");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<ChallengeResultResponse>> call, Throwable t) {
                android.util.Log.e("ChallengeRepository", "Challenge result API error: " + t.getMessage());
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
    }
    
    /**
     * Lấy trạng thái thách đấu real-time (để đợi cả 2 người hoàn thành)
     */
    public void getChallengeStatus(String challengeId, String childId, ResultCallback<com.kidsapp.data.response.ChallengeGameResponse> callback) {
        android.util.Log.d("ChallengeRepository", "Getting challenge status for challengeId: " + challengeId + ", childId: " + childId);
        
        apiService.getChallengeStatus(challengeId, childId).enqueue(new Callback<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeGameResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeGameResponse>> call,
                                 Response<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeGameResponse>> response) {
                android.util.Log.d("ChallengeRepository", "Challenge status response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeGameResponse> wrapper = response.body();
                    if (wrapper.isSuccess() && wrapper.getData() != null) {
                        android.util.Log.d("ChallengeRepository", "Challenge status loaded successfully: " + wrapper.getData().getStatus());
                        callback.onSuccess(wrapper.getData());
                    } else {
                        android.util.Log.w("ChallengeRepository", "Challenge status API success but no data: " + wrapper.getMessage());
                        callback.onError(wrapper.getMessage() != null ? wrapper.getMessage() : "Không thể lấy trạng thái");
                    }
                } else {
                    android.util.Log.e("ChallengeRepository", "Challenge status API failed: " + response.code());
                    callback.onError("Không thể lấy trạng thái thách đấu");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.ChallengeGameResponse>> call, Throwable t) {
                android.util.Log.e("ChallengeRepository", "Challenge status API error: " + t.getMessage());
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
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
