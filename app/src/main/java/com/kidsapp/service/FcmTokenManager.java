package com.kidsapp.service;

import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Manager để quản lý FCM Token
 * - Lấy token từ Firebase
 * - Gửi token lên server
 * - Xóa token khi logout
 */
public class FcmTokenManager {

    private static final String TAG = "FcmTokenManager";
    private final Context context;
    private final SharedPref sharedPref;
    private final ApiService apiService;

    public FcmTokenManager(Context context) {
        this.context = context;
        this.sharedPref = new SharedPref(context);
        this.apiService = RetrofitClient.getInstance(sharedPref).getApiService();
    }

    /**
     * Lấy FCM token và gửi lên server
     * Gọi sau khi đăng nhập thành công
     */
    public void registerToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);
                    
                    // Lưu token local
                    sharedPref.saveFcmToken(token);
                    
                    // Gửi lên server
                    sendTokenToServer(token);
                });
    }

    /**
     * Gửi FCM token lên server
     */
    public void sendTokenToServer(String token) {
        if (token == null || token.isEmpty()) {
            Log.w(TAG, "FCM token is empty");
            return;
        }

        if (!sharedPref.isLoggedIn()) {
            Log.w(TAG, "User not logged in, skip sending token");
            return;
        }

        Map<String, String> request = new HashMap<>();
        request.put("fcmToken", token);

        apiService.registerFcmToken(request).enqueue(new Callback<ApiService.ApiResponseWrapper<Void>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Void>> call,
                                   Response<ApiService.ApiResponseWrapper<Void>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "FCM token registered successfully");
                } else {
                    Log.e(TAG, "Failed to register FCM token: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Void>> call, Throwable t) {
                Log.e(TAG, "Error registering FCM token", t);
            }
        });
    }

    /**
     * Xóa FCM token khỏi server (khi logout)
     */
    public void unregisterToken() {
        apiService.removeFcmToken().enqueue(new Callback<ApiService.ApiResponseWrapper<Void>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Void>> call,
                                   Response<ApiService.ApiResponseWrapper<Void>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "FCM token removed successfully");
                }
                // Xóa token local
                sharedPref.clearFcmToken();
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Void>> call, Throwable t) {
                Log.e(TAG, "Error removing FCM token", t);
                sharedPref.clearFcmToken();
            }
        });
    }

    /**
     * Gửi lại token đã lưu (nếu có)
     */
    public void resendSavedToken() {
        String savedToken = sharedPref.getFcmToken();
        if (savedToken != null && !savedToken.isEmpty()) {
            sendTokenToServer(savedToken);
        } else {
            registerToken();
        }
    }
}
