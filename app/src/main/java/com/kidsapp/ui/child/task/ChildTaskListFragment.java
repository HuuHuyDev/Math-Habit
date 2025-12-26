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
import com.kidsapp.data.FakeNotificationRepository;
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
                            tab.setText(R.string.task_tab_homework);
                            break;
                        case 1:
                            tab.setText(R.string.task_tab_personal);
                            break;
                        case 2:
                            tab.setText(R.string.task_tab_exercise);
                            break;
                        case 3:
                            tab.setText(R.string.task_tab_history);
                            break;
                    }
                }
        ).attach();
        
        // Select "Bài tập" tab (index 2) by default
        if (binding.tabLayout.getTabCount() > 2) {
            binding.tabLayout.getTabAt(2).select();
        }
    }

    /**
     * Hiển thị BottomSheet danh sách thông báo từ parent
     */
    private void showNotificationsBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        BottomsheetNotificationsBinding bottomSheetBinding = BottomsheetNotificationsBinding.inflate(
                getLayoutInflater());

        // Load dữ liệu thông báo từ parent
        List<Notification> notifications = FakeNotificationRepository.getDemoNotifications();

        // Setup adapter
        NotificationAdapter adapter = new NotificationAdapter();
        adapter.setNotifications(notifications);
        adapter.setOnNotificationClickListener((notification, position) -> {
            // Đánh dấu đã đọc khi click
            Toast.makeText(requireContext(),
                    notification.getMessage(), Toast.LENGTH_SHORT).show();
        });

        bottomSheetBinding.recyclerNotifications.setAdapter(adapter);

        // Hiển thị empty state nếu không có thông báo
        if (notifications.isEmpty()) {
            bottomSheetBinding.layoutEmptyNotifications.setVisibility(View.VISIBLE);
            bottomSheetBinding.recyclerNotifications.setVisibility(View.GONE);
        } else {
            bottomSheetBinding.layoutEmptyNotifications.setVisibility(View.GONE);
            bottomSheetBinding.recyclerNotifications.setVisibility(View.VISIBLE);
        }

        // Xử lý nút "Đánh dấu đã đọc"
        bottomSheetBinding.txtMarkAllRead.setOnClickListener(v -> {
            adapter.markAllAsRead();
            Toast.makeText(requireContext(), "Đã đánh dấu tất cả là đã đọc", Toast.LENGTH_SHORT).show();
        });

        bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());
        bottomSheetDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

