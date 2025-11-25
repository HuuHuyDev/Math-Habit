package com.kidsapp.ui.parent.child.detail;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.kidsapp.ui.parent.child.detail.tabs.badge.BadgeTabFragment;
import com.kidsapp.ui.parent.child.detail.tabs.exercise.ExerciseTabFragment;
import com.kidsapp.ui.parent.child.detail.tabs.housework.HouseworkTabFragment;

/**
 * Adapter cho ViewPager2 với 3 tabs: Việc nhà, Bài tập, Huy hiệu
 */
public class ViewPagerAdapter extends FragmentStateAdapter {

    private static final int TAB_COUNT = 3;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: // Việc nhà
                return HouseworkTabFragment.newInstance();
            case 1: // Bài tập
                return ExerciseTabFragment.newInstance();
            case 2: // Huy hiệu
                return BadgeTabFragment.newInstance();
            default:
                return HouseworkTabFragment.newInstance();
        }
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}

