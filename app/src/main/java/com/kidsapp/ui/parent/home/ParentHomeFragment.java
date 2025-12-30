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

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.ActivityLog;
import com.kidsapp.data.model.Child;
import com.kidsapp.databinding.BottomsheetNotificationsBinding;
import com.kidsapp.databinding.FragmentParentHomeBinding;
import com.kidsapp.ui.components.LoadingDialog;
import com.kidsapp.ui.parent.home.adapter.ChildCardAdapter;
import com.kidsapp.ui.parent.home.adapter.NotificationAdapter;
import com.kidsapp.ui.parent.home.adapter.RecentActivityAdapter;
import com.kidsapp.ui.parent.home.model.Notification;
import com.kidsapp.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Parent Home Fragment
 */
public class ParentHomeFragment extends Fragment {

    private FragmentParentHomeBinding binding;
    private SharedPref sharedPref;
    private HomeViewModel viewModel;
    private ChildCardAdapter childCardAdapter;
    private RecentActivityAdapter recentActivityAdapter;
    private ApiService apiService;
    private LoadingDialog loadingDialog;

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
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();
        loadingDialog = new LoadingDialog(requireContext());
        
        setupHeader();
        setupChildrenRecycler();
        setupRecentRecycler();
        observeViewModel();
        applyAnimations();
        
        // Load data từ API
        showLoading();
        viewModel.loadHomeData();
        
        // Load parent avatar
        loadParentAvatar();
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
        
        // Click vào avatar để mở Profile
        binding.headerParent.layoutAvatar.setOnClickListener(v -> {
            try {
                Navigation.findNavController(requireView())
                        .navigate(R.id.nav_profile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Load avatar của parent từ API
     */
    private void loadParentAvatar() {
        apiService.getParentProfileApi().enqueue(new Callback<ApiService.ApiResponseWrapper<ApiService.ParentProfileResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<ApiService.ParentProfileResponse>> call,
                                   Response<ApiService.ApiResponseWrapper<ApiService.ParentProfileResponse>> response) {
                if (!isAdded() || binding == null) return;
                
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    ApiService.ParentProfileResponse profile = response.body().data;
                    
                    // Cập nhật tên
                    if (profile.fullName != null && !profile.fullName.isEmpty()) {
                        binding.headerParent.tvHelloSubtitle.setText(getString(R.string.hello_parent, profile.fullName));
                    }
                    
                    // Cập nhật avatar
                    if (profile.avatarUrl != null && !profile.avatarUrl.isEmpty()) {
                        Glide.with(requireContext())
                                .load(profile.avatarUrl)
                                .placeholder(R.drawable.ic_user_default)
                                .error(R.drawable.ic_user_default)
                                .circleCrop()
                                .into(binding.headerParent.ivParentAvatar);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<ApiService.ParentProfileResponse>> call, Throwable t) {
                // Ignore - keep default avatar
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
                if (isLoading) {
                    showLoading();
                } else {
                    hideLoading();
                }
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
        child.setTotalPoints(response.totalXp != null ? response.totalXp : 0);
        child.setGrade(response.grade);
        child.setDailyProgress(response.dailyProgress != null ? response.dailyProgress : 0f);
        
        // Set avatar: sử dụng avatarUrl trực tiếp (có thể là URL, drawable name, hoặc emoji)
        if (response.avatarUrl != null && !response.avatarUrl.isEmpty()) {
            child.setAvatarUrl(response.avatarUrl);
        } else {
            // Fallback to emoji theo giới tính
            String defaultAvatar = (response.gender != null && response.gender) ? "👦" : "👧";
            child.setAvatarUrl(defaultAvatar);
        }
        
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

        NotificationAdapter adapter = new NotificationAdapter();
        bottomSheetBinding.recyclerNotifications.setAdapter(adapter);
        
        // Show loading state
        bottomSheetBinding.layoutEmptyNotifications.setVisibility(View.GONE);
        bottomSheetBinding.recyclerNotifications.setVisibility(View.GONE);

        // Gọi API lấy thông báo
        apiService.getNotifications().enqueue(new Callback<ApiService.ApiResponseWrapper<List<ApiService.NotificationResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.NotificationResponse>>> call,
                                   Response<ApiService.ApiResponseWrapper<List<ApiService.NotificationResponse>>> response) {
                if (!isAdded()) return;
                
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    List<ApiService.NotificationResponse> apiNotifications = response.body().data;
                    
                    if (apiNotifications != null && !apiNotifications.isEmpty()) {
                        List<Notification> notifications = new ArrayList<>();
                        for (ApiService.NotificationResponse n : apiNotifications) {
                            notifications.add(new Notification(
                                    n.id,
                                    n.title != null ? n.title : "Thông báo",
                                    n.iconUrl,
                                    n.message != null ? n.message : "",
                                    n.type != null ? n.type : "info",
                                    n.timeAgo != null ? n.timeAgo : "",
                                    n.isRead
                            ));
                        }
                        adapter.setNotifications(notifications);
                        bottomSheetBinding.layoutEmptyNotifications.setVisibility(View.GONE);
                        bottomSheetBinding.recyclerNotifications.setVisibility(View.VISIBLE);
                    } else {
                        bottomSheetBinding.layoutEmptyNotifications.setVisibility(View.VISIBLE);
                        bottomSheetBinding.recyclerNotifications.setVisibility(View.GONE);
                    }
                } else {
                    bottomSheetBinding.layoutEmptyNotifications.setVisibility(View.VISIBLE);
                    bottomSheetBinding.recyclerNotifications.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.NotificationResponse>>> call, Throwable t) {
                if (!isAdded()) return;
                bottomSheetBinding.layoutEmptyNotifications.setVisibility(View.VISIBLE);
                bottomSheetBinding.recyclerNotifications.setVisibility(View.GONE);
            }
        });

        adapter.setOnNotificationClickListener((notification, position) -> {
            // Đánh dấu đã đọc khi click
            if (!notification.isRead()) {
                markNotificationAsRead(notification.getId());
                notification.setRead(true);
                adapter.notifyItemChanged(position);
            }
            Toast.makeText(requireContext(), notification.getMessage(), Toast.LENGTH_SHORT).show();
        });

        bottomSheetBinding.txtMarkAllRead.setOnClickListener(v -> {
            markAllNotificationsAsRead();
            adapter.markAllAsRead();
            Toast.makeText(requireContext(), "Đã đánh dấu tất cả là đã đọc", Toast.LENGTH_SHORT).show();
        });

        bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());
        bottomSheetDialog.show();
    }
    
    /**
     * Đánh dấu một thông báo đã đọc
     */
    private void markNotificationAsRead(String notificationId) {
        apiService.markNotificationAsRead(notificationId).enqueue(new Callback<ApiService.ApiResponseWrapper<Void>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Void>> call,
                                   Response<ApiService.ApiResponseWrapper<Void>> response) {
                // Silent - không cần xử lý
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Void>> call, Throwable t) {
                // Silent - không cần xử lý
            }
        });
    }
    
    /**
     * Đánh dấu tất cả thông báo đã đọc
     */
    private void markAllNotificationsAsRead() {
        apiService.markAllNotificationsAsRead().enqueue(new Callback<ApiService.ApiResponseWrapper<Void>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Void>> call,
                                   Response<ApiService.ApiResponseWrapper<Void>> response) {
                // Silent - không cần xử lý
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Void>> call, Throwable t) {
                // Silent - không cần xử lý
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideLoading();
        loadingDialog = null;
        binding = null;
    }
}
