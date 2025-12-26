package com.kidsapp.data.repository;

import android.content.Context;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.model.Child;
import com.kidsapp.data.local.SharedPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for Child operations
 */
public class ChildRepository {
    private ApiService apiService;
    private SharedPref sharedPref;

    public ChildRepository(Context context) {
        sharedPref = new SharedPref(context);
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();
    }

    public void getChildDetail(String childId, ChildCallback callback) {
        Call<Child> call = apiService.getChildDetail(childId);
        
        call.enqueue(new Callback<Child>() {
            @Override
            public void onResponse(Call<Child> call, Response<Child> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải thông tin trẻ");
                }
            }

            @Override
            public void onFailure(Call<Child> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    /**
     * Lấy thông tin profile của child hiện tại (từ token)
     */
    public void getMyProfile(ChildCallback callback) {
        Call<ApiService.ApiResponseWrapper<Child>> call = apiService.getMyProfile();
        
        call.enqueue(new Callback<ApiService.ApiResponseWrapper<Child>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Child>> call, 
                                   Response<ApiService.ApiResponseWrapper<Child>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    Child child = response.body().data;
                    // Lưu thông tin vào SharedPreferences để dùng offline
                    saveChildToPrefs(child);
                    callback.onSuccess(child);
                } else {
                    callback.onError("Không thể tải thông tin profile");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Child>> call, Throwable t) {
                // Thử load từ cache nếu có lỗi network
                Child cachedChild = getChildFromPrefs();
                if (cachedChild != null) {
                    callback.onSuccess(cachedChild);
                } else {
                    callback.onError(t.getMessage());
                }
            }
        });
    }

    /**
     * Lưu thông tin child vào SharedPreferences
     */
    private void saveChildToPrefs(Child child) {
        sharedPref.saveString("child_id", child.getId());
        sharedPref.saveString("child_user_id", child.getUserId());
        sharedPref.saveString("child_name", child.getName());
        sharedPref.saveString("child_nickname", child.getNickname());
        sharedPref.saveString("child_avatar_url", child.getAvatarUrl());
        sharedPref.saveInt("child_level", child.getLevel());
        sharedPref.saveInt("child_total_points", child.getTotalPoints());
        sharedPref.saveInt("child_grade", child.getGrade() != null ? child.getGrade() : 0);
        sharedPref.saveString("child_school", child.getSchool());
        sharedPref.saveString("child_birth_date", child.getBirthDate());
    }

    /**
     * Lấy thông tin child từ SharedPreferences (cache)
     */
    private Child getChildFromPrefs() {
        String id = sharedPref.getString("child_id", null);
        if (id == null) return null;
        
        Child child = new Child();
        child.setId(id);
        child.setUserId(sharedPref.getString("child_user_id", ""));
        child.setName(sharedPref.getString("child_name", ""));
        child.setNickname(sharedPref.getString("child_nickname", ""));
        child.setAvatarUrl(sharedPref.getString("child_avatar_url", ""));
        child.setLevel(sharedPref.getInt("child_level", 1));
        child.setTotalPoints(sharedPref.getInt("child_total_points", 0));
        child.setGrade(sharedPref.getInt("child_grade", 0));
        child.setSchool(sharedPref.getString("child_school", ""));
        child.setBirthDate(sharedPref.getString("child_birth_date", ""));
        return child;
    }

    public interface ChildCallback {
        void onSuccess(Child child);
        void onError(String error);
    }
}

