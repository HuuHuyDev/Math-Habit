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
