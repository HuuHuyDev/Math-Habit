package com.kidsapp.ui.child.task.tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.kidsapp.R;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.HistoryTask;
import com.kidsapp.data.model.Task;
import com.kidsapp.data.repository.TaskRepository;
import com.kidsapp.databinding.FragmentHistoryTabBinding;
import com.kidsapp.ui.child.task.history.HistoryDetailFragment;
import com.kidsapp.ui.child.task.history.TaskHistoryAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment hiển thị lịch sử nhiệm vụ đã hoàn thành
 * Load từ API: GET /tasks/child/{childId}?status=COMPLETED
 */
public class HistoryTabFragment extends Fragment {
    
    private static final String TAG = "HistoryTabFragment";
    
    private FragmentHistoryTabBinding binding;
    private TaskHistoryAdapter adapter;
    private TaskRepository taskRepository;
    private SharedPref sharedPref;
    
    private List<HistoryTask> allHistoryTasks = new ArrayList<>();
    private List<HistoryTask> filteredTasks = new ArrayList<>();
    private int selectedFilterIndex = 0; // 0: Tất cả, 1: Hôm nay, 2: Tuần này, 3: Tháng này

    public HistoryTabFragment() {}

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
        
        // Init
        sharedPref = new SharedPref(requireContext());
        taskRepository = new TaskRepository(requireContext());
        
        setupFilterChips();
        setupRecyclerView();
        loadHistory();
    }

    private void setupFilterChips() {
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

        updateFilterChipStyles();
    }

    private void updateFilterChipStyles() {
        binding.chipAll.setBackgroundResource(R.drawable.bg_filter_chip_unselected);
        binding.chipAll.setTextColor(ContextCompat.getColor(requireContext(), R.color.history_primary));
        binding.chipToday.setBackgroundResource(R.drawable.bg_filter_chip_unselected);
        binding.chipToday.setTextColor(ContextCompat.getColor(requireContext(), R.color.history_primary));
        binding.chipThisWeek.setBackgroundResource(R.drawable.bg_filter_chip_unselected);
        binding.chipThisWeek.setTextColor(ContextCompat.getColor(requireContext(), R.color.history_primary));
        binding.chipThisMonth.setBackgroundResource(R.drawable.bg_filter_chip_unselected);
        binding.chipThisMonth.setTextColor(ContextCompat.getColor(requireContext(), R.color.history_primary));

        Chip selectedChip = null;
        switch (selectedFilterIndex) {
            case 0: selectedChip = binding.chipAll; break;
            case 1: selectedChip = binding.chipToday; break;
            case 2: selectedChip = binding.chipThisWeek; break;
            case 3: selectedChip = binding.chipThisMonth; break;
        }

        if (selectedChip != null) {
            selectedChip.setBackgroundResource(R.drawable.bg_filter_chip_selected);
            selectedChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        }
    }

    private void filterTasks() {
        filteredTasks.clear();
        
        if (selectedFilterIndex == 0) {
            // Tất cả
            filteredTasks.addAll(allHistoryTasks);
        } else {
            // Filter theo thời gian
            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            
            for (HistoryTask task : allHistoryTasks) {
                Date taskDate = parseDate(task.getCompletionTime());
                if (taskDate == null) continue;
                
                boolean include = false;
                switch (selectedFilterIndex) {
                    case 1: // Hôm nay
                        include = isSameDay(taskDate, now);
                        break;
                    case 2: // Tuần này
                        include = isSameWeek(taskDate, now);
                        break;
                    case 3: // Tháng này
                        include = isSameMonth(taskDate, now);
                        break;
                }
                
                if (include) {
                    filteredTasks.add(task);
                }
            }
        }
        
        adapter.updateList(filteredTasks);
        updateEmptyState();
    }
    
    private Date parseDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
            return sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
    
    private boolean isSameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
               c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }
    
    private boolean isSameWeek(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
               c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR);
    }
    
    private boolean isSameMonth(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
               c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH);
    }

    private void setupRecyclerView() {
        adapter = new TaskHistoryAdapter(new ArrayList<>());
        adapter.setOnHistoryClickListener(this::navigateToHistoryDetail);
        binding.recyclerViewHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewHistory.setAdapter(adapter);
    }
    
    private void navigateToHistoryDetail(HistoryTask history) {
        if (getActivity() != null) {
            HistoryDetailFragment detailFragment = HistoryDetailFragment.newInstance(
                    history.getTitle(),
                    history.getCompletionTime(),
                    history.getCoins(),
                    history.getXp(),
                    history.getRating(),
                    history.getIconRes()
            );
            
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.childHomeHost, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Load lịch sử từ API - tasks đã COMPLETED
     */
    private void loadHistory() {
        // Ưu tiên childId, fallback sang userId nếu chưa có
        String childId = sharedPref.getChildId();
        if (childId == null || childId.isEmpty()) {
            childId = sharedPref.getUserId();
        }
        
        if (childId == null || childId.isEmpty()) {
            Log.e(TAG, "Child ID and User ID not found");
            updateEmptyState();
            return;
        }
        
        Log.d(TAG, "Loading history for child: " + childId);
        
        // Load tất cả tasks với status COMPLETED
        taskRepository.getTasksByChild(childId, "COMPLETED", null, null, 
                new TaskRepository.TasksCallback() {
            @Override
            public void onSuccess(List<Task> tasks) {
                if (!isAdded()) return;
                
                Log.d(TAG, "Loaded " + tasks.size() + " completed tasks");
                
                // Convert Task to HistoryTask
                allHistoryTasks.clear();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
                
                for (Task task : tasks) {
                    String completionTime = task.getCompletedAt();
                    if (completionTime == null) {
                        completionTime = sdf.format(new Date());
                    }
                    
                    // Xác định icon dựa vào loại task
                    int iconRes = R.drawable.ic_task_habit;
                    if ("EXERCISE".equalsIgnoreCase(task.getTaskType())) {
                        iconRes = R.drawable.ic_task_exercise;
                    }
                    
                    // Rating cho exercise (từ score)
                    float rating = 0f;
                    if (task.getScore() != null) {
                        rating = task.getScore() / 20f; // Convert 0-100 to 0-5
                    }
                    
                    HistoryTask historyTask = new HistoryTask(
                            task.getTitle(),
                            completionTime,
                            task.getCoinsReward(),
                            task.getPointsReward(),
                            rating,
                            iconRes
                    );
                    allHistoryTasks.add(historyTask);
                }
                
                filterTasks();
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                Log.e(TAG, "Error loading history: " + error);
                Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                updateEmptyState();
            }
        });
    }
    
    private void updateEmptyState() {
        if (binding == null) return;
        
        if (filteredTasks.isEmpty()) {
            binding.recyclerViewHistory.setVisibility(View.GONE);
            // TODO: Show empty state view if exists
        } else {
            binding.recyclerViewHistory.setVisibility(View.VISIBLE);
        }
    }
    
    public void refresh() {
        loadHistory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
