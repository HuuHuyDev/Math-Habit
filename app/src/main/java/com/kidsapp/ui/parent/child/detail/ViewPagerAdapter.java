package com.kidsapp.ui.parent.child.detail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.kidsapp.ui.parent.child.detail.tabs.badge.BadgeTabFragment;
import com.kidsapp.ui.parent.child.detail.tabs.exercise.ExerciseTabFragment;
import com.kidsapp.ui.parent.child.detail.tabs.housework.HouseworkTabFragment;

/**
 * Adapter cho ViewPager2 với 3 tabs: Thói quen, Học tập, Huy hiệu
 */
public class ViewPagerAdapter extends FragmentStateAdapter {

    private static final int TAB_COUNT = 3;
    private final String childId;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, String childId) {
        super(fragmentActivity);
        this.childId = childId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle args = new Bundle();
        args.putString("childId", childId);
        
        Fragment fragment;
        switch (position) {
            case 0: // Thói quen
                fragment = HouseworkTabFragment.newInstance();
                break;
            case 1: // Học tập
                fragment = ExerciseTabFragment.newInstance();
                break;
            case 2: // Huy hiệu
                fragment = BadgeTabFragment.newInstance();
                break;
            default:
                fragment = HouseworkTabFragment.newInstance();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}

