package com.kidsapp.ui.child.task;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.kidsapp.ui.child.task.tabs.WorkTabFragment;
import com.kidsapp.ui.child.task.tabs.ExerciseTabFragment;
import com.kidsapp.ui.child.task.tabs.HistoryTabFragment;

/**
 * Adapter cho ViewPager2 với 3 tabs
 * - Tab 0: Công việc (Việc nhà + Cá nhân)
 * - Tab 1: Bài tập
 * - Tab 2: Lịch sử
 */
public class TaskPagerAdapter extends FragmentStateAdapter {
    private static final int TAB_COUNT = 3;

    public TaskPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: // Công việc (housework + habit + custom)
                return new WorkTabFragment();
            case 1: // Bài tập (exercise)
                return ExerciseTabFragment.newInstance("", "");
            case 2: // Lịch sử
                return HistoryTabFragment.newInstance("", "");
            default:
                return new WorkTabFragment();
        }
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}

