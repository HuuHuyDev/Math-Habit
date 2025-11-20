package com.kidsapp.ui.parent.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.ActivityLog;
import com.kidsapp.data.model.Child;
import com.kidsapp.databinding.FragmentParentHomeBinding;
import com.kidsapp.ui.parent.home.adapter.ChildCardAdapter;
import com.kidsapp.ui.parent.home.adapter.RecentActivityAdapter;
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

    private void setupHeader() {
        String userName = sharedPref.getUserName();
        if (userName == null || userName.isEmpty()) {
            userName = "Phụ huynh";
        }
        binding.headerParent.tvHelloSubtitle.setText(getString(R.string.hello_parent, userName));
    }

    private void setupChildrenRecycler() {
        List<Child> children = new ArrayList<>();
        children.add(createChild("1", "Hồ Hữu Huy", 3, 650));
        children.add(createChild("2", "Linh", 2, 480));

        childCardAdapter = new ChildCardAdapter(children, child -> {
            // TODO: navigate to child detail screen
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

