package com.kidsapp.ui.child.task.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;
import com.kidsapp.R;
import com.kidsapp.data.model.HistoryTask;
import com.kidsapp.databinding.FragmentHistoryTabBinding;
import com.kidsapp.ui.child.task.TaskHistoryAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment hiển thị lịch sử nhiệm vụ đã hoàn thành
 */
public class HistoryTabFragment extends Fragment {
    private FragmentHistoryTabBinding binding;
    private TaskHistoryAdapter adapter;
    private List<HistoryTask> allHistoryTasks = new ArrayList<>();
    private List<HistoryTask> filteredTasks = new ArrayList<>();
    private int selectedFilterIndex = 0; // 0: Tất cả, 1: Hôm nay, 2: Tuần này, 3: Tháng này

    public HistoryTabFragment() {
        // Required empty public constructor
    }

    public static HistoryTabFragment newInstance(String param1, String param2) {
        return new HistoryTabFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHistoryTabBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupFilterChips();
        setupRecyclerView();
        loadSampleData();
    }



    private void setupFilterChips() {
        // Xử lý click vào các chip filter
        binding.chipAll.setOnClickListener(v -> {
            selectedFilterIndex = 0;
            updateFilterChipStyles();
            filterTasks();
        });

        binding.chipToday.setOnClickListener(v -> {
            selectedFilterIndex = 1;
            updateFilterChipStyles();
            filterTasks();
        });

        binding.chipThisWeek.setOnClickListener(v -> {
            selectedFilterIndex = 2;
            updateFilterChipStyles();
            filterTasks();
        });

        binding.chipThisMonth.setOnClickListener(v -> {
            selectedFilterIndex = 3;
            updateFilterChipStyles();
            filterTasks();
        });

        // Mặc định chọn "Tất cả"
        updateFilterChipStyles();
    }

    private void updateFilterChipStyles() {
        // Reset tất cả chips về unselected
        binding.chipAll.setBackgroundResource(R.drawable.bg_filter_chip_unselected);
        binding.chipAll.setTextColor(ContextCompat.getColor(requireContext(), R.color.history_primary));
        binding.chipToday.setBackgroundResource(R.drawable.bg_filter_chip_unselected);
        binding.chipToday.setTextColor(ContextCompat.getColor(requireContext(), R.color.history_primary));
        binding.chipThisWeek.setBackgroundResource(R.drawable.bg_filter_chip_unselected);
        binding.chipThisWeek.setTextColor(ContextCompat.getColor(requireContext(), R.color.history_primary));
        binding.chipThisMonth.setBackgroundResource(R.drawable.bg_filter_chip_unselected);
        binding.chipThisMonth.setTextColor(ContextCompat.getColor(requireContext(), R.color.history_primary));

        // Set selected chip
        Chip selectedChip = null;
        switch (selectedFilterIndex) {
            case 0:
                selectedChip = binding.chipAll;
                break;
            case 1:
                selectedChip = binding.chipToday;
                break;
            case 2:
                selectedChip = binding.chipThisWeek;
                break;
            case 3:
                selectedChip = binding.chipThisMonth;
                break;
        }

        if (selectedChip != null) {
            selectedChip.setBackgroundResource(R.drawable.bg_filter_chip_selected);
            selectedChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        }
    }

    private void filterTasks() {
        // TODO: Implement logic filter theo thời gian thực tế
        // Hiện tại chỉ hiển thị tất cả
        filteredTasks.clear();
        filteredTasks.addAll(allHistoryTasks);
        adapter.updateList(filteredTasks);
    }

    private void setupRecyclerView() {
        adapter = new TaskHistoryAdapter(new ArrayList<>());
        binding.recyclerViewHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewHistory.setAdapter(adapter);
    }

    private void loadSampleData() {
        allHistoryTasks.clear();
        
        // Sample data theo hình mẫu
        allHistoryTasks.add(new HistoryTask(
                "Bài 1: Luyện phép cộng",
                "12/11/2025 - 15:30",
                50,
                100,
                5.0f,
                R.drawable.ic_launcher_foreground
        ));
        
        allHistoryTasks.add(new HistoryTask(
                "Quét nhà phòng khách",
                "12/11/2025 - 14:00",
                30,
                60,
                0f,
                R.drawable.ic_launcher_foreground
        ));
        
        allHistoryTasks.add(new HistoryTask(
                "Nấu ăn cùng ba mẹ",
                "11/11/2025 - 18:15",
                40,
                80,
                0f,
                R.drawable.ic_launcher_foreground
        ));
        
        allHistoryTasks.add(new HistoryTask(
                "Dọn và sắp xếp phòng",
                "11/11/2025 - 16:45",
                35,
                70,
                0f,
                R.drawable.ic_launcher_foreground
        ));
        
        allHistoryTasks.add(new HistoryTask(
                "Học từ vựng tiếng Anh",
                "10/11/2025 - 10:20",
                45,
                90,
                4.5f,
                R.drawable.ic_launcher_foreground
        ));
        
        allHistoryTasks.add(new HistoryTask(
                "Tưới cây trong vườn",
                "09/11/2025 - 09:00",
                25,
                50,
                0f,
                R.drawable.ic_launcher_foreground
        ));

        filterTasks();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
