package com.kidsapp.data.repository;

import android.content.Context;

import com.kidsapp.data.api.ApiConfig;
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
                        android.util.Log.d("AuthRepository", "wrapper.success: " + wrapper.success);
                        android.util.Log.d("AuthRepository", "wrapper.message: " + wrapper.message);
                        android.util.Log.d("AuthRepository", "wrapper.data: " + (wrapper.data != null ? "not null" : "null"));
                        
                        if (wrapper.success && wrapper.data != null) {
                            saveAuthData(wrapper.data);
                            callback.onSuccess(wrapper.data);
                        } else {
                            callback.onError(wrapper.message != null ? wrapper.message : "Đăng nhập thất bại");
                        }
                    } else {
                        // Log error body
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                            android.util.Log.e("AuthRepository", "Error body: " + errorBody);
                        } catch (Exception e) {
                            android.util.Log.e("AuthRepository", "Cannot read error body");
                        }
                        callback.onError("Đăng nhập thất bại. Vui lòng kiểm tra email và mật khẩu.");
                    }
                }

                @Override
                public void onFailure(Call<ApiService.ApiResponseWrapper<ApiService.AuthResponse>> call, Throwable t) {
                    android.util.Log.e("AuthRepository", "Login failed", t);
                    android.util.Log.e("AuthRepository", "Request URL: " + call.request().url());
                    callback.onError("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Không thể kết nối đến server"));
                }
            });
        } catch (Exception e) {
            android.util.Log.e("AuthRepository", "Login exception", e);
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
        
        // Reset RetrofitClient để tạo mới khi đăng nhập lại
        RetrofitClient.resetInstance();
        
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
    
    // ==================== FORGOT PASSWORD ====================
    
    public void forgotPassword(String email, SimpleCallback callback) {
        if (apiService == null) {
            callback.onError("API service chưa được khởi tạo");
            return;
        }
        
        ApiService.ForgotPasswordRequest request = new ApiService.ForgotPasswordRequest(email);
        apiService.forgotPassword(request).enqueue(new Callback<ApiService.ApiResponseWrapper<Void>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Void>> call,
                    Response<ApiService.ApiResponseWrapper<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponseWrapper<Void> wrapper = response.body();
                    if (wrapper.success) {
                        callback.onSuccess(wrapper.message);
                    } else {
                        callback.onError(wrapper.message != null ? wrapper.message : "Gửi OTP thất bại");
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                        if (errorBody.contains("không tồn tại")) {
                            callback.onError("Email không tồn tại trong hệ thống");
                        } else {
                            callback.onError("Gửi OTP thất bại");
                        }
                    } catch (Exception e) {
                        callback.onError("Gửi OTP thất bại");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Void>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void verifyOtp(String email, String otp, SimpleCallback callback) {
        if (apiService == null) {
            callback.onError("API service chưa được khởi tạo");
            return;
        }
        
        ApiService.VerifyOtpRequest request = new ApiService.VerifyOtpRequest(email, otp);
        apiService.verifyOtp(request).enqueue(new Callback<ApiService.ApiResponseWrapper<Boolean>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Boolean>> call,
                    Response<ApiService.ApiResponseWrapper<Boolean>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponseWrapper<Boolean> wrapper = response.body();
                    if (wrapper.success) {
                        callback.onSuccess("OTP hợp lệ");
                    } else {
                        callback.onError(wrapper.message != null ? wrapper.message : "OTP không hợp lệ");
                    }
                } else {
                    callback.onError("OTP không hợp lệ hoặc đã hết hạn");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Boolean>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void resetPassword(String email, String otp, String newPassword, SimpleCallback callback) {
        if (apiService == null) {
            callback.onError("API service chưa được khởi tạo");
            return;
        }
        
        ApiService.ResetPasswordRequest request = new ApiService.ResetPasswordRequest(email, otp, newPassword);
        apiService.resetPassword(request).enqueue(new Callback<ApiService.ApiResponseWrapper<Void>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Void>> call,
                    Response<ApiService.ApiResponseWrapper<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponseWrapper<Void> wrapper = response.body();
                    if (wrapper.success) {
                        callback.onSuccess("Đặt lại mật khẩu thành công");
                    } else {
                        callback.onError(wrapper.message != null ? wrapper.message : "Đặt lại mật khẩu thất bại");
                    }
                } else {
                    callback.onError("Đặt lại mật khẩu thất bại");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Void>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public interface SimpleCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}
