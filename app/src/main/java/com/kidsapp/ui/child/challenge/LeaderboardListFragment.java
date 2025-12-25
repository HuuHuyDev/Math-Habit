package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.data.model.LeaderboardItem;
import com.kidsapp.databinding.FragmentLeaderboardListBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment hiển thị danh sách xếp hạng cho mỗi tab
 */
public class LeaderboardListFragment extends Fragment {

    private static final String ARG_TAB_INDEX = "tab_index";

    private FragmentLeaderboardListBinding binding;
    private LeaderboardAdapter adapter;
    private int tabIndex;

    public static LeaderboardListFragment newInstance(int tabIndex) {
        LeaderboardListFragment fragment = new LeaderboardListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_INDEX, tabIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLeaderboardListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        if (getArguments() != null) {
            tabIndex = getArguments().getInt(ARG_TAB_INDEX);
        }

        setupRecyclerView();
        loadLeaderboard();

        binding.swipeRefresh.setOnRefreshListener(() -> {
            loadLeaderboard();
            binding.swipeRefresh.setRefreshing(false);
        });
    }

    private void setupRecyclerView() {
        adapter = new LeaderboardAdapter();
        binding.recyclerLeaderboard.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerLeaderboard.setAdapter(adapter);
    }

    private void loadLeaderboard() {
        // TODO: Load from repository based on tabIndex
        // 0 = Week, 1 = Month, 2 = All
        List<LeaderboardItem> items = generateSampleData();
        adapter.submitList(items);
    }

    private List<LeaderboardItem> generateSampleData() {
        List<LeaderboardItem> items = new ArrayList<>();
        
        // Sample data
        items.add(new LeaderboardItem("1", "Nguyễn Văn A", 1250, 85, 15, 1));
        items.add(new LeaderboardItem("2", "Trần Thị B", 1180, 80, 20, 2));
        items.add(new LeaderboardItem("3", "Lê Văn C", 1050, 75, 25, 3));
        items.add(new LeaderboardItem("4", "Phạm Thị D", 980, 70, 30, 4));
        items.add(new LeaderboardItem("5", "Hoàng Văn E", 920, 68, 32, 5));
        items.add(new LeaderboardItem("6", "Vũ Thị F", 850, 65, 35, 6));
        items.add(new LeaderboardItem("7", "Đỗ Văn G", 780, 60, 40, 7));
        items.add(new LeaderboardItem("8", "Bùi Thị H", 720, 58, 42, 8));
        items.add(new LeaderboardItem("9", "Đinh Văn I", 650, 55, 45, 9));
        items.add(new LeaderboardItem("10", "Ngô Thị K", 580, 50, 50, 10));

        return items;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
