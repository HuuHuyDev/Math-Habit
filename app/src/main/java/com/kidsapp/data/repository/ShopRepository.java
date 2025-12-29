package com.kidsapp.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.PurchasedItem;
import com.kidsapp.data.model.ShopItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository cho Shop - Mua vật phẩm (Avatar, Booster)
 */
public class ShopRepository {
    private static final String TAG = "ShopRepository";
    private final ApiService apiService;

    public ShopRepository(Context context) {
        SharedPref sharedPref = new SharedPref(context);
        this.apiService = RetrofitClient.getInstance(sharedPref).getApiService();
    }

    /**
     * Lấy danh sách vật phẩm trong shop
     */
    public LiveData<List<ShopItem>> getShopItems(String childId, String itemType) {
        MutableLiveData<List<ShopItem>> result = new MutableLiveData<>();
        
        apiService.getShopItems(childId, itemType).enqueue(new Callback<ApiService.ApiResponseWrapper<List<ShopItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<List<ShopItem>>> call,
                                   @NonNull Response<ApiService.ApiResponseWrapper<List<ShopItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    result.setValue(response.body().data);
                } else {
                    Log.e(TAG, "getShopItems failed: " + response.message());
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<List<ShopItem>>> call, @NonNull Throwable t) {
                Log.e(TAG, "getShopItems error: " + t.getMessage());
                result.setValue(null);
            }
        });
        
        return result;
    }

    /**
     * Mua vật phẩm
     */
    public LiveData<PurchasedItem> purchaseItem(String childId, String itemId, PurchaseCallback callback) {
        MutableLiveData<PurchasedItem> result = new MutableLiveData<>();
        
        apiService.purchaseItem(itemId, childId).enqueue(new Callback<ApiService.ApiResponseWrapper<PurchasedItem>>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<PurchasedItem>> call,
                                   @NonNull Response<ApiService.ApiResponseWrapper<PurchasedItem>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    result.setValue(response.body().data);
                    if (callback != null) {
                        callback.onSuccess(response.body().data, response.body().message);
                    }
                } else {
                    String errorMsg = "Mua thất bại";
                    if (response.body() != null && response.body().message != null) {
                        errorMsg = response.body().message;
                    }
                    Log.e(TAG, "purchaseItem failed: " + errorMsg);
                    result.setValue(null);
                    if (callback != null) {
                        callback.onError(errorMsg);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<PurchasedItem>> call, @NonNull Throwable t) {
                Log.e(TAG, "purchaseItem error: " + t.getMessage());
                result.setValue(null);
                if (callback != null) {
                    callback.onError("Lỗi kết nối: " + t.getMessage());
                }
            }
        });
        
        return result;
    }

    /**
     * Lấy danh sách vật phẩm đã mua
     */
    public LiveData<List<PurchasedItem>> getMyItems(String childId, String itemType) {
        MutableLiveData<List<PurchasedItem>> result = new MutableLiveData<>();
        
        apiService.getMyItems(childId, itemType).enqueue(new Callback<ApiService.ApiResponseWrapper<List<PurchasedItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<List<PurchasedItem>>> call,
                                   @NonNull Response<ApiService.ApiResponseWrapper<List<PurchasedItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    result.setValue(response.body().data);
                } else {
                    Log.e(TAG, "getMyItems failed: " + response.message());
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<List<PurchasedItem>>> call, @NonNull Throwable t) {
                Log.e(TAG, "getMyItems error: " + t.getMessage());
                result.setValue(null);
            }
        });
        
        return result;
    }

    /**
     * Trang bị avatar
     */
    public void equipAvatar(String childId, String purchasedItemId, ActionCallback callback) {
        apiService.equipAvatar(purchasedItemId, childId).enqueue(new Callback<ApiService.ApiResponseWrapper<PurchasedItem>>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<PurchasedItem>> call,
                                   @NonNull Response<ApiService.ApiResponseWrapper<PurchasedItem>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    callback.onSuccess(response.body().message);
                } else {
                    String errorMsg = response.body() != null ? response.body().message : "Trang bị thất bại";
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<PurchasedItem>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    /**
     * Kích hoạt booster
     */
    public void activateBooster(String childId, String purchasedItemId, ActionCallback callback) {
        apiService.activateBooster(purchasedItemId, childId).enqueue(new Callback<ApiService.ApiResponseWrapper<PurchasedItem>>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<PurchasedItem>> call,
                                   @NonNull Response<ApiService.ApiResponseWrapper<PurchasedItem>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    callback.onSuccess(response.body().message);
                } else {
                    String errorMsg = response.body() != null ? response.body().message : "Kích hoạt thất bại";
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<PurchasedItem>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Callbacks
    public interface PurchaseCallback {
        void onSuccess(PurchasedItem item, String message);
        void onError(String error);
    }

    public interface ActionCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}
