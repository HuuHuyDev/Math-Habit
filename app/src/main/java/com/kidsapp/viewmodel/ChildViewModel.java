package com.kidsapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.repository.ChildRepository;

import java.util.List;

/**
 * ViewModel for Child Management
 */
public class ChildViewModel extends AndroidViewModel {
    private ChildRepository childRepository;
    private MutableLiveData<List<ApiService.ChildResponse>> childrenLiveData;
    private MutableLiveData<ApiService.ChildResponse> childDetailLiveData;
    private MutableLiveData<String> errorLiveData;
    private MutableLiveData<String> successLiveData;
    private MutableLiveData<Boolean> isLoadingLiveData;

    public ChildViewModel(@NonNull Application application) {
        super(application);
        childRepository = new ChildRepository(application);
        childrenLiveData = new MutableLiveData<>();
        childDetailLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
        successLiveData = new MutableLiveData<>();
        isLoadingLiveData = new MutableLiveData<>();
    }

    /**
     * Lấy danh sách con
     */
    public void loadChildren() {
        isLoadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        childRepository.getChildren(new ChildRepository.ChildListCallback() {
            @Override
            public void onSuccess(List<ApiService.ChildResponse> children) {
                isLoadingLiveData.postValue(false);
                childrenLiveData.postValue(children);
            }

            @Override
            public void onError(String error) {
                isLoadingLiveData.postValue(false);
                errorLiveData.postValue(error);
            }
        });
    }

    /**
     * Lấy thông tin chi tiết con
     */
    public void loadChildDetail(String childId) {
        isLoadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        childRepository.getChildDetail(childId, new ChildRepository.ChildCallback() {
            @Override
            public void onSuccess(ApiService.ChildResponse child) {
                isLoadingLiveData.postValue(false);
                childDetailLiveData.postValue(child);
            }

            @Override
            public void onError(String error) {
                isLoadingLiveData.postValue(false);
                errorLiveData.postValue(error);
            }
        });
    }

    /**
     * Thêm con mới
     */
    public void createChild(String name, String nickname, String birthDate, Integer grade,
                           String school, String avatarUrl, Boolean gender, String username, String password) {
        isLoadingLiveData.setValue(true);
        errorLiveData.setValue(null);
        successLiveData.setValue(null);

        childRepository.createChild(name, nickname, birthDate, grade, school, avatarUrl, 
                gender, username, password, new ChildRepository.ChildCallback() {
            @Override
            public void onSuccess(ApiService.ChildResponse child) {
                isLoadingLiveData.postValue(false);
                successLiveData.postValue("Thêm bé thành công");
                childDetailLiveData.postValue(child);
                // Reload danh sách
                loadChildren();
            }

            @Override
            public void onError(String error) {
                isLoadingLiveData.postValue(false);
                errorLiveData.postValue(error);
            }
        });
    }

    /**
     * Cập nhật thông tin con
     */
    public void updateChild(String childId, String name, String nickname, String birthDate,
                           Integer grade, String school, String avatarUrl, Boolean gender,
                           String newPassword) {
        isLoadingLiveData.setValue(true);
        errorLiveData.setValue(null);
        successLiveData.setValue(null);

        childRepository.updateChild(childId, name, nickname, birthDate, grade, school, 
                avatarUrl, gender, newPassword, new ChildRepository.ChildCallback() {
            @Override
            public void onSuccess(ApiService.ChildResponse child) {
                isLoadingLiveData.postValue(false);
                successLiveData.postValue("Cập nhật thành công");
                childDetailLiveData.postValue(child);
                // Reload danh sách
                loadChildren();
            }

            @Override
            public void onError(String error) {
                isLoadingLiveData.postValue(false);
                errorLiveData.postValue(error);
            }
        });
    }

    /**
     * Xóa con
     */
    public void deleteChild(String childId) {
        android.util.Log.d("ChildViewModel", "deleteChild called: childId=" + childId);
        
        isLoadingLiveData.setValue(true);
        errorLiveData.setValue(null);
        successLiveData.setValue(null);

        childRepository.deleteChild(childId, new ChildRepository.SimpleCallback() {
            @Override
            public void onSuccess(String message) {
                android.util.Log.d("ChildViewModel", "deleteChild success: " + message);
                isLoadingLiveData.postValue(false);
                successLiveData.postValue(message);
                // Reload danh sách
                loadChildren();
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("ChildViewModel", "deleteChild error: " + error);
                isLoadingLiveData.postValue(false);
                errorLiveData.postValue(error);
            }
        });
    }

    public void clearMessages() {
        errorLiveData.setValue(null);
        successLiveData.setValue(null);
    }

    // Getters
    public LiveData<List<ApiService.ChildResponse>> getChildren() {
        return childrenLiveData;
    }

    public LiveData<ApiService.ChildResponse> getChildDetail() {
        return childDetailLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<String> getSuccess() {
        return successLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }
}
