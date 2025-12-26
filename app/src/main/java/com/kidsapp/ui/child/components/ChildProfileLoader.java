package com.kidsapp.ui.child.components;

import android.content.Context;
import com.kidsapp.R;
import com.kidsapp.data.model.Child;
import com.kidsapp.data.repository.ChildRepository;
import com.kidsapp.data.repository.NotificationRepository;

/**
 * Helper class để load child profile và fill vào header
 * Tái sử dụng ở nhiều fragment
 */
public class ChildProfileLoader {
    
    private final Context context;
    private final ChildRepository childRepository;
    private final NotificationRepository notificationRepository;
    private Child cachedChild;
    
    public interface ProfileLoadListener {
        void onProfileLoaded(Child child);
        void onProfileLoadError(String error);
    }
    
    public ChildProfileLoader(Context context) {
        this.context = context;
        this.childRepository = new ChildRepository(context);
        this.notificationRepository = new NotificationRepository(context);
    }
    
    /**
     * Load profile và fill vào HeaderUserView
     */
    public void loadAndFillHeader(HeaderUserView headerView, ProfileLoadListener listener) {
        // Nếu đã có cache, dùng luôn
        if (cachedChild != null) {
            fillHeader(headerView, cachedChild);
            if (listener != null) {
                listener.onProfileLoaded(cachedChild);
            }
            // Vẫn load notification count
            loadNotificationCount(headerView);
            return;
        }
        
        // Load từ API
        childRepository.getMyProfile(new ChildRepository.MyProfileCallback() {
            @Override
            public void onSuccess(Child child) {
                cachedChild = child;
                fillHeader(headerView, child);
                
                // Load notification count
                loadNotificationCount(headerView);
                
                if (listener != null) {
                    listener.onProfileLoaded(child);
                }
            }

            @Override
            public void onError(String error) {
                // Fill dữ liệu mặc định
                headerView.setUserName("Bé yêu");
                headerView.setAvatar(R.drawable.ic_child_face);
                
                // Vẫn cố load notification count
                loadNotificationCount(headerView);
                
                if (listener != null) {
                    listener.onProfileLoadError(error);
                }
            }
        });
    }
    
    /**
     * Fill dữ liệu vào header
     */
    private void fillHeader(HeaderUserView headerView, Child child) {
        String displayName = child.getDisplayName();
        headerView.setUserName(displayName);
        
        // TODO: Load avatar từ URL nếu có
        // if (child.getAvatarUrl() != null && !child.getAvatarUrl().isEmpty()) {
        //     headerView.setAvatarUrl(child.getAvatarUrl());
        // } else {
            headerView.setAvatar(R.drawable.ic_child_face);
        // }
    }
    
    /**
     * Load số thông báo chưa đọc và hiển thị badge
     */
    public void loadNotificationCount(HeaderUserView headerView) {
        notificationRepository.getUnreadCount(new NotificationRepository.UnreadCountCallback() {
            @Override
            public void onSuccess(long count) {
                headerView.setNotificationCount((int) count);
            }

            @Override
            public void onError(String error) {
                // Không hiển thị badge nếu lỗi
                headerView.setNotificationCount(0);
            }
        });
    }
    
    /**
     * Lấy cached child profile
     */
    public Child getCachedChild() {
        return cachedChild;
    }
    
    /**
     * Clear cache (dùng khi logout hoặc switch user)
     */
    public void clearCache() {
        cachedChild = null;
    }
}
