package com.kidsapp.data.repository;

import android.content.Context;
import android.util.Log;

import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.ExerciseContent;
import com.kidsapp.data.model.ExerciseResult;
import com.kidsapp.data.model.QuestionResponse;
import com.kidsapp.data.request.SubmitAnswerRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository xử lý API calls cho Exercise
 */
public class ExerciseRepository {
    
    private static final String TAG = "ExerciseRepository";
    private final ApiService apiService;
    private final SharedPref sharedPref;

    public ExerciseRepository(Context context) {
        this.sharedPref = new SharedPref(context);
        this.apiService = RetrofitClient.getInstance(sharedPref).getApiService();
    }

    /**
     * Lấy tất cả bài tập
     */
    public void getAllExercises(String childId, ExerciseListCallback callback) {
        apiService.getAllExercises(childId).enqueue(new Callback<ApiService.ApiResponseWrapper<List<ExerciseContent>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ExerciseContent>>> call,
                                 Response<ApiService.ApiResponseWrapper<List<ExerciseContent>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể tải danh sách bài tập");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ExerciseContent>>> call, Throwable t) {
                Log.e(TAG, "getAllExercises failed", t);
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
    }

    /**
     * Lấy bài tập theo lớp
     */
    public void getExercisesByGrade(String childId, int gradeLevel, ExerciseListCallback callback) {
        apiService.getExercisesByGrade(gradeLevel, childId).enqueue(new Callback<ApiService.ApiResponseWrapper<List<ExerciseContent>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ExerciseContent>>> call,
                                 Response<ApiService.ApiResponseWrapper<List<ExerciseContent>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể tải danh sách bài tập");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ExerciseContent>>> call, Throwable t) {
                Log.e(TAG, "getExercisesByGrade failed", t);
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
    }

    /**
     * Lấy câu hỏi của bài tập
     */
    public void getExerciseQuestions(String exerciseId, QuestionListCallback callback) {
        apiService.getExerciseQuestions(exerciseId).enqueue(new Callback<ApiService.ApiResponseWrapper<List<QuestionResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<QuestionResponse>>> call,
                                 Response<ApiService.ApiResponseWrapper<List<QuestionResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể tải câu hỏi");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<QuestionResponse>>> call, Throwable t) {
                Log.e(TAG, "getExerciseQuestions failed", t);
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
    }

    /**
     * Bắt đầu làm bài
     */
    public void startExercise(String exerciseId, String childId, StartExerciseCallback callback) {
        apiService.startExercise(exerciseId, childId).enqueue(new Callback<ApiService.ApiResponseWrapper<Void>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Void>> call,
                                 Response<ApiService.ApiResponseWrapper<Void>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Không thể bắt đầu bài tập");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Void>> call, Throwable t) {
                Log.e(TAG, "startExercise failed", t);
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
    }

    /**
     * Submit bài làm
     */
    public void submitExercise(String childId, SubmitAnswerRequest request, SubmitExerciseCallback callback) {
        apiService.submitExercise(childId, request).enqueue(new Callback<ApiService.ApiResponseWrapper<ExerciseResult>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<ExerciseResult>> call,
                                 Response<ApiService.ApiResponseWrapper<ExerciseResult>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể nộp bài");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<ExerciseResult>> call, Throwable t) {
                Log.e(TAG, "submitExercise failed", t);
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
    }

    // Callbacks
    public interface ExerciseListCallback {
        void onSuccess(List<ExerciseContent> exercises);
        void onError(String error);
    }

    public interface QuestionListCallback {
        void onSuccess(List<QuestionResponse> questions);
        void onError(String error);
    }

    public interface StartExerciseCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface SubmitExerciseCallback {
        void onSuccess(ExerciseResult result);
        void onError(String error);
    }
}
