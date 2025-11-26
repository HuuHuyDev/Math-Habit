package com.kidsapp.ui.parent.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kidsapp.R;
import com.kidsapp.data.FakeNotificationRepository;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.ActivityLog;
import com.kidsapp.data.model.Child;
import com.kidsapp.databinding.BottomsheetNotificationsBinding;
import com.kidsapp.databinding.FragmentParentHomeBinding;
import com.kidsapp.ui.parent.home.adapter.ChildCardAdapter;
import com.kidsapp.ui.parent.home.adapter.NotificationAdapter;
import com.kidsapp.ui.parent.home.adapter.RecentActivityAdapter;
import com.kidsapp.ui.parent.home.model.Notification;
import java.util.ArrayList;
import java.util.List;

/**
 * Parent Home Fragment
 */
public class ParentHomeFragment extends Fragment {

    private FragmentParentHomeBinding binding;
    private SharedPref sharedPref;
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
        setupHeader();
        setupChildrenRecycler();
        setupRecentRecycler();
        applyAnimations();
    }
    private void setupClickListeners() {
        binding.rvChildren.setOnClickListener(v -> {
            // Navigate to forgot password fragment
            Navigation.findNavController(v).navigate(R.id.action_register_to_login);
        });

    }
    private void setupHeader() {
        String userName = sharedPref.getUserName();
        if (userName == null || userName.isEmpty()) {
            userName = "Phụ huynh";
        }
        binding.headerParent.tvHelloSubtitle.setText(getString(R.string.hello_parent, userName));
        
        // Click vào bell icon để hiện thông báo
        binding.headerParent.flBell.setOnClickListener(v -> showNotificationsBottomSheet());
    }

    private void setupChildrenRecycler() {
        List<Child> children = new ArrayList<>();
        children.add(createChild("1", "Hồ Hữu Huy", 3, 650));
        children.add(createChild("2", "Linh", 2, 480));

        childCardAdapter = new ChildCardAdapter(children, child -> {
            try {
                Bundle bundle = new Bundle();
                bundle.putString("childId", child.getId());
                bundle.putString("childName", child.getName());
                bundle.putInt("childLevel", child.getLevel());
                bundle.putInt("childXP", child.getTotalXP());

                // Tìm NavController - thử nhiều cách
                View view = getView();
                if (view == null) {
                    view = binding.getRoot();
                }
                
                androidx.navigation.NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_nav_home_to_parentChildDetail, bundle);
                
            } catch (IllegalArgumentException e) {
                // Nếu không tìm thấy NavController từ view, thử từ Activity
                try {
                    androidx.navigation.NavController navController = 
                        Navigation.findNavController(requireActivity(), R.id.navHostFragment);
                    Bundle bundle = new Bundle();
                    bundle.putString("childId", child.getId());
                    bundle.putString("childName", child.getName());
                    bundle.putInt("childLevel", child.getLevel());
                    bundle.putInt("childXP", child.getTotalXP());
                    navController.navigate(R.id.action_nav_home_to_parentChildDetail, bundle);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(requireContext(), "Lỗi navigation: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView rvChildren = binding.rvChildren;
        rvChildren.setLayoutManager(
                new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvChildren.setAdapter(childCardAdapter);
    }


    private Child createChild(String id, String name, int level, int xp) {
        Child child = new Child();
        child.setId(id);
        child.setName(name);
        child.setLevel(level);
        child.setTotalXP(xp);
        return child;
    }

    private void setupRecentRecycler() {
        List<ActivityLog> activities = new ArrayList<>();
        activities.add(new ActivityLog("1", "1", "Huy",
                "đã hoàn thành bài tập Cộng level 2", 15, null, null, "10 phút trước"));
        activities.add(new ActivityLog("2", "2", "Linh",
                "đã dọn đồ chơi", 12, null, null, "25 phút trước"));
        activities.add(new ActivityLog("3", "1", "Huy",
                "đã đánh răng sáng", 5, null, null, "2 giờ trước"));

        recentActivityAdapter = new RecentActivityAdapter(activities);
        binding.rvRecentActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvRecentActivities.setAdapter(recentActivityAdapter);
    }

    private void applyAnimations() {
        binding.headerParent.getRoot().setAlpha(0f);
        binding.headerParent.getRoot().animate()
                .alpha(1f)
                .setDuration(getResources().getInteger(com.kidsapp.R.integer.anim_duration_medium))
                .start();
    }

    /**
     * Hiển thị BottomSheet danh sách thông báo
     */
    private void showNotificationsBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        BottomsheetNotificationsBinding bottomSheetBinding = BottomsheetNotificationsBinding.inflate(
                getLayoutInflater());

        // Load dữ liệu demo
        List<Notification> notifications = FakeNotificationRepository.getDemoNotifications();

        // Setup adapter
        NotificationAdapter adapter = new NotificationAdapter();
        adapter.setNotifications(notifications);
        adapter.setOnNotificationClickListener((notification, position) -> {
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

        // Đánh dấu tất cả đã đọc
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

