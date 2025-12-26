package com.kidsapp.data.repository;

import android.content.Context;
import android.util.Log;

import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository để gọi Task Assignment APIs
 */
public class TaskAssignmentRepository {
    private static final String TAG = "TaskAssignmentRepo";
    private final ApiService apiService;

    public TaskAssignmentRepository(Context context) {
        SharedPref sharedPref = new SharedPref(context);
        this.apiService = RetrofitClient.getInstance(sharedPref).getApiService();
    }

    // ==================== CALLBACK INTERFACES ====================

    public interface OnSubjectsCallback {
        void onSuccess(List<String> subjects);
        void onError(String message);
    }

    public interface OnExercisesCallback {
        void onSuccess(List<ApiService.ExerciseTemplateResponse> exercises);
        void onError(String message);
    }

    public interface OnHabitCategoriesCallback {
        void onSuccess(List<String> categories);
        void onError(String message);
    }

    public interface OnHabitsCallback {
        void onSuccess(List<ApiService.HabitTemplateResponse> habits);
        void onError(String message);
    }

    public interface OnAssignTaskCallback {
        void onSuccess(ApiService.TaskAssignmentResponse task);
        void onError(String message);
    }

    public interface OnAssignMultipleCallback {
        void onSuccess(List<ApiService.TaskAssignmentResponse> tasks);
        void onError(String message);
    }

    public interface OnUpdateTaskCallback {
        void onSuccess(ApiService.TaskAssignmentResponse task);
        void onError(String message);
    }

    public interface OnDeleteTaskCallback {
        void onSuccess();
        void onError(String message);
    }

    // ==================== API METHODS ====================

    /**
     * Lấy danh sách môn học có sẵn
     */
    public void getAvailableSubjects(OnSubjectsCallback callback) {
        apiService.getAvailableSubjects().enqueue(new Callback<ApiService.ApiResponseWrapper<List<String>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<String>>> call,
                                   Response<ApiService.ApiResponseWrapper<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể lấy danh sách môn học");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<String>>> call, Throwable t) {
                Log.e(TAG, "getAvailableSubjects error", t);
                callback.onError(t.getMessage());
            }
        });
    }

    /**
     * Lấy danh sách bài tập theo môn và lớp
     */
    public void getExerciseTemplates(String subject, Integer gradeLevel, OnExercisesCallback callback) {
        apiService.getExerciseTemplates(subject, gradeLevel).enqueue(
                new Callback<ApiService.ApiResponseWrapper<List<ApiService.ExerciseTemplateResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.ExerciseTemplateResponse>>> call,
                                   Response<ApiService.ApiResponseWrapper<List<ApiService.ExerciseTemplateResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể lấy danh sách bài tập");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.ExerciseTemplateResponse>>> call, Throwable t) {
                Log.e(TAG, "getExerciseTemplates error", t);
                callback.onError(t.getMessage());
            }
        });
    }

    /**
     * Lấy danh sách bài tập phù hợp với bé
     */
    public void getExerciseTemplatesForChild(String childId, String subject, OnExercisesCallback callback) {
        apiService.getExerciseTemplatesForChild(childId, subject).enqueue(
                new Callback<ApiService.ApiResponseWrapper<List<ApiService.ExerciseTemplateResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.ExerciseTemplateResponse>>> call,
                                   Response<ApiService.ApiResponseWrapper<List<ApiService.ExerciseTemplateResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể lấy danh sách bài tập cho bé");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.ExerciseTemplateResponse>>> call, Throwable t) {
                Log.e(TAG, "getExerciseTemplatesForChild error", t);
                callback.onError(t.getMessage());
            }
        });
    }

    /**
     * Lấy danh sách category thói quen
     */
    public void getAvailableHabitCategories(OnHabitCategoriesCallback callback) {
        apiService.getAvailableHabitCategories().enqueue(new Callback<ApiService.ApiResponseWrapper<List<String>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<String>>> call,
                                   Response<ApiService.ApiResponseWrapper<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể lấy danh sách loại thói quen");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<String>>> call, Throwable t) {
                Log.e(TAG, "getAvailableHabitCategories error", t);
                callback.onError(t.getMessage());
            }
        });
    }

    /**
     * Lấy danh sách thói quen theo category
     */
    public void getHabitTemplates(String category, OnHabitsCallback callback) {
        apiService.getHabitTemplates(category).enqueue(
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
    public void getHabitTemplatesForChild(String childId, String category, OnHabitsCallback callback) {
        apiService.getHabitTemplatesForChild(childId, category).enqueue(
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
     * Giao bài tập/thói quen cho con
     */
    public void assignTask(ApiService.AssignTaskRequest request, OnAssignTaskCallback callback) {
        apiService.assignTask(request).enqueue(
                new Callback<ApiService.ApiResponseWrapper<ApiService.TaskAssignmentResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<ApiService.TaskAssignmentResponse>> call,
                                   Response<ApiService.ApiResponseWrapper<ApiService.TaskAssignmentResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    callback.onSuccess(response.body().data);
                } else {
                    String msg = response.body() != null ? response.body().message : "Không thể giao bài tập";
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<ApiService.TaskAssignmentResponse>> call, Throwable t) {
                Log.e(TAG, "assignTask error", t);
                callback.onError(t.getMessage());
            }
        });
    }

    /**
     * Giao nhiều bài tập cùng lúc
     */
    public void assignMultipleTasks(List<ApiService.AssignTaskRequest> requests, OnAssignMultipleCallback callback) {
        apiService.assignMultipleTasks(requests).enqueue(
                new Callback<ApiService.ApiResponseWrapper<List<ApiService.TaskAssignmentResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.TaskAssignmentResponse>>> call,
                                   Response<ApiService.ApiResponseWrapper<List<ApiService.TaskAssignmentResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    callback.onSuccess(response.body().data);
                } else {
                    String msg = response.body() != null ? response.body().message : "Không thể giao bài tập";
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.TaskAssignmentResponse>>> call, Throwable t) {
                Log.e(TAG, "assignMultipleTasks error", t);
                callback.onError(t.getMessage());
            }
        });
    }

    /**
     * Cập nhật task đã giao
     */
    public void updateTask(String taskId, ApiService.UpdateTaskRequest request, OnUpdateTaskCallback callback) {
        apiService.updateTaskAssignment(taskId, request).enqueue(
                new Callback<ApiService.ApiResponseWrapper<ApiService.TaskAssignmentResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<ApiService.TaskAssignmentResponse>> call,
                                   Response<ApiService.ApiResponseWrapper<ApiService.TaskAssignmentResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    callback.onSuccess(response.body().data);
                } else {
                    String msg = response.body() != null ? response.body().message : "Không thể cập nhật task";
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<ApiService.TaskAssignmentResponse>> call, Throwable t) {
                Log.e(TAG, "updateTask error", t);
                callback.onError(t.getMessage());
            }
        });
    }

    /**
     * Xóa task đã giao
     */
    public void deleteTask(String taskId, OnDeleteTaskCallback callback) {
        apiService.deleteTaskAssignment(taskId).enqueue(
                new Callback<ApiService.ApiResponseWrapper<Void>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Void>> call,
                                   Response<ApiService.ApiResponseWrapper<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    callback.onSuccess();
                } else {
                    String msg = response.body() != null ? response.body().message : "Không thể xóa task";
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
