package com.kidsapp.data.repository;

import android.content.Context;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for Authentication
 */
public class AuthRepository {
    private ApiService apiService;
    private SharedPref sharedPref;

    public AuthRepository(Context context) {
        try {
            sharedPref = new SharedPref(context);
            apiService = RetrofitClient.getInstance(sharedPref).getApiService();
        } catch (Exception e) {
            e.printStackTrace();
            sharedPref = new SharedPref(context);
        }
    }

    public void login(String email, String password, AuthCallback callback) {
        if (apiService == null) {
            callback.onError("API service chưa được khởi tạo. Vui lòng kiểm tra kết nối mạng.");
            return;
        }
        
        try {
            ApiService.LoginRequest request = new ApiService.LoginRequest(email, password);
            Call<ApiService.ApiResponseWrapper<ApiService.AuthResponse>> call = apiService.login(request);
            
            call.enqueue(new Callback<ApiService.ApiResponseWrapper<ApiService.AuthResponse>>() {
                @Override
                public void onResponse(Call<ApiService.ApiResponseWrapper<ApiService.AuthResponse>> call, 
                        Response<ApiService.ApiResponseWrapper<ApiService.AuthResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiService.ApiResponseWrapper<ApiService.AuthResponse> wrapper = response.body();
                        if (wrapper.success && wrapper.data != null) {
                            saveAuthData(wrapper.data);
                            callback.onSuccess(wrapper.data);
                        } else {
                            callback.onError(wrapper.message != null ? wrapper.message : "Đăng nhập thất bại");
                        }
                    } else {
                        callback.onError("Đăng nhập thất bại. Vui lòng kiểm tra email và mật khẩu.");
                    }
                }

                @Override
                public void onFailure(Call<ApiService.ApiResponseWrapper<ApiService.AuthResponse>> call, Throwable t) {
                    callback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Không thể kết nối đến server"));
                }
            });
        } catch (Exception e) {
            callback.onError("Lỗi: " + e.getMessage());
        }
    }

    public void register(String email, String password, String fullName, String role, AuthCallback callback) {
        if (apiService == null) {
            callback.onError("API service chưa được khởi tạo. Vui lòng kiểm tra kết nối mạng.");
            return;
        }
        
        try {
            ApiService.RegisterRequest request = new ApiService.RegisterRequest(email, password, fullName, role);
            Call<ApiService.ApiResponseWrapper<ApiService.AuthResponse>> call = apiService.register(request);
            
            call.enqueue(new Callback<ApiService.ApiResponseWrapper<ApiService.AuthResponse>>() {
                @Override
                public void onResponse(Call<ApiService.ApiResponseWrapper<ApiService.AuthResponse>> call, 
                        Response<ApiService.ApiResponseWrapper<ApiService.AuthResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiService.ApiResponseWrapper<ApiService.AuthResponse> wrapper = response.body();
                        if (wrapper.success && wrapper.data != null) {
                            saveAuthData(wrapper.data);
                            callback.onSuccess(wrapper.data);
                        } else {
                            callback.onError(wrapper.message != null ? wrapper.message : "Đăng ký thất bại");
                        }
                    } else {
                        callback.onError("Đăng ký thất bại. Email có thể đã được sử dụng.");
                    }
                }

                @Override
                public void onFailure(Call<ApiService.ApiResponseWrapper<ApiService.AuthResponse>> call, Throwable t) {
                    callback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Không thể kết nối đến server"));
                }
            });
        } catch (Exception e) {
            callback.onError("Lỗi: " + e.getMessage());
        }
    }

    public void loginWithGoogle(String accessToken, AuthCallback callback) {
        if (apiService == null) {
            callback.onError("API service chưa được khởi tạo. Vui lòng kiểm tra kết nối mạng.");
            return;
        }
        
        try {
            ApiService.SocialLoginRequest request = new ApiService.SocialLoginRequest(accessToken);
            Call<ApiService.ApiResponseWrapper<ApiService.AuthResponse>> call = apiService.loginWithGoogle(request);
            
            call.enqueue(new Callback<ApiService.ApiResponseWrapper<ApiService.AuthResponse>>() {
                @Override
                public void onResponse(Call<ApiService.ApiResponseWrapper<ApiService.AuthResponse>> call, 
                        Response<ApiService.ApiResponseWrapper<ApiService.AuthResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiService.ApiResponseWrapper<ApiService.AuthResponse> wrapper = response.body();
                        if (wrapper.success && wrapper.data != null) {
                            saveAuthData(wrapper.data);
                            callback.onSuccess(wrapper.data);
                        } else {
                            callback.onError(wrapper.message != null ? wrapper.message : "Đăng nhập Google thất bại");
                        }
                    } else {
                        callback.onError("Đăng nhập Google thất bại");
                    }
                }

                @Override
                public void onFailure(Call<ApiService.ApiResponseWrapper<ApiService.AuthResponse>> call, Throwable t) {
                    callback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Không thể kết nối đến server"));
                }
            });
        } catch (Exception e) {
            callback.onError("Lỗi: " + e.getMessage());
        }
    }

    public void loginWithFacebook(String accessToken, AuthCallback callback) {
        if (apiService == null) {
            callback.onError("API service chưa được khởi tạo. Vui lòng kiểm tra kết nối mạng.");
            return;
        }
        
        try {
            ApiService.SocialLoginRequest request = new ApiService.SocialLoginRequest(accessToken);
            Call<ApiService.ApiResponseWrapper<ApiService.AuthResponse>> call = apiService.loginWithFacebook(request);
            
            call.enqueue(new Callback<ApiService.ApiResponseWrapper<ApiService.AuthResponse>>() {
                @Override
                public void onResponse(Call<ApiService.ApiResponseWrapper<ApiService.AuthResponse>> call, 
                        Response<ApiService.ApiResponseWrapper<ApiService.AuthResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiService.ApiResponseWrapper<ApiService.AuthResponse> wrapper = response.body();
                        if (wrapper.success && wrapper.data != null) {
                            saveAuthData(wrapper.data);
                            callback.onSuccess(wrapper.data);
                        } else {
                            callback.onError(wrapper.message != null ? wrapper.message : "Đăng nhập Facebook thất bại");
                        }
                    } else {
                        callback.onError("Đăng nhập Facebook thất bại");
                    }
                }

                @Override
                public void onFailure(Call<ApiService.ApiResponseWrapper<ApiService.AuthResponse>> call, Throwable t) {
                    callback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Không thể kết nối đến server"));
                }
            });
        } catch (Exception e) {
            callback.onError("Lỗi: " + e.getMessage());
        }
    }

    private void saveAuthData(ApiService.AuthResponse authResponse) {
        if (authResponse.accessToken != null) {
            sharedPref.saveAuthToken(authResponse.accessToken);
        }
        if (authResponse.refreshToken != null) {
            sharedPref.saveRefreshToken(authResponse.refreshToken);
        }
        if (authResponse.user != null) {
            if (authResponse.user.id != null) {
                sharedPref.saveUserId(authResponse.user.id);
            }
            if (authResponse.user.email != null) {
                sharedPref.saveUserEmail(authResponse.user.email);
            }
            if (authResponse.user.fullName != null) {
                sharedPref.saveUserName(authResponse.user.fullName);
            }
            if (authResponse.user.role != null) {
                sharedPref.saveUserRole(authResponse.user.role);
            }
            // Save child ID if user is a child
            if (authResponse.user.childProfile != null && authResponse.user.childProfile.id != null) {
                sharedPref.saveChildId(authResponse.user.childProfile.id);
            }
        }
        sharedPref.setLoggedIn(true);
    }

    public void logout() {
        logout(null);
    }

    public void logout(LogoutCallback callback) {
        String refreshToken = sharedPref.getRefreshToken();
        
        // Clear local data first
        sharedPref.clearAll();
        
        if (apiService != null && refreshToken != null && !refreshToken.isEmpty()) {
            apiService.logout(refreshToken).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Still consider logout successful since local data is cleared
                    if (callback != null) {
                        callback.onSuccess();
                    }
                }
            });
        } else {
            if (callback != null) {
                callback.onSuccess();
            }
        }
    }

    public interface AuthCallback {
        void onSuccess(ApiService.AuthResponse response);
        void onError(String error);
    }

    public interface LogoutCallback {
        void onSuccess();
    }
}
