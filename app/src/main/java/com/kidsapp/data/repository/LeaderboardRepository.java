package com.kidsapp.data.repository;

import android.content.Context;

import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository xử lý các API liên quan đến Leaderboard
 */
public class LeaderboardRepository {
    
    private final ApiService apiService;
    private final SharedPref sharedPref;

    public LeaderboardRepository(Context context) {
        this.sharedPref = new SharedPref(context);
        this.apiService = RetrofitClient.getInstance(sharedPref).getApiService();
    }

    /**
     * Lấy bảng xếp hạng theo period
     */
    public void getLeaderboard(String period, ResultCallback<List<ApiService.LeaderboardResponse>> callback) {
        // For now, return mock data since backend API might not be implemented yet
        List<ApiService.LeaderboardResponse> mockData = createMockLeaderboardData();
        callback.onSuccess(mockData);
    }

    /**
     * Lấy bảng xếp hạng với limit
     */
    public void getLeaderboard(String period, int limit, OnLeaderboardCallback callback) {
        List<ApiService.LeaderboardResponse> mockData = createMockLeaderboardData();
        // Limit the results
        if (mockData.size() > limit) {
            mockData = mockData.subList(0, limit);
        }
        callback.onSuccess(mockData);
    }

    /**
     * Lấy ranking của user hiện tại
     */
    public void getMyRanking(String period, OnRankingCallback callback) {
        List<ApiService.LeaderboardResponse> mockData = createMockLeaderboardData();
        // Find current user
        for (ApiService.LeaderboardResponse item : mockData) {
            if (item.isCurrentUser != null && item.isCurrentUser) {
                callback.onSuccess(item);
                return;
            }
        }
        // If not found, return first item as current user
        if (!mockData.isEmpty()) {
            mockData.get(0).isCurrentUser = true;
            callback.onSuccess(mockData.get(0));
        } else {
            callback.onError("No ranking data found");
        }
    }

    /**
     * Tạo mock data cho leaderboard
     */
    private List<ApiService.LeaderboardResponse> createMockLeaderboardData() {
        List<ApiService.LeaderboardResponse> mockData = new ArrayList<>();
        
        String currentChildId = sharedPref.getChildId();
        
        // Add some mock leaderboard entries
        mockData.add(new ApiService.LeaderboardResponse(
            "child1", "Minh Anh", null, 1, 2500, 15, 20, 75.0, 5, 8, false
        ));
        
        mockData.add(new ApiService.LeaderboardResponse(
            "child2", "Hương Giang", null, 2, 2300, 12, 18, 66.7, 3, 6, false
        ));
        
        mockData.add(new ApiService.LeaderboardResponse(
            currentChildId != null ? currentChildId : "child3", "Bạn", null, 3, 2100, 10, 16, 62.5, 2, 4, true
        ));
        
        mockData.add(new ApiService.LeaderboardResponse(
            "child4", "Tuấn Kiệt", null, 4, 1900, 8, 15, 53.3, 1, 3, false
        ));
        
        mockData.add(new ApiService.LeaderboardResponse(
            "child5", "Lan Anh", null, 5, 1700, 6, 12, 50.0, 0, 2, false
        ));
        
        return mockData;
    }

    /**
     * Callback interface cho các API calls
     */
    public interface ResultCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    /**
     * Callback interface cho leaderboard
     */
    public interface OnLeaderboardCallback {
        void onSuccess(List<ApiService.LeaderboardResponse> leaderboard);
        void onError(String error);
    }

    /**
     * Callback interface cho ranking
     */
    public interface OnRankingCallback {
        void onSuccess(ApiService.LeaderboardResponse ranking);
        void onError(String error);
    }

    /**
     * Period constants for leaderboard
     */
    public static class Period {
        public static final String DAILY = "daily";
        public static final String WEEKLY = "weekly";
        public static final String MONTHLY = "monthly";
        public static final String ALL_TIME = "all_time";
    }
}