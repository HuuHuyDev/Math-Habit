package com.kidsapp.data.api.interceptor;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.kidsapp.data.api.ApiConfig;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

/**
 * Interceptor to add authentication token to API requests
 * and handle 401 errors with token refresh
 */
public class AuthInterceptor implements Interceptor {
    private SharedPref sharedPref;
    private static boolean isRefreshing = false;
    private static final Object lock = new Object();

    // Các endpoint không cần token
    private static final String[] PUBLIC_ENDPOINTS = {
            "/auth/token",
            "/auth/register",
            "/auth/google",
            "/auth/facebook",
            "/auth/forgot-password",
            "/auth/verify-otp",
            "/auth/reset-password",
            "/auth/token/refresh"
    };

    public AuthInterceptor(SharedPref sharedPref) {
        this.sharedPref = sharedPref;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String path = originalRequest.url().encodedPath();

        // Không thêm token cho các endpoint public
        if (isPublicEndpoint(path)) {
            android.util.Log.d("AuthInterceptor", "Public endpoint: " + path + " - No token added");
            return chain.proceed(originalRequest);
        }

        // Get token from SharedPreferences
        String token = sharedPref.getAuthToken();
        
        android.util.Log.d("AuthInterceptor", "=== API Request ===");
        android.util.Log.d("AuthInterceptor", "Path: " + path);
        android.util.Log.d("AuthInterceptor", "Token: " + (token != null ? "EXISTS (length=" + token.length() + ")" : "NULL"));

        Request request = originalRequest;
        if (token != null && !token.isEmpty()) {
            request = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .build();
            android.util.Log.d("AuthInterceptor", "Token added to request header");
        } else {
            android.util.Log.w("AuthInterceptor", "No token available - Request will fail if endpoint requires auth!");
        }

        Response response = chain.proceed(request);
        android.util.Log.d("AuthInterceptor", "Response code: " + response.code());

        // Nếu 401 Unauthorized, thử refresh token
        if (response.code() == 401) {
            android.util.Log.w("AuthInterceptor", "Got 401 Unauthorized for path: " + path);
            
            synchronized (lock) {
                // Kiểm tra lại token (có thể đã được refresh bởi request khác)
                String currentToken = sharedPref.getAuthToken();
                
                // Nếu token đã thay đổi, retry với token mới
                if (token != null && !token.equals(currentToken) && currentToken != null) {
                    android.util.Log.d("AuthInterceptor", "Token changed, retrying with new token");
                    response.close();
                    Request newRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer " + currentToken)
                            .header("Content-Type", "application/json")
                            .build();
                    return chain.proceed(newRequest);
                }

                // Thử refresh token
                if (!isRefreshing) {
                    android.util.Log.d("AuthInterceptor", "Attempting to refresh token...");
                    isRefreshing = true;
                    boolean refreshSuccess = tryRefreshToken();
                    isRefreshing = false;

                    if (refreshSuccess) {
                        android.util.Log.d("AuthInterceptor", "Token refreshed successfully, retrying request");
                        response.close();
                        String newToken = sharedPref.getAuthToken();
                        Request newRequest = originalRequest.newBuilder()
                                .header("Authorization", "Bearer " + newToken)
                                .header("Content-Type", "application/json")
                                .build();
                        return chain.proceed(newRequest);
                    } else {
                        // Refresh thất bại -> KHÔNG logout tự động
                        android.util.Log.e("AuthInterceptor", "Token refresh failed - keeping session for now");
                        // Để API callback xử lý lỗi
                    }
                }
            }
        }

        return response;
    }

    private boolean isPublicEndpoint(String path) {
        for (String endpoint : PUBLIC_ENDPOINTS) {
            if (path.contains(endpoint)) {
                return true;
            }
        }
        return false;
    }

    private boolean tryRefreshToken() {
        String refreshToken = sharedPref.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            return false;
        }

        try {
            OkHttpClient client = new OkHttpClient();
            
            // Tạo request body
            String json = "{\"refreshToken\":\"" + refreshToken + "\"}";
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            
            Request request = new Request.Builder()
                    .url(ApiConfig.BASE_URL + "auth/token/refresh")
                    .post(body)
                    .header("Content-Type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();
            
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                Gson gson = new Gson();
                ApiService.ApiResponseWrapper<ApiService.AuthResponse> wrapper = 
                        gson.fromJson(responseBody, 
                                new com.google.gson.reflect.TypeToken<ApiService.ApiResponseWrapper<ApiService.AuthResponse>>(){}.getType());
                
                if (wrapper != null && wrapper.success && wrapper.data != null) {
                    // Lưu token mới
                    sharedPref.saveAuthToken(wrapper.data.accessToken);
                    if (wrapper.data.refreshToken != null) {
                        sharedPref.saveRefreshToken(wrapper.data.refreshToken);
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            android.util.Log.e("AuthInterceptor", "Refresh token failed", e);
        }
        
        return false;
    }

    private void handleLogout() {
        // Clear session data
        sharedPref.clearAll();
        RetrofitClient.resetInstance();
        
        // Log và hiển thị thông báo
        android.util.Log.w("AuthInterceptor", "Token expired, user logged out");
        
        // Không tự động logout nữa, để user thấy thông báo lỗi từ API
    }
}
