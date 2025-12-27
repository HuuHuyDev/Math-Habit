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
    }

    /**
     * Lấy danh sách Category cho Challenge
     */
    public void getChallengeCategories(ResultCallback<List<com.kidsapp.data.model.Category>> callback) {
        android.util.Log.d("ChallengeRepository", "Calling getChallengeCategories API with type=CHALLENGE");
        
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
        
        apiService.getChildren().enqueue(new Callback<List<Child>>() {
            @Override
            public void onResponse(Call<List<Child>> call, Response<List<Child>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Lọc bỏ chính mình
                    List<Child> friends = new ArrayList<>();
                    for (Child child : response.body()) {
                        if (currentChildId == null || !child.getId().equals(currentChildId)) {
                            friends.add(child);
                        }
                    }
                    callback.onSuccess(friends);
                } else {
                    callback.onError("Không thể tải danh sách bạn bè");
                }
            }

            @Override
            public void onFailure(Call<List<Child>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    /**
     * Gửi lời mời thách đấu
     */
    public void sendChallengeInvite(String opponentId, ResultCallback<String> callback) {
        // TODO: Implement API call
        // apiService.sendChallengeInvite(opponentId).enqueue(...)
        
        // Mock success
        callback.onSuccess("invite_" + System.currentTimeMillis());
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
