package com.kidsapp.data.repository;

import android.content.Context;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.model.ActivityLog;
import com.kidsapp.data.model.WeeklyProgress;
import com.kidsapp.data.local.SharedPref;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for Reports
 */
public class ReportRepository {
    private ApiService apiService;
    private SharedPref sharedPref;

    public ReportRepository(Context context) {
        sharedPref = new SharedPref(context);
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();
    }

    public void getActivityLogs(int limit, ActivityLogsCallback callback) {
        apiService.getActivities(limit).enqueue(new Callback<ApiService.ApiResponseWrapper<List<ApiService.ActivityLogResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.ActivityLogResponse>>> call,
                                   Response<ApiService.ApiResponseWrapper<List<ApiService.ActivityLogResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponseWrapper<List<ApiService.ActivityLogResponse>> wrapper = response.body();
                    if (wrapper.success && wrapper.data != null) {
                        // Map to ActivityLog
                        List<ActivityLog> logs = new java.util.ArrayList<>();
                        for (ApiService.ActivityLogResponse r : wrapper.data) {
                            logs.add(new ActivityLog(
                                    r.id, r.childId, r.childName, r.description,
                                    r.xpEarned != null ? r.xpEarned : 0,
                                    r.activityType, r.childAvatar, r.timeAgo
                            ));
                        }
                        callback.onSuccess(logs);
                    } else {
                        callback.onError("Không thể tải nhật ký hoạt động");
                    }
                } else {
                    callback.onError("Không thể tải nhật ký hoạt động");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.ActivityLogResponse>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getWeeklyProgress(String childId, WeeklyProgressCallback callback) {
        Call<WeeklyProgress> call = apiService.getWeeklyProgress(childId);
        
        call.enqueue(new Callback<WeeklyProgress>() {
            @Override
            public void onResponse(Call<WeeklyProgress> call, Response<WeeklyProgress> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải tiến độ tuần");
                }
            }

            @Override
            public void onFailure(Call<WeeklyProgress> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface ActivityLogsCallback {
        void onSuccess(List<ActivityLog> logs);
        void onError(String error);
    }

    public interface WeeklyProgressCallback {
        void onSuccess(WeeklyProgress progress);
        void onError(String error);
    }
}
