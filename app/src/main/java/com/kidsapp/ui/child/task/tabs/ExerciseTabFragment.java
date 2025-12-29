package com.kidsapp.ui.child.task.tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.R;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.Task;
import com.kidsapp.data.repository.TaskRepository;
import com.kidsapp.databinding.FragmentExerciseTabBinding;
import com.kidsapp.ui.child.task.adapter.ExerciseTaskAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Tab Bài tập - Hiển thị các EXERCISE tasks được giao
 * Load từ API: GET /tasks/child/{childId}?taskType=EXERCISE
 */
public class ExerciseTabFragment extends Fragment {
    
    private static final String TAG = "ExerciseTabFragment";
    
    private FragmentExerciseTabBinding binding;
    private ExerciseTaskAdapter adapter;
    private TaskRepository taskRepository;
    private SharedPref sharedPref;

    public ExerciseTabFragment() {}

    public static ExerciseTabFragment newInstance(String param1, String param2) {
        return new ExerciseTabFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExerciseTabBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Init repository
        sharedPref = new SharedPref(requireContext());
        taskRepository = new TaskRepository(requireContext());
        
        setupRecyclerView();
        setupSwipeRefresh();
        loadTasks();
        
        // Lắng nghe kết quả từ PracticeFragment để refresh danh sách
        // Dùng activity's fragment manager vì PracticeFragment được add vào activity
        requireActivity().getSupportFragmentManager().setFragmentResultListener("exercise_completed", 
            getViewLifecycleOwner(), (requestKey, result) -> {
                Log.d(TAG, "Exercise completed, refreshing list...");
                loadTasks();
            });
    }
    
    private void setupRecyclerView() {
        binding.recyclerExercise.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        adapter = new ExerciseTaskAdapter();
        adapter.setOnTaskClickListener(this::openExerciseDetail);
        binding.recyclerExercise.setAdapter(adapter);
    }
    
    private void setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary);
        binding.swipeRefresh.setOnRefreshListener(this::loadTasks);
    }
    
    /**
     * Load EXERCISE tasks từ API
     */
    private void loadTasks() {
        showLoading();
        
        // Ưu tiên childId, fallback sang userId nếu chưa có
        String childId = sharedPref.getChildId();
        if (childId == null || childId.isEmpty()) {
            childId = sharedPref.getUserId();
        }
        
        if (childId == null || childId.isEmpty()) {
            Log.e(TAG, "Child ID and User ID not found");
            showError("Không tìm thấy thông tin tài khoản. Vui lòng đăng nhập lại.");
            return;
        }
        
        Log.d(TAG, "Loading EXERCISE tasks for child: " + childId);
        
        final String finalChildId = childId;
        
        // Lấy ngày hôm nay để filter
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(new java.util.Date());
        
        // Load EXERCISE tasks của ngày hôm nay
        taskRepository.getTasksByChild(childId, null, "EXERCISE", today, 
                new TaskRepository.TasksCallback() {
            @Override
            public void onSuccess(List<Task> allTasks) {
                if (!isAdded()) return;
                
                Log.d(TAG, "Loaded " + allTasks.size() + " EXERCISE tasks");
                
                // Filter tasks cần hiển thị:
                // - PENDING: chưa làm
                // - COMPLETED: đã hoàn thành
                List<Task> exerciseTasks = new ArrayList<>();
                for (Task task : allTasks) {
                    String status = task.getStatus();
                    if (status == null) continue;
                    
                    // Hiển thị PENDING và COMPLETED
                    if ("PENDING".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
                        exerciseTasks.add(task);
                    }
                }
                
                Log.d(TAG, "Filtered " + exerciseTasks.size() + " EXERCISE tasks (pending + completed)");
                
                if (exerciseTasks.isEmpty()) {
                    showEmptyState();
                } else {
                    showTasks(exerciseTasks);
                }
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                Log.e(TAG, "Error loading tasks: " + error);
                showError(error);
            }
        });
    }
    
    private void showLoading() {
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.recyclerExercise.setVisibility(View.GONE);
        binding.progressLoading.setVisibility(View.VISIBLE);
    }
    
    private void showTasks(List<Task> tasks) {
        binding.progressLoading.setVisibility(View.GONE);
        binding.swipeRefresh.setRefreshing(false);
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.recyclerExercise.setVisibility(View.VISIBLE);
        adapter.setTasks(tasks);
    }
    
    private void showEmptyState() {
        binding.progressLoading.setVisibility(View.GONE);
        binding.swipeRefresh.setRefreshing(false);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
        binding.recyclerExercise.setVisibility(View.GONE);
    }
    
    private void showError(String message) {
        binding.progressLoading.setVisibility(View.GONE);
        binding.swipeRefresh.setRefreshing(false);
        showEmptyState();
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Mở màn hình làm bài tập
     * Hiển thị BottomSheet để chọn chế độ: Luyện tập hoặc Kiểm tra
     */
    private void openExerciseDetail(Task task) {
        String exerciseId = task.getExerciseId();
        String taskId = task.getId();
        String title = task.getTitle();
        int pointsReward = task.getPointsReward();
        int coinsReward = task.getCoinsReward();
        
        if (exerciseId == null || exerciseId.isEmpty()) {
            Toast.makeText(requireContext(), "Bài tập chưa được cấu hình", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Kiểm tra trạng thái task
        if ("COMPLETED".equalsIgnoreCase(task.getStatus())) {
            Toast.makeText(requireContext(), "Bạn đã hoàn thành bài tập này!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Hiển thị BottomSheet chọn chế độ
        com.kidsapp.ui.child.task.exercise.ExerciseModeBottomSheet bottomSheet = 
            com.kidsapp.ui.child.task.exercise.ExerciseModeBottomSheet.newInstance(title, 10, 0);
        
        bottomSheet.setModeListener(mode -> {
            if (mode == com.kidsapp.ui.child.task.exercise.ExerciseModeBottomSheet.Mode.PRACTICE) {
                // Chế độ Luyện tập - KHÔNG cộng coin/xp
                openPracticeMode(exerciseId, title);
            } else {
                // Chế độ Kiểm tra - Đúng 100% mới cộng coin/xp
                openExamMode(exerciseId, title, taskId, pointsReward, coinsReward);
            }
        });
        
        bottomSheet.show(getChildFragmentManager(), "ExerciseModeBottomSheet");
    }
    
    /**
     * Mở chế độ Luyện tập - KHÔNG cộng coin/xp
     */
    private void openPracticeMode(String exerciseId, String title) {
        com.kidsapp.ui.child.practice.PracticeFragment fragment = 
            com.kidsapp.ui.child.practice.PracticeFragment.newInstance(exerciseId, title);
        
        // KHÔNG truyền taskId → không gọi API complete
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.childHomeHost, fragment)
                .addToBackStack(null)
                .commit();
    }
    
    /**
     * Mở chế độ Kiểm tra - Đúng 100% mới cộng coin/xp
     */
    private void openExamMode(String exerciseId, String title, String taskId, int pointsReward, int coinsReward) {
        com.kidsapp.ui.child.quizz.ExamFragment fragment = 
            com.kidsapp.ui.child.quizz.ExamFragment.newInstance(exerciseId, title);
        
        // Truyền taskId, pointsReward và coinsReward để complete task khi đúng 100%
        Bundle args = fragment.getArguments();
        if (args == null) args = new Bundle();
        args.putString("taskId", taskId);
        args.putInt("pointsReward", pointsReward);
        args.putInt("coinsReward", coinsReward);
        fragment.setArguments(args);
        
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.childHomeHost, fragment)
                .addToBackStack(null)
                .commit();
    }
    
    public void refresh() {
        loadTasks();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
