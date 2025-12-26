package com.kidsapp.ui.parent.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.ActivityLog;
import com.kidsapp.data.model.Child;
import com.kidsapp.databinding.BottomsheetNotificationsBinding;
import com.kidsapp.databinding.FragmentParentHomeBinding;
import com.kidsapp.ui.parent.home.adapter.ChildCardAdapter;
import com.kidsapp.ui.parent.home.adapter.NotificationAdapter;
import com.kidsapp.ui.parent.home.adapter.RecentActivityAdapter;
import com.kidsapp.ui.parent.home.model.Notification;
import com.kidsapp.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Parent Home Fragment
 */
public class ParentHomeFragment extends Fragment {

    private FragmentParentHomeBinding binding;
    private SharedPref sharedPref;
    private HomeViewModel viewModel;
    private ChildCardAdapter childCardAdapter;
    private RecentActivityAdapter recentActivityAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentParentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPref = new SharedPref(requireContext());
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        
        setupHeader();
        setupChildrenRecycler();
        setupRecentRecycler();
        observeViewModel();
        applyAnimations();
        
        // Load data từ API
        viewModel.loadHomeData();
    }

    private void setupHeader() {
        String userName = sharedPref.getUserName();
        if (userName == null || userName.isEmpty()) {
            userName = "Phụ huynh";
        }
        binding.headerParent.tvHelloSubtitle.setText(getString(R.string.hello_parent, userName));
        
        // Click vào bell icon để hiện thông báo
        binding.headerParent.flBell.setOnClickListener(v -> showNotificationsBottomSheet());
        
        // Click vào chat icon để mở Chat Hub
        binding.headerParent.flChat.setOnClickListener(v -> {
            try {
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_nav_home_to_chat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setupChildrenRecycler() {
        childCardAdapter = new ChildCardAdapter(new ArrayList<>(), child -> {
            navigateToChildDetail(child);
        });

        RecyclerView rvChildren = binding.rvChildren;
        rvChildren.setLayoutManager(
                new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvChildren.setAdapter(childCardAdapter);
    }

    private void setupRecentRecycler() {
        recentActivityAdapter = new RecentActivityAdapter(new ArrayList<>());
        binding.rvRecentActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvRecentActivities.setAdapter(recentActivityAdapter);
    }

    private void observeViewModel() {
        // Observe loading
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        // Observe children
        viewModel.getChildren().observe(getViewLifecycleOwner(), children -> {
            if (children != null && !children.isEmpty()) {
                List<Child> childList = new ArrayList<>();
                for (ApiService.ChildResponse response : children) {
                    childList.add(mapToChild(response));
                }
                childCardAdapter.updateChildren(childList);
            }
        });

        // Observe activities
        viewModel.getActivities().observe(getViewLifecycleOwner(), activities -> {
            if (activities != null) {
                List<ActivityLog> activityLogs = new ArrayList<>();
                for (ApiService.ActivityLogResponse response : activities) {
                    activityLogs.add(mapToActivityLog(response));
                }
                recentActivityAdapter.updateActivities(activityLogs);
            }
        });

        // Observe errors
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Child mapToChild(ApiService.ChildResponse response) {
        Child child = new Child();
        child.setId(response.id);
        child.setName(response.name != null ? response.name : response.nickname);
        child.setLevel(response.level != null ? response.level : 1);
        child.setTotalPoints(response.totalPoints != null ? response.totalPoints : 0);
        child.setAvatarUrl(response.avatarUrl);
        child.setGrade(response.grade);
        child.setDailyProgress(response.dailyProgress != null ? response.dailyProgress : 0f);
        return child;
    }

    private ActivityLog mapToActivityLog(ApiService.ActivityLogResponse response) {
        return new ActivityLog(
                response.id,
                response.childId,
                response.childName,
                response.description,
                response.xpEarned != null ? response.xpEarned : 0,
                response.childAvatar,  // avatar
                response.activityType, // icon
                response.timeAgo
        );
    }

    private void navigateToChildDetail(Child child) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("childId", child.getId());
            bundle.putString("childName", child.getName());
            bundle.putInt("childLevel", child.getLevel());
            bundle.putInt("childXP", child.getTotalPoints());

            View view = getView();
            if (view == null) {
                view = binding.getRoot();
            }
            
            Navigation.findNavController(view)
                    .navigate(R.id.action_nav_home_to_parentChildDetail, bundle);
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Lỗi navigation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void applyAnimations() {
        binding.headerParent.getRoot().setAlpha(0f);
        binding.headerParent.getRoot().animate()
                .alpha(1f)
                .setDuration(getResources().getInteger(R.integer.anim_duration_medium))
                .start();
    }

    private void showNotificationsBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        BottomsheetNotificationsBinding bottomSheetBinding = BottomsheetNotificationsBinding.inflate(
                getLayoutInflater());

        List<Notification> notifications = new ArrayList<>();

        NotificationAdapter adapter = new NotificationAdapter();
        adapter.setNotifications(notifications);
        adapter.setOnNotificationClickListener((notification, position) -> {
            Toast.makeText(requireContext(), notification.getMessage(), Toast.LENGTH_SHORT).show();
        });

        bottomSheetBinding.recyclerNotifications.setAdapter(adapter);
        bottomSheetBinding.layoutEmptyNotifications.setVisibility(View.VISIBLE);
        bottomSheetBinding.recyclerNotifications.setVisibility(View.GONE);

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
