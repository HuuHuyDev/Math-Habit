package com.kidsapp.data.repository;

import android.content.Context;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.Notification;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Notification operations
 */
public class NotificationRepository {
    
    private final ApiService apiService;
    private final SharedPref sharedPref;

    public NotificationRepository(Context context) {
        sharedPref = new SharedPref(context);
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();
    }

    // Callback interfaces
    public interface NotificationListCallback {
        void onSuccess(List<Notification> notifications);
        void onError(String error);
    }

    public interface UnreadCountCallback {
        void onSuccess(long count);
        void onError(String error);
    }

    public interface ActionCallback {
        void onSuccess();
        void onError(String error);
    }

    /**
     * Lấy tất cả thông báo
     */
    public void getNotifications(NotificationListCallback callback) {
        Call<ApiService.ApiResponseWrapper<List<ApiService.NotificationResponse>>> call = 
            apiService.getNotifications();
        
        call.enqueue(new Callback<ApiService.ApiResponseWrapper<List<ApiService.NotificationResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.NotificationResponse>>> call,
                                 Response<ApiService.ApiResponseWrapper<List<ApiService.NotificationResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    List<Notification> notifications = convertToNotifications(response.body().data);
                    callback.onSuccess(notifications);
                } else {
                    callback.onError("Không thể tải thông báo");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.NotificationResponse>>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
    }

    /**
     * Lấy thông báo chưa đọc
     */
    public void getUnreadNotifications(NotificationListCallback callback) {
        Call<ApiService.ApiResponseWrapper<List<ApiService.NotificationResponse>>> call = 
            apiService.getUnreadNotifications();
        
        call.enqueue(new Callback<ApiService.ApiResponseWrapper<List<ApiService.NotificationResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.NotificationResponse>>> call,
                                 Response<ApiService.ApiResponseWrapper<List<ApiService.NotificationResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    List<Notification> notifications = convertToNotifications(response.body().data);
                    callback.onSuccess(notifications);
                } else {
                    callback.onError("Không thể tải thông báo chưa đọc");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.NotificationResponse>>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
    }

    /**
     * Đếm số thông báo chưa đọc
     */
    public void getUnreadCount(UnreadCountCallback callback) {
        Call<ApiService.ApiResponseWrapper<Long>> call = apiService.getUnreadCount();
        
        call.enqueue(new Callback<ApiService.ApiResponseWrapper<Long>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Long>> call,
                                 Response<ApiService.ApiResponseWrapper<Long>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Không thể tải số thông báo");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Long>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
            }
        });
    }

    /**
     * Đánh dấu đã đọc
     */
    public void markAsRead(String notificationId, ActionCallback callback) {
        Call<ApiService.ApiResponseWrapper<Void>> call = apiService.markNotificationAsRead(notificationId);
        
        call.enqueue(new Callback<ApiService.ApiResponseWrapper<Void>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Void>> call,
                                 Response<ApiService.ApiResponseWrapper<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    if (callback != null) callback.onSuccess();
                } else {
                    if (callback != null) callback.onError("Không thể đánh dấu đã đọc");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Void>> call, Throwable t) {
                if (callback != null) {
                    callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
                }
            }
        });
    }

    /**
     * Đánh dấu tất cả đã đọc
     */
    public void markAllAsRead(ActionCallback callback) {
        Call<ApiService.ApiResponseWrapper<Void>> call = apiService.markAllNotificationsAsRead();
        
        call.enqueue(new Callback<ApiService.ApiResponseWrapper<Void>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Void>> call,
                                 Response<ApiService.ApiResponseWrapper<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    if (callback != null) callback.onSuccess();
                } else {
                    if (callback != null) callback.onError("Không thể đánh dấu tất cả đã đọc");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Void>> call, Throwable t) {
                if (callback != null) {
                    callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
                }
            }
        });
    }

    /**
     * Xóa thông báo
     */
    public void deleteNotification(String notificationId, ActionCallback callback) {
        Call<ApiService.ApiResponseWrapper<Void>> call = apiService.deleteNotification(notificationId);
        
        call.enqueue(new Callback<ApiService.ApiResponseWrapper<Void>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Void>> call,
                                 Response<ApiService.ApiResponseWrapper<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    if (callback != null) callback.onSuccess();
                } else {
                    if (callback != null) callback.onError("Không thể xóa thông báo");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Void>> call, Throwable t) {
                if (callback != null) {
                    callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi kết nối");
                }
            }
        });
    }

    /**
     * Convert từ API Response sang Notification model
     */
    private List<Notification> convertToNotifications(List<ApiService.NotificationResponse> responses) {
        List<Notification> notifications = new ArrayList<>();
        
        if (responses != null) {
            for (ApiService.NotificationResponse response : responses) {
                Notification notification = new Notification();
                notification.setId(response.id);
                notification.setType(response.type);
                notification.setTitle(response.title);
                notification.setMessage(response.message);
                notification.setRead(response.isRead);
                notification.setReadAt(response.readAt);
                notification.setReferenceId(response.referenceId);
                notification.setReferenceType(response.referenceType);
                notification.setIconUrl(response.iconUrl);
                notification.setCreatedAt(response.createdAt);
                notification.setTimeAgo(response.timeAgo);
                
                notifications.add(notification);
            }
        }
        
        return notifications;
    }
}
