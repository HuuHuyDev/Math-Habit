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
import com.kidsapp.data.FakeNotificationRepository;
import com.kidsapp.data.model.Child;
import com.kidsapp.databinding.BottomsheetNotificationsBinding;
import com.kidsapp.databinding.FragmentChildHomeBinding;
import com.kidsapp.ui.child.challenge.ChallengeHomeFragment;
import com.kidsapp.ui.child.components.ChildProfileLoader;
import com.kidsapp.ui.child.progress.ProgresssFragment;
import com.kidsapp.ui.child.shop.ShopFragment;
import com.kidsapp.ui.child.task.ChildTaskListFragment;
import com.kidsapp.ui.parent.home.adapter.NotificationAdapter;
import com.kidsapp.ui.parent.home.model.Notification;

import java.util.List;

public class ChildHomeFragment extends Fragment {
    private FragmentChildHomeBinding binding;
    private ChildProfileLoader profileLoader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChildHomeBinding.inflate(inflater, container, false);
        
        // Khởi tạo profile loader
        profileLoader = new ChildProfileLoader(requireContext());
        
        // Load profile từ API
        loadChildProfile();
        
        setupLevelCard();
        setupTodayGoal();
        setupClickListeners();
        
        return binding.getRoot();
    }

    /**
     * Load profile từ API và fill vào header
     */
    private void loadChildProfile() {
        profileLoader.loadAndFillHeader(binding.headerUser, new ChildProfileLoader.ProfileLoadListener() {
            @Override
            public void onProfileLoaded(Child child) {
                if (getActivity() == null) return;
                
                // Cập nhật level card với dữ liệu thực
                updateLevelCard(child);
            }

            @Override
            public void onProfileLoadError(String error) {
                // Dữ liệu mặc định đã được fill bởi loader
            }
        });
    }

    /**
     * Cập nhật level card với dữ liệu từ API
     */
    private void updateLevelCard(Child child) {
        int level = child.getLevel();
        int currentExp = child.getTotalPoints();
        
        // Tính XP cần để lên level tiếp theo (mỗi level cần 100 XP)
        int targetExp = level * 100;
        int currentLevelExp = currentExp % 100; // XP trong level hiện tại
        int remainExp = 100 - currentLevelExp;
        int percent = currentLevelExp;

        binding.txtLevelLabel.setText(getString(R.string.child_level_value, level));
        binding.txtExp.setText(currentLevelExp + " / 100 XP");
        binding.txtExpPercent.setText(percent + "%");
        binding.progressExp.setProgress(percent);
        binding.txtExpHint.setText("Chỉ còn " + remainExp + " XP nữa để lên cấp!");
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
    }

    private void setupTodayGoal() {
        // TODO: Lấy từ API
        int tasksCompleted = 0;
        int totalTasks = 5;
        int xpEarned = 0;

        binding.txtTasksInfo.setText("Hoàn thành " + tasksCompleted + "/" + totalTasks + " nhiệm vụ");
        binding.txtXpInfo.setText("Nhận " + xpEarned + " XP");
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

        List<Notification> notifications = FakeNotificationRepository.getDemoNotifications();
        NotificationAdapter adapter = new NotificationAdapter();
        adapter.setNotifications(notifications);
        adapter.setOnNotificationClickListener((notification, position) -> 
            Toast.makeText(requireContext(), notification.getMessage(), Toast.LENGTH_SHORT).show()
        );

        sheetBinding.recyclerNotifications.setAdapter(adapter);
        sheetBinding.layoutEmptyNotifications.setVisibility(notifications.isEmpty() ? View.VISIBLE : View.GONE);
        sheetBinding.recyclerNotifications.setVisibility(notifications.isEmpty() ? View.GONE : View.VISIBLE);

        sheetBinding.txtMarkAllRead.setOnClickListener(v -> {
            adapter.markAllAsRead();
            Toast.makeText(requireContext(), "Đã đánh dấu tất cả là đã đọc", Toast.LENGTH_SHORT).show();
        });

        dialog.setContentView(sheetBinding.getRoot());
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
