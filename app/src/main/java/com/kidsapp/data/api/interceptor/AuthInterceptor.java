package com.kidsapp.data.api.interceptor;

import com.kidsapp.data.local.SharedPref;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

/**
 * Interceptor to add authentication token to API requests
 */
public class AuthInterceptor implements Interceptor {
    private SharedPref sharedPref;
    
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
            return chain.proceed(originalRequest);
        }
        
        // Get token from SharedPreferences
        String token = sharedPref.getAuthToken();
        
        if (token != null && !token.isEmpty()) {
            // Add Authorization header
            Request.Builder requestBuilder = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json");
            
            return chain.proceed(requestBuilder.build());
        }
        
        return chain.proceed(originalRequest);
    }
    
    private boolean isPublicEndpoint(String path) {
        for (String endpoint : PUBLIC_ENDPOINTS) {
            if (path.contains(endpoint)) {
                return true;
            }
        }
        return false;
    }
}

