package com.kidsapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.repository.AuthRepository;

/**
 * ViewModel for Authentication
 */
public class AuthViewModel extends AndroidViewModel {
    private AuthRepository authRepository;
    private MutableLiveData<ApiService.AuthResponse> authResponseLiveData;
    private MutableLiveData<String> errorLiveData;
    private MutableLiveData<Boolean> isLoadingLiveData;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
        authResponseLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
        isLoadingLiveData = new MutableLiveData<>();
    }

    public void login(String email, String password) {
        isLoadingLiveData.setValue(true);
        errorLiveData.setValue(null);
        authResponseLiveData.setValue(null); // Clear previous response
        
        authRepository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(ApiService.AuthResponse response) {
                isLoadingLiveData.postValue(false);
                authResponseLiveData.postValue(response);
            }

            @Override
            public void onError(String error) {
                isLoadingLiveData.postValue(false);
                errorLiveData.postValue(error);
            }
        });
    }

    public void register(String email, String password, String fullName, String role) {
        isLoadingLiveData.setValue(true);
        errorLiveData.setValue(null);
        
        authRepository.register(email, password, fullName, role, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(ApiService.AuthResponse response) {
                isLoadingLiveData.postValue(false);
                authResponseLiveData.postValue(response);
            }

            @Override
            public void onError(String error) {
                isLoadingLiveData.postValue(false);
                errorLiveData.postValue(error);
            }
        });
    }

    public void loginWithGoogle(String accessToken) {
        isLoadingLiveData.setValue(true);
        errorLiveData.setValue(null);
        
        authRepository.loginWithGoogle(accessToken, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(ApiService.AuthResponse response) {
                isLoadingLiveData.postValue(false);
                authResponseLiveData.postValue(response);
            }

            @Override
            public void onError(String error) {
                isLoadingLiveData.postValue(false);
                errorLiveData.postValue(error);
            }
        });
    }

    public void loginWithFacebook(String accessToken) {
        isLoadingLiveData.setValue(true);
        errorLiveData.setValue(null);
        
        authRepository.loginWithFacebook(accessToken, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(ApiService.AuthResponse response) {
                isLoadingLiveData.postValue(false);
                authResponseLiveData.postValue(response);
            }

            @Override
            public void onError(String error) {
                isLoadingLiveData.postValue(false);
                errorLiveData.postValue(error);
            }
        });
    }

    public void logout() {
        authRepository.logout();
    }

    public void clearError() {
        errorLiveData.setValue(null);
    }

    public void clearState() {
        authResponseLiveData.setValue(null);
        errorLiveData.setValue(null);
        isLoadingLiveData.setValue(false);
    }

    public LiveData<ApiService.AuthResponse> getAuthResponse() {
        return authResponseLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }
}
