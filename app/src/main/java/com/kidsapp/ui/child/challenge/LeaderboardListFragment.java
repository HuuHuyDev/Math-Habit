package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.data.api.ApiService;
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
        adapter = new LeaderboardAdapter(new ArrayList<>());
        binding.recyclerLeaderboard.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerLeaderboard.setAdapter(adapter);
    }

    private void loadLeaderboard() {
        // TODO: Load from repository based on tabIndex
        // 0 = Week, 1 = Month, 2 = All
        List<ApiService.LeaderboardResponse> items = generateSampleData();
        adapter.updateData(items);
    }

    private List<ApiService.LeaderboardResponse> generateSampleData() {
        List<ApiService.LeaderboardResponse> items = new ArrayList<>();
        
        // Sample data
        items.add(new ApiService.LeaderboardResponse("1", "Nguyễn Văn A", null, 1, 1250, 15, 20, 75.0, 5, 8, false));
        items.add(new ApiService.LeaderboardResponse("2", "Trần Thị B", null, 2, 1180, 12, 18, 66.7, 3, 6, false));
        items.add(new ApiService.LeaderboardResponse("3", "Lê Văn C", null, 3, 1050, 10, 16, 62.5, 2, 4, false));
        items.add(new ApiService.LeaderboardResponse("4", "Phạm Thị D", null, 4, 980, 8, 15, 53.3, 1, 3, false));
        items.add(new ApiService.LeaderboardResponse("5", "Hoàng Văn E", null, 5, 920, 6, 12, 50.0, 0, 2, false));
        items.add(new ApiService.LeaderboardResponse("6", "Vũ Thị F", null, 6, 850, 5, 10, 50.0, 0, 1, false));
        items.add(new ApiService.LeaderboardResponse("7", "Đỗ Văn G", null, 7, 780, 4, 8, 50.0, 0, 1, false));
        items.add(new ApiService.LeaderboardResponse("8", "Bùi Thị H", null, 8, 720, 3, 6, 50.0, 0, 1, false));
        items.add(new ApiService.LeaderboardResponse("9", "Đinh Văn I", null, 9, 650, 2, 4, 50.0, 0, 1, false));
        items.add(new ApiService.LeaderboardResponse("10", "Ngô Thị K", null, 10, 580, 1, 2, 50.0, 0, 1, false));

        return items;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
