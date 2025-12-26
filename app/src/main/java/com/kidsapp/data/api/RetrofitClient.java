package com.kidsapp.data.api;

import com.kidsapp.data.api.interceptor.AuthInterceptor;
import com.kidsapp.data.local.SharedPref;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

/**
 * Retrofit Client singleton
 */
public class RetrofitClient {
    private static RetrofitClient instance;
    private Retrofit retrofit;
    private ApiService apiService;
    private SharedPref sharedPref;

    private RetrofitClient(SharedPref sharedPref) {
        this.sharedPref = sharedPref;
        
        try {
            
            // Create logging interceptor
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                android.util.Log.d("OkHttp", message);
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // Create OkHttpClient with interceptors
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(sharedPref))
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            
            // Create Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConfig.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            
            apiService = retrofit.create(ApiService.class);
            android.util.Log.d("RetrofitClient", "Retrofit created successfully!");
        } catch (Exception e) {
            android.util.Log.e("RetrofitClient", "Error creating Retrofit", e);
            e.printStackTrace();
            // Tạo Retrofit đơn giản không có interceptor nếu có lỗi
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
    }

    public static synchronized RetrofitClient getInstance(SharedPref sharedPref) {
        if (instance == null) {
            instance = new RetrofitClient(sharedPref);
        }
        return instance;
    }
    
    /**
     * Reset instance - call this when user logs out
     */
    public static synchronized void resetInstance() {
        instance = null;
    }

    public ApiService getApiService() {
        return apiService;
    }
}

