package com.kidsapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel cho Parent Home
 */
public class HomeViewModel extends AndroidViewModel {
    private ApiService apiService;
    private SharedPref sharedPref;
    
    private MutableLiveData<List<ApiService.ChildResponse>> childrenLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ApiService.ActivityLogResponse>> activitiesLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        sharedPref = new SharedPref(application);
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();
    }

    /**
     * Load danh sách con
     */
    public void loadChildren() {
        isLoadingLiveData.setValue(true);
        
        apiService.getParentChildren().enqueue(new Callback<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> call,
                                   Response<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> response) {
                isLoadingLiveData.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>> wrapper = response.body();
                    if (wrapper.success && wrapper.data != null) {
                        childrenLiveData.postValue(wrapper.data);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> call, Throwable t) {
                isLoadingLiveData.postValue(false);
                errorLiveData.postValue("Lỗi tải danh sách con: " + t.getMessage());
            }
        });
    }

    /**
     * Load hoạt động gần đây
     */
    public void loadActivities(int limit) {
        apiService.getActivities(limit).enqueue(new Callback<ApiService.ApiResponseWrapper<List<ApiService.ActivityLogResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.ActivityLogResponse>>> call,
                                   Response<ApiService.ApiResponseWrapper<List<ApiService.ActivityLogResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponseWrapper<List<ApiService.ActivityLogResponse>> wrapper = response.body();
                    if (wrapper.success && wrapper.data != null) {
                        activitiesLiveData.postValue(wrapper.data);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.ActivityLogResponse>>> call, Throwable t) {
                // Không hiển thị lỗi cho activities, chỉ log
                android.util.Log.e("HomeViewModel", "Load activities failed", t);
            }
        });
    }

    /**
     * Load tất cả data cho Home
     */
    public void loadHomeData() {
        loadChildren();
        loadActivities(20);
    }

    // Getters
    public LiveData<List<ApiService.ChildResponse>> getChildren() {
        return childrenLiveData;
    }

    public LiveData<List<ApiService.ActivityLogResponse>> getActivities() {
        return activitiesLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }
}
