package com.kidsapp.data.repository;

import android.content.Context;
import android.util.Log;

import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.ExerciseContent;
import com.kidsapp.data.request.CreateTaskRequest;
import com.kidsapp.data.response.TaskResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository để gọi Task Assignment APIs
 * Sử dụng endpoints mới từ TaskController
 */
public class TaskAssignmentRepository {
    private static final String TAG = "TaskAssignmentRepo";
    private final ApiService apiService;

    public TaskAssignmentRepository(Context context) {
        SharedPref sharedPref = new SharedPref(context);
        this.apiService = RetrofitClient.getInstance(sharedPref).getApiService();
    }

    // ==================== CALLBACK INTERFACES ====================

    public interface OnExercisesCallback {
        void onSuccess(List<ExerciseContent> exercises);
        void onError(String message);
    }

    public interface OnHabitsCallback {
        void onSuccess(List<ApiService.HabitTemplateResponse> habits);
        void onError(String message);
    }

    public interface OnCreateTaskCallback {
        void onSuccess(TaskResponse task);
        void onError(String message);
    }

    public interface OnDeleteTaskCallback {
        void onSuccess();
        void onError(String message);
    }

    // ==================== API METHODS ====================

    /**
     * Lấy danh sách bài tập cho bé
     */
    public void getExercisesForChild(String childId, OnExercisesCallback callback) {
        apiService.getExercisesForChild(childId, null).enqueue(
                new Callback<ApiService.ApiResponseWrapper<List<ExerciseContent>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ExerciseContent>>> call,
                                   Response<ApiService.ApiResponseWrapper<List<ExerciseContent>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể lấy danh sách bài tập");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ExerciseContent>>> call, Throwable t) {
                Log.e(TAG, "getExercisesForChild error", t);
                callback.onError(t.getMessage());
            }
        });
    }

    /**
     * Lấy danh sách thói quen mẫu
     */
    public void getHabitTemplates(String categoryId, OnHabitsCallback callback) {
        apiService.getHabitTemplates(categoryId).enqueue(
                new Callback<ApiService.ApiResponseWrapper<List<ApiService.HabitTemplateResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.HabitTemplateResponse>>> call,
                                   Response<ApiService.ApiResponseWrapper<List<ApiService.HabitTemplateResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể lấy danh sách thói quen");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.HabitTemplateResponse>>> call, Throwable t) {
                Log.e(TAG, "getHabitTemplates error", t);
                callback.onError(t.getMessage());
            }
        });
    }

    /**
     * Lấy danh sách thói quen phù hợp với bé
     */
    public void getHabitTemplatesForChild(String childId, String categoryId, OnHabitsCallback callback) {
        apiService.getHabitTemplatesForChild(childId, categoryId).enqueue(
                new Callback<ApiService.ApiResponseWrapper<List<ApiService.HabitTemplateResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.HabitTemplateResponse>>> call,
                                   Response<ApiService.ApiResponseWrapper<List<ApiService.HabitTemplateResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể lấy danh sách thói quen cho bé");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.HabitTemplateResponse>>> call, Throwable t) {
                Log.e(TAG, "getHabitTemplatesForChild error", t);
                callback.onError(t.getMessage());
            }
        });
    }

    /**
     * Tạo task mới (giao bài tập/thói quen cho con)
     */
    public void createTask(CreateTaskRequest request, OnCreateTaskCallback callback) {
        apiService.createTask(request).enqueue(
                new Callback<ApiService.ApiResponseWrapper<TaskResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<TaskResponse>> call,
                                   Response<ApiService.ApiResponseWrapper<TaskResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    callback.onSuccess(response.body().data);
                } else {
                    String msg = response.body() != null ? response.body().message : "Không thể giao bài tập";
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<TaskResponse>> call, Throwable t) {
                Log.e(TAG, "createTask error", t);
                callback.onError(t.getMessage());
            }
        });
    }

    /**
     * Cập nhật task
     */
    public void updateTask(String taskId, com.kidsapp.data.request.UpdateTaskRequest request, OnCreateTaskCallback callback) {
        apiService.updateTask(taskId, request).enqueue(
                new Callback<ApiService.ApiResponseWrapper<TaskResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<TaskResponse>> call,
                                   Response<ApiService.ApiResponseWrapper<TaskResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    callback.onSuccess(response.body().data);
                } else {
                    String msg = response.body() != null ? response.body().message : "Không thể cập nhật nhiệm vụ";
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<TaskResponse>> call, Throwable t) {
                Log.e(TAG, "updateTask error", t);
                callback.onError(t.getMessage());
            }
        });
    }

    /**
     * Xóa task
     */
    public void deleteTask(String taskId, OnDeleteTaskCallback callback) {
        apiService.deleteTask(taskId).enqueue(
                new Callback<ApiService.ApiResponseWrapper<Void>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Void>> call,
                                   Response<ApiService.ApiResponseWrapper<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    callback.onSuccess();
                } else {
                    String msg = response.body() != null ? response.body().message : "Không thể xóa nhiệm vụ";
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Void>> call, Throwable t) {
                Log.e(TAG, "deleteTask error", t);
                callback.onError(t.getMessage());
            }
        });
    }
}
