package com.kidsapp.ui.child.components;

import android.content.Context;
import com.kidsapp.R;
import com.kidsapp.data.model.Child;
import com.kidsapp.data.repository.ChildRepository;

/**
 * Helper class để load child profile và fill vào header
 * Tái sử dụng ở nhiều fragment
 */
public class ChildProfileLoader {
    
    private final Context context;
    private final ChildRepository childRepository;
    private Child cachedChild;
    
    public interface ProfileLoadListener {
        void onProfileLoaded(Child child);
        void onProfileLoadError(String error);
    }
    
    public ChildProfileLoader(Context context) {
        this.context = context;
        this.childRepository = new ChildRepository(context);
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
            return;
        }
        
        // Load từ API
        childRepository.getMyProfile(new ChildRepository.ChildCallback() {
            @Override
            public void onSuccess(Child child) {
                cachedChild = child;
                fillHeader(headerView, child);
                if (listener != null) {
                    listener.onProfileLoaded(child);
                }
            }

            @Override
            public void onError(String error) {
                // Fill dữ liệu mặc định
                headerView.setUserName("Bé yêu");
                headerView.setAvatar(R.drawable.ic_child_face);
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
