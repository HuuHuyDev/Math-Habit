package com.kidsapp.ui.child.task;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.kidsapp.ui.child.task.tabs.HomeworkTabFragment;
import com.kidsapp.ui.child.task.tabs.PersonalTabFragment;
import com.kidsapp.ui.child.task.tabs.ExerciseTabFragment;
import com.kidsapp.ui.child.task.tabs.HistoryTabFragment;

/**
 * Adapter cho ViewPager2 với 4 tabs
 */
public class TaskPagerAdapter extends FragmentStateAdapter {
    private static final int TAB_COUNT = 4;

    public TaskPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: // Việc nhà
                return HomeworkTabFragment.newInstance("", "");
            case 1: // Cá nhân
                return PersonalTabFragment.newInstance("", "");
            case 2: // Bài tập
                return ExerciseTabFragment.newInstance("", "");
            case 3: // Lịch sử
                return HistoryTabFragment.newInstance("", "");
            default:
                return ExerciseTabFragment.newInstance("", "");
        }
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}

