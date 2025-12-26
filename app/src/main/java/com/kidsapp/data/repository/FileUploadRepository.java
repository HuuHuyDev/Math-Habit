package com.kidsapp.data.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository để upload file (ảnh/video)
 */
public class FileUploadRepository {
    
    private static final String TAG = "FileUploadRepository";
    
    private ApiService apiService;
    private Context context;
    
    public FileUploadRepository(Context context) {
        this.context = context;
        SharedPref sharedPref = new SharedPref(context);
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();
    }
    
    /**
     * Upload ảnh minh chứng
     */
    public void uploadImage(Uri imageUri, UploadCallback callback) {
        try {
            // Convert URI to File
            File file = getFileFromUri(imageUri, "image_temp.jpg");
            if (file == null) {
                callback.onError("Không thể đọc file ảnh");
                return;
            }
            
            // Create RequestBody
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("image/*"),
                    file
            );
            
            // Create MultipartBody.Part
            MultipartBody.Part body = MultipartBody.Part.createFormData(
                    "file",
                    file.getName(),
                    requestFile
            );
            
            // Call API
            Call<ApiService.ApiResponseWrapper<ApiService.UploadResponse>> call = 
                    apiService.uploadImage(body);
            
            call.enqueue(new Callback<ApiService.ApiResponseWrapper<ApiService.UploadResponse>>() {
                @Override
                public void onResponse(Call<ApiService.ApiResponseWrapper<ApiService.UploadResponse>> call,
                                     Response<ApiService.ApiResponseWrapper<ApiService.UploadResponse>> response) {
                    // Delete temp file
                    file.delete();
                    
                    if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                        String url = response.body().data.url;
                        Log.d(TAG, "Upload image success: " + url);
                        callback.onSuccess(url, "IMAGE");
                    } else {
                        callback.onError("Upload thất bại");
                    }
                }

                @Override
                public void onFailure(Call<ApiService.ApiResponseWrapper<ApiService.UploadResponse>> call, 
                                    Throwable t) {
                    file.delete();
                    Log.e(TAG, "Upload image failed", t);
                    callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error preparing image upload", e);
            callback.onError("Lỗi khi chuẩn bị upload: " + e.getMessage());
        }
    }
    
    /**
     * Upload video minh chứng
     */
    public void uploadVideo(Uri videoUri, UploadCallback callback) {
        try {
            // Convert URI to File
            File file = getFileFromUri(videoUri, "video_temp.mp4");
            if (file == null) {
                callback.onError("Không thể đọc file video");
                return;
            }
            
            // Create RequestBody
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("video/*"),
                    file
            );
            
            // Create MultipartBody.Part
            MultipartBody.Part body = MultipartBody.Part.createFormData(
                    "file",
                    file.getName(),
                    requestFile
            );
            
            // Call API
            Call<ApiService.ApiResponseWrapper<ApiService.UploadResponse>> call = 
                    apiService.uploadVideo(body);
            
            call.enqueue(new Callback<ApiService.ApiResponseWrapper<ApiService.UploadResponse>>() {
                @Override
                public void onResponse(Call<ApiService.ApiResponseWrapper<ApiService.UploadResponse>> call,
                                     Response<ApiService.ApiResponseWrapper<ApiService.UploadResponse>> response) {
                    // Delete temp file
                    file.delete();
                    
                    if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                        String url = response.body().data.url;
                        Log.d(TAG, "Upload video success: " + url);
                        callback.onSuccess(url, "VIDEO");
                    } else {
                        callback.onError("Upload thất bại");
                    }
                }

                @Override
                public void onFailure(Call<ApiService.ApiResponseWrapper<ApiService.UploadResponse>> call, 
                                    Throwable t) {
                    file.delete();
                    Log.e(TAG, "Upload video failed", t);
                    callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error preparing video upload", e);
            callback.onError("Lỗi khi chuẩn bị upload: " + e.getMessage());
        }
    }
    
    /**
     * Convert URI to File
     */
    private File getFileFromUri(Uri uri, String filename) {
        try {
            File tempFile = new File(context.getCacheDir(), filename);
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            
            if (inputStream == null) {
                return null;
            }
            
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
            Log.e(TAG, "Error converting URI to File", e);
            return null;
        }
    }
    
    public interface UploadCallback {
        void onSuccess(String url, String type);
        void onError(String error);
    }
}
