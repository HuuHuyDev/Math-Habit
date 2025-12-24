package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;
import com.kidsapp.databinding.FragmentLeaderboardBinding;

/**
 * Fragment bảng xếp hạng
 * Hiển thị tabs: Tuần, Tháng, Tất cả
 */
public class LeaderboardFragment extends Fragment {

    private FragmentLeaderboardBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        binding.btnBack.setOnClickListener(v -> 
            requireActivity().onBackPressed());

        setupViewPager();
    }

    private void setupViewPager() {
        LeaderboardPagerAdapter adapter = new LeaderboardPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
            (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText("Tuần");
                        break;
                    case 1:
                        tab.setText("Tháng");
                        break;
                    case 2:
                        tab.setText("Tất cả");
                        break;
                }
            }
        ).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * ViewPager adapter for leaderboard tabs
     */
    private static class LeaderboardPagerAdapter extends FragmentStateAdapter {

        public LeaderboardPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return LeaderboardListFragment.newInstance(position);
        }

        @Override
        public int getItemCount() {
            return 3; // Week, Month, All
        }
    }
}
