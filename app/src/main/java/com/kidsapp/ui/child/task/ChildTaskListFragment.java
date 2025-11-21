package com.kidsapp.ui.child.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.kidsapp.R;
import com.kidsapp.databinding.FragmentTaskListBinding;

/**
 * Child Task List Fragment
 */
public class ChildTaskListFragment extends Fragment {
    private FragmentTaskListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskListBinding.inflate(inflater, container, false);
        setupHeader();
        setupBackButton();
        setupRecyclerView();
        setupTabLayout();
        setupStartButton();
        return binding.getRoot();
    }

    private void setupHeader() {
        binding.headerUser.setUserName("Hồ Hữu Huy");
        binding.headerUser.setAvatar(R.drawable.ic_user_default);
        binding.headerUser.setNotificationClick(v ->
                Toast.makeText(requireContext(), R.string.feature_coming_soon, Toast.LENGTH_SHORT).show());
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

    private void setupStartButton() {
        binding.btnStart.setOnClickListener(v ->
                Toast.makeText(requireContext(), R.string.task_start, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

