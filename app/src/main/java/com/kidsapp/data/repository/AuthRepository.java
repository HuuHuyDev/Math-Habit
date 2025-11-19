package com.kidsapp.data.repository;

import android.content.Context;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for Authentication
 */
public class AuthRepository {
    private ApiService apiService;
    private SharedPref sharedPref;

    public AuthRepository(Context context) {
        try {
            sharedPref = new SharedPref(context);
            apiService = RetrofitClient.getInstance(sharedPref).getApiService();
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback: tạo SharedPref nhưng không tạo API service
            sharedPref = new SharedPref(context);
        }
    }

    public void login(String email, String password, AuthCallback callback) {
        if (apiService == null) {
            callback.onError("API service chưa được khởi tạo. Vui lòng kiểm tra kết nối mạng.");
            return;
        }
        
        try {
            ApiService.LoginRequest request = new ApiService.LoginRequest(email, password);
            Call<ApiService.AuthResponse> call = apiService.login(request);
            
            call.enqueue(new Callback<ApiService.AuthResponse>() {
                @Override
                public void onResponse(Call<ApiService.AuthResponse> call, Response<ApiService.AuthResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiService.AuthResponse authResponse = response.body();
                        // Save token and user info
                        if (authResponse.token != null) {
                            sharedPref.saveAuthToken(authResponse.token);
                        }
                        if (authResponse.userId != null) {
                            sharedPref.saveUserId(authResponse.userId);
                        }
                        if (authResponse.role != null) {
                            sharedPref.saveUserRole(authResponse.role);
                        }
                        sharedPref.setLoggedIn(true);
                        callback.onSuccess(authResponse);
                    } else {
                        callback.onError("Đăng nhập thất bại");
                    }
                }

                @Override
                public void onFailure(Call<ApiService.AuthResponse> call, Throwable t) {
                    callback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Không thể kết nối đến server"));
                }
            });
        } catch (Exception e) {
            callback.onError("Lỗi: " + e.getMessage());
        }
    }

    public void logout() {
        sharedPref.clearAll();
    }

    public interface AuthCallback {
        void onSuccess(ApiService.AuthResponse response);
        void onError(String error);
    }
}

