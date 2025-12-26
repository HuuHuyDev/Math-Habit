package com.kidsapp.ui.child.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kidsapp.R;
import com.kidsapp.databinding.BottomsheetNotificationsBinding;
import com.kidsapp.databinding.FragmentTaskListBinding;
import com.kidsapp.ui.child.components.ChildProfileLoader;
import com.kidsapp.ui.parent.home.adapter.NotificationAdapter;
import com.kidsapp.ui.parent.home.model.Notification;

import java.util.List;

/**
 * Child Task List Fragment
 */
public class ChildTaskListFragment extends Fragment {
    private FragmentTaskListBinding binding;
    private ChildProfileLoader profileLoader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskListBinding.inflate(inflater, container, false);
        
        profileLoader = new ChildProfileLoader(requireContext());
        
        setupHeader();
        setupBackButton();
        setupRecyclerView();
        setupTabLayout();
        return binding.getRoot();
    }

    private void setupHeader() {
        // Load profile từ API và fill vào header
        profileLoader.loadAndFillHeader(binding.headerUser, null);
        
        // Click vào chuông để xem thông báo từ parent
        binding.headerUser.setNotificationClick(v -> showNotificationsBottomSheet());
    }

    private void setupBackButton() {
        binding.layoutBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupRecyclerView() {
        // ViewPager2 sẽ được setup trong setupTabLayout
    }

    /**
     * Setup TabLayout với 3 tabs
     * - Công việc (Việc nhà + Cá nhân)
     * - Bài tập
     * - Lịch sử
     */
    private void setupTabLayout() {
        // Setup ViewPager2 với adapter
        TaskPagerAdapter pagerAdapter = new TaskPagerAdapter(requireActivity());
        binding.viewPager.setAdapter(pagerAdapter);
        
        // Kết nối TabLayout với ViewPager2 và set text cho từng tab
        new com.google.android.material.tabs.TabLayoutMediator(
                binding.tabLayout,
                binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Công việc"); // Gộp Việc nhà + Cá nhân
                            break;
                        case 1:
                            tab.setText(R.string.task_tab_exercise); // Bài tập
                            break;
                        case 2:
                            tab.setText(R.string.task_tab_history); // Lịch sử
                            break;
                    }
                }
        ).attach();
        
        // Select "Công việc" tab (index 0) by default
        if (binding.tabLayout.getTabCount() > 0) {
            binding.tabLayout.getTabAt(0).select();
        }
    }

    /**
     * Hiển thị BottomSheet danh sách thông báo từ parent
     */
    private void showNotificationsBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        BottomsheetNotificationsBinding sheetBinding = BottomsheetNotificationsBinding.inflate(getLayoutInflater());

        // ✅ Load thông báo từ API thay vì fake data
        com.kidsapp.data.repository.NotificationRepository notificationRepo = 
            new com.kidsapp.data.repository.NotificationRepository(requireContext());
        
        notificationRepo.getNotifications(new com.kidsapp.data.repository.NotificationRepository.NotificationListCallback() {
            @Override
            public void onSuccess(List<com.kidsapp.data.model.Notification> notifications) {
                if (getActivity() == null) return;
                
                // Convert sang model của UI
                List<Notification> uiNotifications = convertToUINotifications(notifications);
                
                NotificationAdapter adapter = new NotificationAdapter();
                adapter.setNotifications(uiNotifications);
                adapter.setOnNotificationClickListener((notification, position) -> {
                    // Đánh dấu đã đọc
                    String notifId = notifications.get(position).getId();
                    notificationRepo.markAsRead(notifId, new com.kidsapp.data.repository.NotificationRepository.ActionCallback() {
                        @Override
                        public void onSuccess() {
                            // Reload notification count
                            profileLoader.loadNotificationCount(binding.headerUser);
                        }

                        @Override
                        public void onError(String error) {
                            // Ignore error
                        }
                    });
                    
                    // Xử lý click notification
                    handleNotificationClick(notifications.get(position));
                });

                sheetBinding.recyclerNotifications.setAdapter(adapter);
                
                // Show/hide empty state
                boolean isEmpty = notifications.isEmpty();
                sheetBinding.layoutEmptyNotifications.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                sheetBinding.recyclerNotifications.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;
                Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                
                // Hiển thị empty state
                sheetBinding.layoutEmptyNotifications.setVisibility(View.VISIBLE);
                sheetBinding.recyclerNotifications.setVisibility(View.GONE);
            }
        });

        // Đánh dấu tất cả đã đọc
        sheetBinding.txtMarkAllRead.setOnClickListener(v -> {
            notificationRepo.markAllAsRead(new com.kidsapp.data.repository.NotificationRepository.ActionCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(requireContext(), "Đã đánh dấu tất cả là đã đọc", Toast.LENGTH_SHORT).show();
                    // Reload notification count
                    profileLoader.loadNotificationCount(binding.headerUser);
                    dialog.dismiss();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.setContentView(sheetBinding.getRoot());
        dialog.show();
    }
    
    /**
     * Convert từ API Notification sang UI Notification model
     */
    private List<Notification> convertToUINotifications(
            List<com.kidsapp.data.model.Notification> apiNotifications) {
        List<Notification> uiNotifications = new java.util.ArrayList<>();
        
        for (com.kidsapp.data.model.Notification apiNotif : apiNotifications) {
            // Map API notification fields to UI notification constructor
            // Constructor: (id, childName, childAvatar, taskTitle, taskType, time, isRead)
            Notification uiNotif = new Notification(
                apiNotif.getId(),
                apiNotif.getTitle(),           // childName -> title
                "",                             // childAvatar -> empty (không cần cho child)
                apiNotif.getMessage(),          // taskTitle -> message
                apiNotif.getType(),             // taskType -> type
                apiNotif.getTimeAgo(),          // time -> timeAgo
                apiNotif.isRead()               // isRead
            );
            uiNotifications.add(uiNotif);
        }
        
        return uiNotifications;
    }
    
    /**
     * Xử lý khi click vào notification
     */
    private void handleNotificationClick(com.kidsapp.data.model.Notification notification) {
        // Hiển thị message
        Toast.makeText(requireContext(), notification.getMessage(), Toast.LENGTH_SHORT).show();
        
        // TODO: Navigate dựa vào referenceType
        // if ("TASK".equals(notification.getReferenceType())) {
        //     // Navigate to task detail
        // } else if ("CHALLENGE".equals(notification.getReferenceType())) {
        //     // Navigate to challenge
        // }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

