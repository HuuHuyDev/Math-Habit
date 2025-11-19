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
        authRepository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(ApiService.AuthResponse response) {
                isLoadingLiveData.setValue(false);
                authResponseLiveData.setValue(response);
            }

            @Override
            public void onError(String error) {
                isLoadingLiveData.setValue(false);
                errorLiveData.setValue(error);
            }
        });
    }

    public void logout() {
        authRepository.logout();
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

