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

    public void getActivityLogs(String parentId, ActivityLogsCallback callback) {
        Call<List<ActivityLog>> call = apiService.getActivityLogs(parentId);
        
        call.enqueue(new Callback<List<ActivityLog>>() {
            @Override
            public void onResponse(Call<List<ActivityLog>> call, Response<List<ActivityLog>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải nhật ký hoạt động");
                }
            }

            @Override
            public void onFailure(Call<List<ActivityLog>> call, Throwable t) {
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

