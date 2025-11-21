package com.kidsapp.data.repository;

import android.content.Context;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.model.Task;
import com.kidsapp.data.local.SharedPref;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for Tasks
 */
public class TaskRepository {
    private ApiService apiService;
    private SharedPref sharedPref;

    public TaskRepository(Context context) {
        sharedPref = new SharedPref(context);
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();
    }

    public void getTasks(String childId, TasksCallback callback) {
        Call<List<Task>> call = apiService.getTasks(childId);
        
        call.enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải danh sách nhiệm vụ");
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void createTask(Task task, TaskCallback callback) {
        Call<Task> call = apiService.createTask(task);
        
        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tạo nhiệm vụ");
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface TasksCallback {
        void onSuccess(List<Task> tasks);
        void onError(String error);
    }

    public interface TaskCallback {
        void onSuccess(Task task);
        void onError(String error);
    }
}

