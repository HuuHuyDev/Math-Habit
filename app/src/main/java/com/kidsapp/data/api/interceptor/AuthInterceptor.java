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

    public AuthInterceptor(SharedPref sharedPref) {
        this.sharedPref = sharedPref;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
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
}

