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
     * Lấy tất cả tasks của child
     */
    public void getTasksByChild(String childId, TasksCallback callback) {
        Call<ApiService.ApiResponseWrapper<List<Task>>> call = apiService.getTasksByChild(childId);
        
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
     * Lấy tasks theo status (pending, in_progress, completed)
     */
    public void getTasksByStatus(String childId, String status, TasksCallback callback) {
        Call<ApiService.ApiResponseWrapper<List<Task>>> call = apiService.getTasksByStatus(childId, status);
        
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
     * Lấy tasks theo type (housework, habit, exercise, custom)
     */
    public void getTasksByType(String childId, String type, TasksCallback callback) {
        Call<ApiService.ApiResponseWrapper<List<Task>>> call = apiService.getTasksByType(childId, type);
        
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

    /**
     * Hoàn thành task (không cần minh chứng - dùng cho exercise)
     */
    public void completeTask(String taskId, TaskCallback callback) {
        Call<ApiService.ApiResponseWrapper<Task>> call = apiService.completeTask(taskId);
        
        call.enqueue(new Callback<ApiService.ApiResponseWrapper<Task>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Task>> call, 
                                 Response<ApiService.ApiResponseWrapper<Task>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể hoàn thành nhiệm vụ");
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
    
    public interface TaskProofCallback {
        void onSuccess(ApiService.TaskProofResponse proof);
        void onError(String error);
    }
}

