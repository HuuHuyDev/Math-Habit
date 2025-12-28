package com.kidsapp.ui.parent.task_plan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.widget.TextView;

import com.kidsapp.R;
import com.kidsapp.data.WeekPlanHelper;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.Task;
import com.kidsapp.data.repository.TaskAssignmentRepository;
import com.kidsapp.databinding.FragmentWeeklyPlanBinding;
import com.kidsapp.ui.components.LoadingDialog;
import com.kidsapp.ui.parent.report.adapter.ReportChildSelectorAdapter;
import com.kidsapp.ui.parent.report.model.Child;
import com.kidsapp.ui.parent.task_plan.bottomsheet.AddTaskBottomSheet;
import com.kidsapp.ui.parent.task_plan.bottomsheet.ConfirmDeleteBottomSheet;
import com.kidsapp.ui.parent.task_plan.bottomsheet.EditTaskBottomSheet;
import com.kidsapp.ui.parent.task_plan.model.WeekDay;
import com.kidsapp.ui.parent.task_plan.model.WeekTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Weekly Plan Fragment - Kế hoạch nhiệm vụ trong tuần của bé
 * Load dữ liệu từ API, hiển thị theo tuần hiện tại
 */
public class WeeklyPlanFragment extends Fragment {
    private FragmentWeeklyPlanBinding binding;
    private WeekDayAdapter weekDayAdapter;
    private TaskAdapter taskAdapter;
    private TaskAssignmentRepository taskRepository;
    private LoadingDialog loadingDialog;

    private List<WeekDay> weekDays = new ArrayList<>();
    private List<WeekTask> allTasks = new ArrayList<>();
    private int selectedDayIndex = 0;
    
    // Loading state
    private boolean isLoading = false;

    // Child info
    private List<Child> childList = new ArrayList<>();
    private Child selectedChild;
    private String childId;
    private String childName;
    private int childLevel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            childId = getArguments().getString("childId");
            childName = getArguments().getString("childName");
            childLevel = getArguments().getInt("childLevel", 1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWeeklyPlanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskRepository = new TaskAssignmentRepository(requireContext());
        loadingDialog = new LoadingDialog(requireContext());
        
        // Mặc định chọn ngày hôm nay
        selectedDayIndex = WeekPlanHelper.getTodayIndex();
        
        setupWeekDaysRecycler();
        setupTasksRecycler();
        setupListeners();
        setupAppBar();
        showLoading();
        loadChildren();
    }
    
    private void showLoading() {
        if (loadingDialog != null) {
            loadingDialog.show("Đang tải...");
        }
    }
    
    private void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    /**
     * Load danh sách bé từ API
     */
    private void loadChildren() {
        childList.clear();
        
        SharedPref sharedPref = new SharedPref(requireContext());
        ApiService apiService = RetrofitClient.getInstance(sharedPref).getApiService();
        
        apiService.getParentChildren().enqueue(new Callback<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> call,
                                   Response<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> response) {
                if (!isAdded()) return;
                
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    List<ApiService.ChildResponse> children = response.body().data;
                    for (ApiService.ChildResponse child : children) {
                        String avatar = getAvatarFromChild(child);
                        int level = child.grade != null ? child.grade : 1;
                        int points = child.totalXp != null ? child.totalXp : 0;
                        childList.add(new Child(child.id, child.name, level, points, avatar));
                    }
                    
                    // Chọn child
                    if (!childList.isEmpty()) {
                        if (childId != null) {
                            for (Child c : childList) {
                                if (c.getId().equals(childId)) {
                                    selectedChild = c;
                                    break;
                                }
                            }
                        }
                        if (selectedChild == null) {
                            selectedChild = childList.get(0);
                        }
                        childId = selectedChild.getId();
                        childName = selectedChild.getName();
                        childLevel = selectedChild.getLevel();
                    }
                    
                    setupChildSelector();
                    loadData();
                } else {
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> call, Throwable t) {
                if (!isAdded()) return;
                hideLoading();
                Toast.makeText(requireContext(), "Không thể tải danh sách bé", Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        });
    }
    
    private String getAvatarFromChild(ApiService.ChildResponse child) {
        if (child.avatarUrl != null && !child.avatarUrl.isEmpty() && !child.avatarUrl.startsWith("http")) {
            return child.avatarUrl;
        }
        return child.gender != null && child.gender ? "👦" : "👧";
    }

    private void setupAppBar() {
        binding.appbar.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void setupChildSelector() {
        View childSelectorLayout = binding.getRoot().findViewById(R.id.childSelector);
        TextView txtSelectedChild = childSelectorLayout.findViewById(R.id.txtSelectedChild);
        TextView imgChildAvatar = childSelectorLayout.findViewById(R.id.imgChildAvatar);
        
        if (selectedChild != null) {
            txtSelectedChild.setText(selectedChild.getName() + " – Lớp " + selectedChild.getLevel());
            imgChildAvatar.setText(selectedChild.getAvatar());
        }
        
        childSelectorLayout.setOnClickListener(v -> showChildBottomSheet());
    }

    private void showChildBottomSheet() {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = 
            new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottomsheet_child_selector, null);
        
        androidx.recyclerview.widget.RecyclerView recyclerChildList = 
            bottomSheetView.findViewById(R.id.recyclerChildList);
        
        ReportChildSelectorAdapter adapter = new ReportChildSelectorAdapter(child -> {
            selectedChild = child;
            childName = child.getName();
            childLevel = child.getLevel();
            childId = child.getId();
            
            View childSelectorLayout = binding.getRoot().findViewById(R.id.childSelector);
            TextView txtSelectedChild = childSelectorLayout.findViewById(R.id.txtSelectedChild);
            TextView imgChildAvatar = childSelectorLayout.findViewById(R.id.imgChildAvatar);
            txtSelectedChild.setText(child.getName() + " – Lớp " + child.getLevel());
            imgChildAvatar.setText(child.getAvatar());
            
            bottomSheetDialog.dismiss();
            loadData();
        });
        
        adapter.setChildList(childList);
        recyclerChildList.setAdapter(adapter);
        
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void setupWeekDaysRecycler() {
        weekDayAdapter = new WeekDayAdapter();
        binding.recyclerWeekDays.setAdapter(weekDayAdapter);

        weekDayAdapter.setOnDayClickListener((weekDay, position) -> {
            selectedDayIndex = position;
            loadTasksForSelectedDay();
        });
    }

    private void setupTasksRecycler() {
        taskAdapter = new TaskAdapter();
        binding.recyclerTasks.setAdapter(taskAdapter);

        taskAdapter.setOnTaskActionListener(new TaskAdapter.OnTaskActionListener() {
            @Override
            public void onEditTask(WeekTask task, int position) {
                // Không cho sửa task của ngày đã qua
                if (weekDays.get(selectedDayIndex).isPast()) {
                    Toast.makeText(requireContext(), "Không thể sửa nhiệm vụ của ngày đã qua", Toast.LENGTH_SHORT).show();
                    return;
                }
                showEditTaskBottomSheet(task, position);
            }

            @Override
            public void onDeleteTask(WeekTask task, int position) {
                showDeleteConfirmation(position);
            }
        });
    }

    private void setupListeners() {
        binding.btnAddTask.setOnClickListener(v -> {
            // Không cho thêm task vào ngày đã qua
            if (weekDays.size() > selectedDayIndex && weekDays.get(selectedDayIndex).isPast()) {
                Toast.makeText(requireContext(), "Không thể thêm nhiệm vụ vào ngày đã qua", Toast.LENGTH_SHORT).show();
                return;
            }
            showAddTaskBottomSheet();
        });
    }

    /**
     * Load dữ liệu từ API - Tích hợp 3 luồng: Tasks, Habits, Quizzes
     */
    private void loadData() {
        // Load week days với thông tin isPast, isToday
        weekDays = WeekPlanHelper.getWeekDays();
        weekDayAdapter.setWeekDays(weekDays);
        weekDayAdapter.setSelectedPosition(selectedDayIndex);
        
        if (childId == null || childId.isEmpty()) {
            showEmptyState();
            return;
        }
        
        // Show loading state
        isLoading = true;
        
        // Clear existing tasks
        allTasks.clear();
        
        // Load từ 3 API song song
        SharedPref sharedPref = new SharedPref(requireContext());
        ApiService apiService = RetrofitClient.getInstance(sharedPref).getApiService();
        
        // Use demo data for now since API methods are not available
        hideLoading();
        allTasks = WeekPlanHelper.getDemoTasks(childId);
        WeekPlanHelper.updateWeekDaysStats(weekDays, allTasks);
        weekDayAdapter.notifyDataSetChanged();
        loadTasksForSelectedDay();
        updateWeekSummary();
    }
    
    /**
     * Convert API HabitResponse to WeekTask
     */
    private WeekTask convertHabitToWeekTask(ApiService.HabitResponse habit) {
        if (habit == null) return null;
        
        // Determine task type for UI
        String type = "habit";
        
        // Calculate day index - use today since no dueDate available
        int dayIndex = WeekPlanHelper.getTodayIndex();
        
        WeekTask weekTask = new WeekTask(
                habit.id,
                habit.title != null ? habit.title : "Thói quen",
                habit.description != null ? habit.description : "",
                type,
                habit.coinReward != null ? habit.coinReward : 5,
                habit.xpReward != null ? habit.xpReward : 10,
                dayIndex
        );
        
        // Set status based on today completion
        if (habit.completedToday != null && habit.completedToday) {
            weekTask.setCompleted(true);
        }
        
        return weekTask;
    }
    
    private int calculateDayIndexFromDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return 0;
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            java.util.Date date = sdf.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            int dayIndex = dayOfWeek - 2;
            if (dayIndex < 0) dayIndex = 6;
            return dayIndex;
        } catch (Exception e) {
            return 0;
        }
    }

    private void loadTasksForSelectedDay() {
        List<WeekTask> dayTasks = WeekPlanHelper.getTasksByDay(allTasks, selectedDayIndex);
        taskAdapter.setTasks(dayTasks);

        if (dayTasks.isEmpty()) {
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
            binding.recyclerTasks.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyState.setVisibility(View.GONE);
            binding.recyclerTasks.setVisibility(View.VISIBLE);
        }

        String[] dayNames = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
        String title = "Nhiệm vụ " + dayNames[selectedDayIndex];
        
        // Thêm indicator cho ngày đã qua/hôm nay
        if (weekDays.size() > selectedDayIndex) {
            if (weekDays.get(selectedDayIndex).isToday()) {
                title += " (Hôm nay)";
            } else if (weekDays.get(selectedDayIndex).isPast()) {
                title += " (Đã qua)";
            }
        }
        
        binding.txtTasksTitle.setText(title);
        binding.txtTaskCount.setText(dayTasks.size() + " nhiệm vụ");
    }

    private void updateWeekSummary() {
        int totalTasks = allTasks.size();
        int habitCount = 0;
        int quizCount = 0;
        int completedTasks = 0;

        for (WeekTask task : allTasks) {
            if (task.isHabit()) habitCount++;
            if (task.isQuiz()) quizCount++;
            if (task.isCompleted()) completedTasks++;
        }

        int progress = totalTasks > 0 ? (completedTasks * 100 / totalTasks) : 0;

        binding.includeWeekSummary.progressWeek.setProgress(progress);
        binding.includeWeekSummary.txtWeekProgress.setText(progress + "%");
        binding.includeWeekSummary.txtTotalTasks.setText(totalTasks + " nhiệm vụ");
        binding.includeWeekSummary.txtHabitCount.setText(habitCount + " thói quen");
        binding.includeWeekSummary.txtQuizCount.setText(quizCount + " bài tập");

        String suggestion;
        if (progress >= 80) {
            suggestion = "💪 Bạn đang duy trì rất tốt!";
        } else if (progress >= 50) {
            suggestion = "👍 Tiếp tục cố gắng nhé!";
        } else if (totalTasks == 0) {
            suggestion = "📝 Hãy thêm nhiệm vụ cho bé!";
        } else {
            suggestion = "🌟 Hãy hoàn thành thêm nhiệm vụ!";
        }
        binding.includeWeekSummary.txtSuggestion.setText(suggestion);
    }
    
    private void showEmptyState() {
        hideLoading();
        weekDays = WeekPlanHelper.getWeekDays();
        weekDayAdapter.setWeekDays(weekDays);
        weekDayAdapter.setSelectedPosition(selectedDayIndex);
        allTasks.clear();
        WeekPlanHelper.updateWeekDaysStats(weekDays, allTasks);
        loadTasksForSelectedDay();
        updateWeekSummary();
    }

    private void showAddTaskBottomSheet() {
        if (childId == null || childId.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chọn bé trước", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AddTaskBottomSheet bottomSheet = AddTaskBottomSheet.newInstance(selectedDayIndex, childId);
        bottomSheet.setOnTaskAddedListener(task -> {
            allTasks.add(task);
            WeekPlanHelper.updateWeekDaysStats(weekDays, allTasks);
            weekDayAdapter.notifyItemChanged(selectedDayIndex);
            loadTasksForSelectedDay();
            updateWeekSummary();
        });
        bottomSheet.show(getChildFragmentManager(), "AddTaskBottomSheet");
    }

    private void showEditTaskBottomSheet(WeekTask task, int position) {
        EditTaskBottomSheet bottomSheet = EditTaskBottomSheet.newInstance(task, position);
        bottomSheet.setOnTaskUpdatedListener((updatedTask, pos) -> {
            taskAdapter.updateTask(pos, updatedTask);
            WeekPlanHelper.updateWeekDaysStats(weekDays, allTasks);
            weekDayAdapter.notifyItemChanged(selectedDayIndex);
            updateWeekSummary();
        });
        bottomSheet.show(getChildFragmentManager(), "EditTaskBottomSheet");
    }

    private void showDeleteConfirmation(int position) {
        ConfirmDeleteBottomSheet bottomSheet = ConfirmDeleteBottomSheet.newInstance();
        bottomSheet.setOnDeleteConfirmedListener(() -> {
            List<WeekTask> dayTasks = WeekPlanHelper.getTasksByDay(allTasks, selectedDayIndex);
            if (position >= 0 && position < dayTasks.size()) {
                WeekTask taskToRemove = dayTasks.get(position);
                String taskId = taskToRemove.getId();
                
                if (taskId != null && !taskId.isEmpty()) {
                    taskRepository.deleteTask(taskId, new TaskAssignmentRepository.OnDeleteTaskCallback() {
                        @Override
                        public void onSuccess() {
                            if (!isAdded()) return;
                            allTasks.remove(taskToRemove);
                            taskAdapter.removeTask(position);
                            WeekPlanHelper.updateWeekDaysStats(weekDays, allTasks);
                            weekDayAdapter.notifyItemChanged(selectedDayIndex);
                            loadTasksForSelectedDay();
                            updateWeekSummary();
                            Toast.makeText(requireContext(), "Đã xóa nhiệm vụ", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String message) {
                            if (!isAdded()) return;
                            Toast.makeText(requireContext(), "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        bottomSheet.show(getChildFragmentManager(), "ConfirmDeleteBottomSheet");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideLoading();
        loadingDialog = null;
        binding = null;
    }
}