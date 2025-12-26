package com.kidsapp.data.repository;

import android.content.Context;
import android.util.Log;

import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.request.CreateChildRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for Child Management (Parent side)
 */
public class ChildRepository {
    private static final String TAG = "ChildRepository";
    private ApiService apiService;
    private SharedPref sharedPref;

    public ChildRepository(Context context) {
        sharedPref = new SharedPref(context);
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();
    }

    /**
     * Lấy danh sách con của phụ huynh
     */
    public void getChildren(ChildListCallback callback) {
        if (apiService == null) {
            callback.onError("API service chưa được khởi tạo");
            return;
        }

        apiService.getParentChildren().enqueue(new Callback<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> call,
                                   Response<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>> wrapper = response.body();
                    if (wrapper.success && wrapper.data != null) {
                        callback.onSuccess(wrapper.data);
                    } else {
                        callback.onError(wrapper.message != null ? wrapper.message : "Lấy danh sách thất bại");
                    }
                } else if (response.code() == 401) {
                    // Token hết hạn - đã được xử lý trong AuthInterceptor
                    callback.onError("Phiên đăng nhập hết hạn, vui lòng đăng nhập lại");
                } else {
                    callback.onError("Lấy danh sách thất bại");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> call, Throwable t) {
                Log.e(TAG, "getChildren failed", t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    /**
     * Lấy thông tin chi tiết của một con
     */
    public void getChildDetail(String childId, ChildCallback callback) {
        if (apiService == null) {
            callback.onError("API service chưa được khởi tạo");
            return;
        }

        apiService.getChildDetail(childId).enqueue(new Callback<ApiService.ApiResponseWrapper<ApiService.ChildResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<ApiService.ChildResponse>> call,
                                   Response<ApiService.ApiResponseWrapper<ApiService.ChildResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponseWrapper<ApiService.ChildResponse> wrapper = response.body();
                    if (wrapper.success && wrapper.data != null) {
                        callback.onSuccess(wrapper.data);
                    } else {
                        callback.onError(wrapper.message != null ? wrapper.message : "Lấy thông tin thất bại");
                    }
                } else {
                    callback.onError("Lấy thông tin thất bại");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<ApiService.ChildResponse>> call, Throwable t) {
                Log.e(TAG, "getChildDetail failed", t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    /**
     * Thêm con mới
     */
    public void createChild(String name, String nickname, String birthDate, Integer grade,
                           String school, String avatarUrl, Boolean gender, String username, String password,
                           ChildCallback callback) {
        if (apiService == null) {
            callback.onError("API service chưa được khởi tạo");
            return;
        }

        CreateChildRequest request = new CreateChildRequest(
                name, nickname, birthDate, grade, school, avatarUrl, gender, username, password
        );

        apiService.createChild(request).enqueue(new Callback<ApiService.ApiResponseWrapper<ApiService.ChildResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<ApiService.ChildResponse>> call,
                                   Response<ApiService.ApiResponseWrapper<ApiService.ChildResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponseWrapper<ApiService.ChildResponse> wrapper = response.body();
                    if (wrapper.success && wrapper.data != null) {
                        callback.onSuccess(wrapper.data);
                    } else {
                        callback.onError(wrapper.message != null ? wrapper.message : "Thêm bé thất bại");
                    }
                } else {
                    String errorMsg = "Thêm bé thất bại";
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                        Log.e(TAG, "createChild error: " + response.code() + " - " + errorBody);
                        
                        // Parse error message from response
                        if (errorBody.contains("\"message\"")) {
                            // Try to extract message
                            int start = errorBody.indexOf("\"message\":\"") + 11;
                            int end = errorBody.indexOf("\"", start);
                            if (start > 10 && end > start) {
                                String msg = errorBody.substring(start, end);
                                if (!msg.isEmpty()) {
                                    errorMsg = msg;
                                }
                            }
                        }
                        
                        // Specific error handling
                        if (response.code() == 400) {
                            if (errorBody.contains("Username đã tồn tại") || errorBody.contains("username")) {
                                errorMsg = "Tên đăng nhập đã tồn tại";
                            } else if (errorBody.contains("Tên không được để trống")) {
                                errorMsg = "Vui lòng nhập tên bé";
                            }
                        } else if (response.code() == 401) {
                            errorMsg = "Phiên đăng nhập hết hạn";
                        } else if (response.code() == 403) {
                            errorMsg = "Không có quyền thực hiện";
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Cannot read error body", e);
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<ApiService.ChildResponse>> call, Throwable t) {
                Log.e(TAG, "createChild failed", t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    /**
     * Cập nhật thông tin con
     */
    public void updateChild(String childId, String name, String nickname, String birthDate,
                           Integer grade, String school, String avatarUrl, Boolean gender,
                           String newPassword, ChildCallback callback) {
        if (apiService == null) {
            callback.onError("API service chưa được khởi tạo");
            return;
        }

        ApiService.UpdateChildRequest request = new ApiService.UpdateChildRequest(
                name, nickname, birthDate, grade, school, avatarUrl, gender, newPassword
        );

        apiService.updateChild(childId, request).enqueue(new Callback<ApiService.ApiResponseWrapper<ApiService.ChildResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<ApiService.ChildResponse>> call,
                                   Response<ApiService.ApiResponseWrapper<ApiService.ChildResponse>> response) {
                Log.d(TAG, "updateChild response: code=" + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponseWrapper<ApiService.ChildResponse> wrapper = response.body();
                    if (wrapper.success && wrapper.data != null) {
                        callback.onSuccess(wrapper.data);
                    } else {
                        callback.onError(wrapper.message != null ? wrapper.message : "Cập nhật thất bại");
                    }
                } else {
                    String errorMsg = "Cập nhật thất bại";
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                        Log.e(TAG, "updateChild error: " + response.code() + " - " + errorBody);
                        
                        if (response.code() == 401) {
                            errorMsg = "Phiên đăng nhập hết hạn";
                        } else if (response.code() == 403) {
                            errorMsg = "Không có quyền thực hiện";
                        } else if (response.code() == 404) {
                            errorMsg = "Không tìm thấy bé";
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Cannot read error body", e);
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<ApiService.ChildResponse>> call, Throwable t) {
                Log.e(TAG, "updateChild failed", t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    /**
     * Xóa con
     */
    public void deleteChild(String childId, SimpleCallback callback) {
        if (apiService == null) {
            callback.onError("API service chưa được khởi tạo");
            return;
        }

        Log.d(TAG, "deleteChild called: childId=" + childId);

        apiService.deleteChild(childId).enqueue(new Callback<ApiService.ApiResponseWrapper<Void>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Void>> call,
                                   Response<ApiService.ApiResponseWrapper<Void>> response) {
                Log.d(TAG, "deleteChild response: code=" + response.code() + ", isSuccessful=" + response.isSuccessful());
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponseWrapper<Void> wrapper = response.body();
                    Log.d(TAG, "deleteChild wrapper: success=" + wrapper.success + ", message=" + wrapper.message);
                    
                    if (wrapper.success) {
                        callback.onSuccess(wrapper.message != null ? wrapper.message : "Xóa thành công");
                    } else {
                        callback.onError(wrapper.message != null ? wrapper.message : "Xóa thất bại");
                    }
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    Log.e(TAG, "deleteChild failed: code=" + response.code() + ", error=" + errorBody);
                    callback.onError("Xóa thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Void>> call, Throwable t) {
                Log.e(TAG, "deleteChild failed", t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // ==================== CHILD PROFILE APIs (for Child user) ====================

    /**
     * Lấy profile của child hiện tại (dùng cho Child user)
     */
    public void getMyProfile(MyProfileCallback callback) {
        if (apiService == null) {
            callback.onError("API service chưa được khởi tạo");
            return;
        }

        apiService.getMyProfile().enqueue(new Callback<ApiService.ApiResponseWrapper<com.kidsapp.data.model.Child>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.model.Child>> call,
                                   Response<ApiService.ApiResponseWrapper<com.kidsapp.data.model.Child>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponseWrapper<com.kidsapp.data.model.Child> wrapper = response.body();
                    if (wrapper.success && wrapper.data != null) {
                        callback.onSuccess(wrapper.data);
                    } else {
                        callback.onError(wrapper.message != null ? wrapper.message : "Lấy profile thất bại");
                    }
                } else {
                    callback.onError("Lấy profile thất bại");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<com.kidsapp.data.model.Child>> call, Throwable t) {
                Log.e(TAG, "getMyProfile failed", t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Callbacks
    public interface ChildListCallback {
        void onSuccess(List<ApiService.ChildResponse> children);
        void onError(String error);
    }

    public interface ChildCallback {
        void onSuccess(ApiService.ChildResponse child);
        void onError(String error);
    }
    
    public interface MyProfileCallback {
        void onSuccess(com.kidsapp.data.model.Child child);
        void onError(String error);
    }

    public interface SimpleCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}
