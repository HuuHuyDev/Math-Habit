package com.kidsapp.data.repository;

import android.content.Context;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.model.Child;
import com.kidsapp.data.model.Parent;
import com.kidsapp.data.local.SharedPref;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for Parent operations
 */
public class ParentRepository {
    private ApiService apiService;
    private SharedPref sharedPref;

    public ParentRepository(Context context) {
        sharedPref = new SharedPref(context);
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();
    }

    public void getParentProfile(ParentCallback callback) {
        Call<Parent> call = apiService.getParentProfile();
        
        call.enqueue(new Callback<Parent>() {
            @Override
            public void onResponse(Call<Parent> call, Response<Parent> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải thông tin phụ huynh");
                }
            }

            @Override
            public void onFailure(Call<Parent> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getChildren(ChildrenCallback callback) {
        Call<List<Child>> call = apiService.getChildren();
        
        call.enqueue(new Callback<List<Child>>() {
            @Override
            public void onResponse(Call<List<Child>> call, Response<List<Child>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải danh sách trẻ");
                }
            }

            @Override
            public void onFailure(Call<List<Child>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface ParentCallback {
        void onSuccess(Parent parent);
        void onError(String error);
    }

    public interface ChildrenCallback {
        void onSuccess(List<Child> children);
        void onError(String error);
    }
}

