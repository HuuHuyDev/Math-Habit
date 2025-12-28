package com.kidsapp.data.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.model.Task;
import com.kidsapp.data.local.SharedPref;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for Tasks - Updated with new backend API
 */
public class TaskRepository {
    private static final String TAG = "TaskRepository";
    private ApiService apiService;
    private SharedPref sharedPref;
    private Context context;

    public TaskRepository(Context context) {
        this.context = context;
        sharedPref = new SharedPref(context);
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();
    }

    /**
     * Lấy tất cả tasks của child với filters
     */
    public void getTasksByChild(String childId, String status, String taskType, String date, TasksCallback callback) {
        Call<ApiService.ApiResponseWrapper<List<Task>>> call = apiService.getTasksByChild(childId, status, taskType, date);
        
        call.enqueue(new Callback<ApiService.ApiResponseWrapper<List<Task>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<Task>>> call, 
                                 Response<ApiService.ApiResponseWrapper<List<Task>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể tải danh sách nhiệm vụ");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<Task>>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
    }
    
    /**
     * Lấy tất cả tasks của child (không filter)
     */
    public void getTasksByChild(String childId, TasksCallback callback) {
        getTasksByChild(childId, null, null, null, callback);
    }

    /**
     * Lấy chi tiết task
     */
    public void getTaskDetail(String taskId, TaskCallback callback) {
        Call<ApiService.ApiResponseWrapper<Task>> call = apiService.getTaskDetail(taskId);
        
        call.enqueue(new Callback<ApiService.ApiResponseWrapper<Task>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Task>> call, 
                                 Response<ApiService.ApiResponseWrapper<Task>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể tải chi tiết nhiệm vụ");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Task>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
    }

    /**
     * Submit minh chứng hoàn thành task với file upload trực tiếp
     * Upload file và submit proof trong một request
     */
    public void submitTaskProofWithFile(String taskId, Uri fileUri, String note, TaskProofCallback callback) {
        try {
            // Convert Uri to File
            File file = uriToFile(fileUri);
            if (file == null) {
                callback.onError("Không thể đọc file");
                return;
            }
            
            // Determine media type
            String mimeType = context.getContentResolver().getType(fileUri);
            MediaType mediaType = MediaType.parse(mimeType != null ? mimeType : "application/octet-stream");
            
            // Create multipart body
            RequestBody fileBody = RequestBody.create(mediaType, file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
            
            // Create note body
            RequestBody noteBody = RequestBody.create(MediaType.parse("text/plain"), note != null ? note : "");
            
            // Call API
            Call<ApiService.ApiResponseWrapper<ApiService.TaskProofResponse>> call = 
                    apiService.submitTaskProofWithFile(taskId, filePart, noteBody);
            
            call.enqueue(new Callback<ApiService.ApiResponseWrapper<ApiService.TaskProofResponse>>() {
                @Override
                public void onResponse(Call<ApiService.ApiResponseWrapper<ApiService.TaskProofResponse>> call,
                                     Response<ApiService.ApiResponseWrapper<ApiService.TaskProofResponse>> response) {
                    // Clean up temp file
                    file.delete();
                    
                    if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                        callback.onSuccess(response.body().data);
                    } else {
                        callback.onError("Không thể gửi minh chứng");
                    }
                }

                @Override
                public void onFailure(Call<ApiService.ApiResponseWrapper<ApiService.TaskProofResponse>> call, Throwable t) {
                    // Clean up temp file
                    file.delete();
                    
                    Log.e(TAG, "Submit proof failed", t);
                    callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error preparing file upload", e);
            callback.onError("Lỗi: " + e.getMessage());
        }
    }
    
    /**
     * Convert Uri to File
     */
    private File uriToFile(Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;
            
            // Create temp file
            String fileName = "upload_" + System.currentTimeMillis();
            String extension = getFileExtension(uri);
            File tempFile = new File(context.getCacheDir(), fileName + extension);
            
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            outputStream.close();
            inputStream.close();
            
            return tempFile;
        } catch (Exception e) {
            Log.e(TAG, "Error converting Uri to File", e);
            return null;
        }
    }
    
    /**
     * Get file extension from Uri
     */
    private String getFileExtension(Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);
        if (mimeType != null) {
            if (mimeType.startsWith("image/")) {
                return ".jpg";
            } else if (mimeType.startsWith("video/")) {
                return ".mp4";
            }
        }
        return "";
    }

    /**
     * Submit minh chứng hoàn thành task (legacy method - với URL)
     */
    public void submitTaskProof(String taskId, String proofUrl, String proofType, String note, TaskCallback callback) {
        ApiService.TaskSubmissionRequest request = new ApiService.TaskSubmissionRequest(proofUrl, proofType, note);
        Call<ApiService.ApiResponseWrapper<Task>> call = apiService.submitTaskProof(taskId, request);
        
        call.enqueue(new Callback<ApiService.ApiResponseWrapper<Task>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Task>> call, 
                                 Response<ApiService.ApiResponseWrapper<Task>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể gửi minh chứng");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Task>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
    }

    // Legacy method - keep for backward compatibility
    public void getTasks(String childId, TasksCallback callback) {
        getTasksByChild(childId, callback);
    }
    
    /**
     * Lấy tasks theo status
     */
    public void getTasksByStatus(String childId, String status, TasksCallback callback) {
        getTasksByChild(childId, status, null, null, callback);
    }
    
    /**
     * Lấy tasks theo type
     */
    public void getTasksByType(String childId, String type, TasksCallback callback) {
        getTasksByChild(childId, null, type, null, callback);
    }

    // NOTE: createTask() method removed - use TaskAssignmentRepository.assignTask() instead

    // ==================== NEW HABIT/EXERCISE METHODS ====================
    
    /**
     * Nộp minh chứng cho HABIT task (ảnh/video)
     * Chỉ dùng cho HABIT, EXERCISE sẽ throw exception từ BE
     */
    public void submitHabitProof(String taskId, Uri fileUri, String note, SimpleCallback callback) {
        try {
            Log.d(TAG, "submitHabitProof: taskId=" + taskId + ", fileUri=" + fileUri);
            
            File file = uriToFile(fileUri);
            if (file == null) {
                Log.e(TAG, "submitHabitProof: Cannot convert Uri to File");
                callback.onError("Không thể đọc file");
                return;
            }
            
            Log.d(TAG, "submitHabitProof: file=" + file.getAbsolutePath() + ", size=" + file.length());
            
            String mimeType = context.getContentResolver().getType(fileUri);
            Log.d(TAG, "submitHabitProof: mimeType=" + mimeType);
            
            MediaType mediaType = MediaType.parse(mimeType != null ? mimeType : "application/octet-stream");
            
            RequestBody fileBody = RequestBody.create(mediaType, file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
            RequestBody noteBody = RequestBody.create(MediaType.parse("text/plain"), note != null ? note : "");
            
            Call<ApiService.ApiResponseWrapper<String>> call = 
                    apiService.submitHabitProof(taskId, filePart, noteBody);
            
            Log.d(TAG, "submitHabitProof: calling API...");
            
            call.enqueue(new Callback<ApiService.ApiResponseWrapper<String>>() {
                @Override
                public void onResponse(Call<ApiService.ApiResponseWrapper<String>> call,
                                     Response<ApiService.ApiResponseWrapper<String>> response) {
                    file.delete();
                    
                    Log.d(TAG, "submitHabitProof: response code=" + response.code());
                    
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "submitHabitProof: success=" + response.body().success + ", message=" + response.body().message);
                        if (response.body().success) {
                            callback.onSuccess();
                        } else {
                            callback.onError(response.body().message != null ? response.body().message : "Không thể gửi minh chứng");
                        }
                    } else {
                        // Log error body
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                            Log.e(TAG, "submitHabitProof: error body=" + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "submitHabitProof: cannot read error body");
                        }
                        callback.onError("Không thể gửi minh chứng (code: " + response.code() + ")");
                    }
                }

                @Override
                public void onFailure(Call<ApiService.ApiResponseWrapper<String>> call, Throwable t) {
                    file.delete();
                    Log.e(TAG, "submitHabitProof: onFailure", t);
                    callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "submitHabitProof: exception", e);
            callback.onError("Lỗi: " + e.getMessage());
        }
    }
    
    /**
     * Hoàn thành EXERCISE task (sau khi làm bài xong)
     * Tự động complete, không cần Parent duyệt
     */
    public void completeExercise(String taskId, Integer score, Integer correctAnswers, Integer totalAnswers, TaskCallback callback) {
        Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>> call = 
                apiService.completeExercise(taskId, score, correctAnswers, totalAnswers);
        
        call.enqueue(new Callback<ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>> call,
                                 Response<ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    // Convert TaskResponse to Task
                    Task task = new Task();
                    task.setId(response.body().data.getId());
                    task.setStatus(response.body().data.getStatus());
                    callback.onSuccess(task);
                } else {
                    String msg = response.body() != null ? response.body().message : "Không thể hoàn thành bài tập";
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
    }
    
    /**
     * Parent duyệt HABIT task
     */
    public void approveHabit(String taskId, TaskCallback callback) {
        Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>> call = 
                apiService.approveHabit(taskId);
        
        call.enqueue(new Callback<ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>> call,
                                 Response<ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    Task task = new Task();
                    task.setId(response.body().data.getId());
                    task.setStatus(response.body().data.getStatus());
                    callback.onSuccess(task);
                } else {
                    String msg = response.body() != null ? response.body().message : "Không thể duyệt nhiệm vụ";
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
    }
    
    /**
     * Parent từ chối HABIT task
     */
    public void rejectHabit(String taskId, String reason, TaskCallback callback) {
        Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>> call = 
                apiService.rejectHabit(taskId, reason);
        
        call.enqueue(new Callback<ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>> call,
                                 Response<ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    Task task = new Task();
                    task.setId(response.body().data.getId());
                    task.setStatus(response.body().data.getStatus());
                    callback.onSuccess(task);
                } else {
                    String msg = response.body() != null ? response.body().message : "Không thể từ chối nhiệm vụ";
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
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
    
    public interface TaskProofCallback {
        void onSuccess(ApiService.TaskProofResponse proof);
        void onError(String error);
    }
    
    public interface SimpleCallback {
        void onSuccess();
        void onError(String error);
    }
}

