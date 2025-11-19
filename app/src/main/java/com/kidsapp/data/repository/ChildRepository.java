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

    public interface ChildCallback {
        void onSuccess(Child child);
        void onError(String error);
    }
}

