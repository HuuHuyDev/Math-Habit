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
 * Quản lý FCM Token - Gửi lên server khi có token mới
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
     * Đăng ký FCM token (alias cho refreshAndSendToken)
     */
    public void registerToken() {
        refreshAndSendToken();
    }
    
    /**
     * Hủy đăng ký FCM token (alias cho removeTokenFromServer)
     */
    public void unregisterToken() {
        removeTokenFromServer();
    }
    
    /**
     * Lấy FCM token hiện tại và gửi lên server
     */
    public void refreshAndSendToken() {
        if (!sharedPref.isLoggedIn()) {
            Log.d(TAG, "User not logged in, skip sending FCM token");
            return;
        }
        
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    
                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);
                    
                    // Lưu token vào SharedPref
                    sharedPref.saveFcmToken(token);
                    
                    // Gửi token lên server
                    sendTokenToServer(token);
                });
    }
    
    /**
     * Gửi FCM token lên server
     */
    public void sendTokenToServer(String token) {
        if (!sharedPref.isLoggedIn()) {
            Log.d(TAG, "User not logged in, skip sending FCM token");
            return;
        }
        
        try {
            Map<String, String> request = new HashMap<>();
            request.put("fcmToken", token);
            
            Call<ApiService.ApiResponseWrapper<Void>> call = apiService.registerFcmToken(request);
            
            call.enqueue(new Callback<ApiService.ApiResponseWrapper<Void>>() {
                @Override
                public void onResponse(Call<ApiService.ApiResponseWrapper<Void>> call, 
                                     Response<ApiService.ApiResponseWrapper<Void>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().success) {
                        Log.i(TAG, "FCM token sent to server successfully");
                    } else {
                        Log.e(TAG, "Failed to send FCM token: " + response.code());
                    }
                }
                
                @Override
                public void onFailure(Call<ApiService.ApiResponseWrapper<Void>> call, Throwable t) {
                    Log.e(TAG, "Error sending FCM token: " + t.getMessage());
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating FCM token request: " + e.getMessage());
        }
    }
    
    /**
     * Gửi lại FCM token đã lưu (nếu có) lên server
     */
    public void resendSavedToken() {
        if (!sharedPref.isLoggedIn()) {
            Log.d(TAG, "User not logged in, skip resending FCM token");
            return;
        }
        
        String savedToken = sharedPref.getFcmToken();
        if (savedToken != null && !savedToken.isEmpty()) {
            Log.d(TAG, "Resending saved FCM token");
            sendTokenToServer(savedToken);
        } else {
            Log.d(TAG, "No saved FCM token, getting new one");
            refreshAndSendToken();
        }
    }
    
    /**
     * Xóa FCM token khỏi server (khi logout)
     */
    public void removeTokenFromServer() {
        if (!sharedPref.isLoggedIn()) {
            return;
        }
        
        Call<ApiService.ApiResponseWrapper<Void>> call = apiService.removeFcmToken();
        
        call.enqueue(new Callback<ApiService.ApiResponseWrapper<Void>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Void>> call, 
                                 Response<ApiService.ApiResponseWrapper<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    Log.i(TAG, "FCM token removed from server successfully");
                } else {
                    Log.e(TAG, "Failed to remove FCM token: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Void>> call, Throwable t) {
                Log.e(TAG, "Error removing FCM token: " + t.getMessage());
            }
        });
    }
}