package com.kidsapp.ui.child.home;

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
import com.kidsapp.data.model.Child;
import com.kidsapp.databinding.BottomsheetNotificationsBinding;
import com.kidsapp.databinding.FragmentChildHomeBinding;
import com.kidsapp.ui.child.challenge.ChallengeHomeFragment;
import com.kidsapp.ui.child.components.ChildProfileLoader;
import com.kidsapp.ui.child.progress.ProgresssFragment;
import com.kidsapp.ui.child.shop.ShopFragment;
import com.kidsapp.ui.child.task.ChildTaskListFragment;
import com.kidsapp.ui.components.LoadingDialog;
import com.kidsapp.ui.parent.home.adapter.NotificationAdapter;
import com.kidsapp.ui.parent.home.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class ChildHomeFragment extends Fragment {
    private FragmentChildHomeBinding binding;
    private ChildProfileLoader profileLoader;
    private LoadingDialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChildHomeBinding.inflate(inflater, container, false);
        
        // Khởi tạo profile loader và loading dialog
        profileLoader = new ChildProfileLoader(requireContext());
        loadingDialog = new LoadingDialog(requireContext());
        
        // Load profile từ API
        showLoading();
        loadChildProfile();
        
        setupLevelCard();
        setupClickListeners();
        
        return binding.getRoot();
    }
    
    private void showLoading() {
        if (loadingDialog != null) {
            loadingDialog.show("Đang tải...");
        }
    }
    
    private void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    /**
     * Load profile từ API và fill vào header
     */
    private void loadChildProfile() {
        profileLoader.loadAndFillHeader(binding.headerUser, new ChildProfileLoader.ProfileLoadListener() {
            @Override
            public void onProfileLoaded(Child child) {
                if (getActivity() == null) return;
                hideLoading();
                
                // Cập nhật level card với dữ liệu thực
                updateLevelCard(child);
            }

            @Override
            public void onProfileLoadError(String error) {
                hideLoading();
                // Dữ liệu mặc định đã được fill bởi loader
            }
        });
    }

    /**
     * Cập nhật level card với dữ liệu từ API
     */
    private void updateLevelCard(Child child) {
        int level = child.getCurrentLevel() > 0 ? child.getCurrentLevel() : child.getLevel();
        int currentExp = child.getTotalXp();
        int xpToNext = child.getXpToNextLevel() > 0 ? child.getXpToNextLevel() : 100;
        
        // Tính XP trong level hiện tại
        int currentLevelExp = currentExp % xpToNext;
        int remainExp = xpToNext - currentLevelExp;
        int percent = xpToNext > 0 ? (currentLevelExp * 100) / xpToNext : 0;

        binding.txtLevelLabel.setText(getString(R.string.child_level_value, level));
        binding.txtExp.setText(currentLevelExp + " / " + xpToNext + " XP");
        binding.txtExpPercent.setText(percent + "%");
        binding.progressExp.setProgress(percent);
        binding.txtExpHint.setText("Chỉ còn " + remainExp + " XP nữa để lên cấp!");
        
        // Cập nhật Today Goal card
        updateTodayGoalCard(child);
    }
    
    /**
     * Cập nhật Today Goal card với dữ liệu từ API
     */
    private void updateTodayGoalCard(Child child) {
        // Daily progress (0-100)
        float dailyProgress = child.getDailyProgress();
        int progressPercent = Math.round(dailyProgress);
        
        // Đảm bảo progress trong khoảng 0-100
        if (progressPercent < 0) progressPercent = 0;
        if (progressPercent > 100) progressPercent = 100;
        
        // Tasks completed today (not total tasks)
        int dailyGoalTasks = child.getDailyGoalExercises() > 0 ? child.getDailyGoalExercises() : 5;
        int tasksCompletedToday;
        
        if (dailyProgress > 0) {
            // Tính dựa trên dailyProgress
            tasksCompletedToday = Math.round((dailyProgress / 100.0f) * dailyGoalTasks);
        } else {
            // Fallback: Dùng totalTasksCompleted nếu dailyProgress = 0
            // Giả sử trong ngày hoàn thành ít nhất 1 task nếu có totalTasksCompleted
            tasksCompletedToday = child.getTotalTasksCompleted() > 0 ? 
                Math.min(child.getTotalTasksCompleted(), dailyGoalTasks) : 0;
        }
        
        // Đảm bảo không vượt quá mục tiêu
        if (tasksCompletedToday > dailyGoalTasks) {
            tasksCompletedToday = dailyGoalTasks;
        }
        
        // XP earned today
        int xpToday = 0;
        
        // Ưu tiên dùng dữ liệu từ API nếu có
        if (child.getXpEarnedToday() > 0) {
            xpToday = child.getXpEarnedToday();
        } else if (tasksCompletedToday > 0) {
            // Estimate dựa trên số task hoàn thành
            int avgXpPerTask = 15; // Giả sử mỗi task trung bình 15 XP
            xpToday = tasksCompletedToday * avgXpPerTask;
        }
        
        // Streak
        int streak = child.getCurrentStreak();
        
        // Update UI
        binding.txtGoalPercent.setText(progressPercent + "%");
        binding.progressTodayGoal.setProgress(progressPercent);
        binding.txtTasksCompleted.setText(tasksCompletedToday + "/" + dailyGoalTasks);
        binding.txtXpEarnedToday.setText(String.valueOf(xpToday));
        binding.txtStreak.setText(String.valueOf(streak));
        
        // Debug log
        android.util.Log.d("ChildHome", String.format(
            "Daily Progress: %.1f%%, Tasks: %d/%d, XP Today: %d, Streak: %d",
            dailyProgress, tasksCompletedToday, dailyGoalTasks, xpToday, streak
        ));
    }

    private void setupHeader() {
        // Notification count - TODO: Lấy từ API
        binding.headerUser.setNotificationCount(3);
        
        binding.headerUser.setAvatarClick(v -> navigateToProfile());
        binding.headerUser.setNotificationClick(v -> showNotificationsBottomSheet());
    }

    private void setupLevelCard() {
        // Dữ liệu mặc định, sẽ được cập nhật khi load profile thành công
        int level = 1;
        int currentExp = 0;
        int targetExp = 100;
        int remainExp = targetExp - currentExp;
        int percent = 0;

        binding.txtLevelLabel.setText(getString(R.string.child_level_value, level));
        binding.txtExp.setText(currentExp + " / " + targetExp + " XP");
        binding.txtExpPercent.setText(percent + "%");
        binding.progressExp.setProgress(percent);
        binding.txtExpHint.setText("Chỉ còn " + remainExp + " XP nữa để lên cấp!");
        
        // Default today goal
        binding.txtGoalPercent.setText("0%");
        binding.progressTodayGoal.setProgress(0);
        binding.txtTasksCompleted.setText("0/5");
        binding.txtXpEarnedToday.setText("0");
        binding.txtStreak.setText("0");
    }

    private void setupClickListeners() {
        // Setup header listeners
        setupHeader();
        
        // Cards
        binding.cardLevel.setOnClickListener(v -> navigateToProgress());
        binding.cardTodayGoal.setOnClickListener(v -> navigateToTaskList());
        binding.cardMission.setOnClickListener(v -> navigateToTaskList());
        
        // Action buttons
        binding.cardStore.setOnClickListener(v -> navigateToShop());
        binding.cardAchievement.setOnClickListener(v -> navigateToAchievement());
        binding.cardChallenge.setOnClickListener(v -> navigateToChallenge());
    }

    // Navigation methods
    private void navigateToTaskList() {
        navigateTo(new ChildTaskListFragment());
    }

    private void navigateToShop() {
        navigateTo(new ShopFragment());
    }

    private void navigateToProgress() {
        navigateTo(new ProgresssFragment());
    }

    private void navigateToAchievement() {
        navigateTo(new com.kidsapp.ui.child.reward.RewardFragment());
    }

    private void navigateToChallenge() {
        navigateTo(new ChallengeHomeFragment());
    }

    private void navigateToProfile() {
        navigateTo(new com.kidsapp.ui.child.profile.ChildProfileFragment());
    }

    private void navigateTo(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.childHomeHost, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

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
                
                // Convert sang model của UI (nếu cần)
                List<com.kidsapp.ui.parent.home.model.Notification> uiNotifications = 
                    convertToUINotifications(notifications);
                
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
    private List<com.kidsapp.ui.parent.home.model.Notification> convertToUINotifications(
            List<com.kidsapp.data.model.Notification> apiNotifications) {
        List<com.kidsapp.ui.parent.home.model.Notification> uiNotifications = new ArrayList<>();
        
        for (com.kidsapp.data.model.Notification apiNotif : apiNotifications) {
            // Map API notification fields to UI notification constructor
            // Constructor: (id, childName, childAvatar, taskTitle, taskType, time, isRead)
            com.kidsapp.ui.parent.home.model.Notification uiNotif = 
                new com.kidsapp.ui.parent.home.model.Notification(
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
        hideLoading();
        loadingDialog = null;
        binding = null;
    }
}
