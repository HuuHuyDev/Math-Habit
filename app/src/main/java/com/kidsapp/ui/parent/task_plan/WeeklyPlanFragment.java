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
import com.kidsapp.data.FakeWeekPlanRepository;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.Task;
import com.kidsapp.data.repository.TaskAssignmentRepository;
import com.kidsapp.databinding.FragmentWeeklyPlanBinding;
import com.kidsapp.ui.parent.report.adapter.ReportChildSelectorAdapter;
import com.kidsapp.ui.parent.report.model.Child;
import com.kidsapp.ui.parent.task_plan.bottomsheet.AddTaskBottomSheet;
import com.kidsapp.ui.parent.task_plan.bottomsheet.ConfirmDeleteBottomSheet;
import com.kidsapp.ui.parent.task_plan.bottomsheet.ConfirmSaveBottomSheet;
import com.kidsapp.ui.parent.task_plan.bottomsheet.EditTaskBottomSheet;
import com.kidsapp.ui.parent.task_plan.model.WeekDay;
import com.kidsapp.ui.parent.task_plan.model.WeekTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Weekly Plan Fragment - Kế hoạch nhiệm vụ trong tuần của bé
 */
public class WeeklyPlanFragment extends Fragment {
    private FragmentWeeklyPlanBinding binding;
    private WeekDayAdapter weekDayAdapter;
    private TaskAdapter taskAdapter;
    private TaskAssignmentRepository taskRepository;

    private List<WeekDay> weekDays = new ArrayList<>();
    private List<WeekTask> allTasks = new ArrayList<>();
    private int selectedDayIndex = 0;
    
    // Loading state
    private boolean isLoading = false;

    // Child info
    private List<Child> childList = new ArrayList<>();
    private Child selectedChild;
    private String childId;
    private String childName = "Bé Minh";
    private int childLevel = 3;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Nhận dữ liệu từ bundle
        if (getArguments() != null) {
            childId = getArguments().getString("childId");
            childName = getArguments().getString("childName", "Bé Minh");
            childLevel = getArguments().getInt("childLevel", 3);
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
        setupWeekDaysRecycler();
        setupTasksRecycler();
        setupListeners();
        setupAppBar();
        initDemoChildren(); // Load children từ API
    }

    /**
     * Khởi tạo danh sách bé từ API
     */
    private void initDemoChildren() {
        childList.clear();
        
        // Load children từ API
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
                        String avatar = child.gender != null && child.gender ? "👦" : "👧";
                        int level = child.grade != null ? child.grade : 1;
                        int points = child.totalPoints != null ? child.totalPoints : 0;
                        childList.add(new Child(child.id, child.name, level, points, avatar));
                    }
                    
                    // Chọn child đầu tiên nếu chưa có
                    if (selectedChild == null && !childList.isEmpty()) {
                        selectedChild = childList.get(0);
                        childId = selectedChild.getId();
                        childName = selectedChild.getName();
                        childLevel = selectedChild.getLevel();
                    } else if (childId != null) {
                        // Tìm child theo childId từ arguments
                        for (Child c : childList) {
                            if (c.getId().equals(childId)) {
                                selectedChild = c;
                                childName = c.getName();
                                childLevel = c.getLevel();
                                break;
                            }
                        }
                    }
                    
                    // Cập nhật UI
                    setupChildSelector();
                    loadData();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> call, Throwable t) {
                if (!isAdded()) return;
                // Fallback to demo data
                childList.add(new Child("1", "Bé Demo", 3, 1200, "👦"));
                selectedChild = childList.get(0);
                childId = selectedChild.getId();
                setupChildSelector();
                loadData();
            }
        });
    }
    private void setupAppBar() {
        // Sự kiện click nút Back - gọi onBackPressed của Activity để xử lý logic
        binding.appbar.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }
    /**
     * Setup Child Selector - Hiển thị thông tin bé
     */
    private void setupChildSelector() {
        // Tìm các view trong layout_report_child_selector
        View childSelectorLayout = binding.getRoot().findViewById(R.id.childSelector);
        TextView txtSelectedChild = childSelectorLayout.findViewById(R.id.txtSelectedChild);
        TextView imgChildAvatar = childSelectorLayout.findViewById(R.id.imgChildAvatar);
        
        // Hiển thị thông tin bé được chọn
        if (selectedChild != null) {
            txtSelectedChild.setText(selectedChild.getName() + " – Lớp " + selectedChild.getLevel());
            imgChildAvatar.setText(selectedChild.getAvatar());
        }
        
        // Click để mở BottomSheet chọn bé
        childSelectorLayout.setOnClickListener(v -> showChildBottomSheet());
    }

    /**
     * Hiển thị BottomSheet chọn bé
     */
    private void showChildBottomSheet() {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = 
            new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottomsheet_child_selector, null);
        
        androidx.recyclerview.widget.RecyclerView recyclerChildList = 
            bottomSheetView.findViewById(R.id.recyclerChildList);
        
        // Tạo adapter cho danh sách bé (sử dụng lại từ report)
        ReportChildSelectorAdapter adapter = new ReportChildSelectorAdapter(child -> {
            // Cập nhật selected child
            selectedChild = child;
            childName = child.getName();
            childLevel = child.getLevel();
            childId = child.getId();
            
            // Cập nhật UI
            View childSelectorLayout = binding.getRoot().findViewById(R.id.childSelector);
            TextView txtSelectedChild = childSelectorLayout.findViewById(R.id.txtSelectedChild);
            TextView imgChildAvatar = childSelectorLayout.findViewById(R.id.imgChildAvatar);
            txtSelectedChild.setText(child.getName() + " – Lớp " + child.getLevel());
            imgChildAvatar.setText(child.getAvatar());
            
            // Đóng bottom sheet
            bottomSheetDialog.dismiss();
            
            // Tải lại dữ liệu cho bé mới
            loadData();
        });
        
        adapter.setChildList(childList);
        recyclerChildList.setAdapter(adapter);
        
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    /**
     * Setup RecyclerView cho danh sách ngày trong tuần
     */
    private void setupWeekDaysRecycler() {
        weekDayAdapter = new WeekDayAdapter();
        binding.recyclerWeekDays.setAdapter(weekDayAdapter);

        weekDayAdapter.setOnDayClickListener((weekDay, position) -> {
            selectedDayIndex = position;
            loadTasksForSelectedDay();
        });
    }

    /**
     * Setup RecyclerView cho danh sách nhiệm vụ
     */
    private void setupTasksRecycler() {
        taskAdapter = new TaskAdapter();
        binding.recyclerTasks.setAdapter(taskAdapter);

        taskAdapter.setOnTaskActionListener(new TaskAdapter.OnTaskActionListener() {
            @Override
            public void onEditTask(WeekTask task, int position) {
                showEditTaskBottomSheet(task, position);
            }

            @Override
            public void onDeleteTask(WeekTask task, int position) {
                showDeleteConfirmation(position);
            }
        });
    }

    /**
     * Setup các listeners cho buttons
     */
    private void setupListeners() {
        // Nút Back từ appbar (view_parent_weekplan_child_appbar)
        // TODO: Kiểm tra ID button trong appbar layout
        // binding.appbar.btnBack.setOnClickListener(v -> {
        //     if (getActivity() != null) {
        //         getActivity().onBackPressed();
        //     }
        // });

        // Nút Thêm nhiệm vụ
        binding.btnAddTask.setOnClickListener(v -> showAddTaskBottomSheet());

        // Nút Lưu kế hoạch tuần
        binding.fabSaveWeek.setOnClickListener(v -> showSaveConfirmation());
    }

    /**
     * Load dữ liệu từ API - Tích hợp 3 luồng: Tasks, Habits, Quizzes
     */
    private void loadData() {
        // Load week days (static)
        weekDays = FakeWeekPlanRepository.getWeekDays();
        weekDayAdapter.setWeekDays(weekDays);
        weekDayAdapter.setSelectedPosition(selectedDayIndex);
        
        if (childId == null || childId.isEmpty()) {
            // Fallback to demo data if no child selected
            allTasks = FakeWeekPlanRepository.getDemoTasks(childId);
            FakeWeekPlanRepository.updateWeekDaysStats(weekDays, allTasks);
            loadTasksForSelectedDay();
            updateWeekSummary();
            return;
        }
        
        // Show loading state
        isLoading = true;
        
        // Clear existing tasks
        allTasks.clear();
        
        // Load từ 3 API song song
        SharedPref sharedPref = new SharedPref(requireContext());
        ApiService apiService = RetrofitClient.getInstance(sharedPref).getApiService();
        
        // Counter để đợi cả 3 API hoàn thành
        final int[] completedCalls = {0};
        final int totalCalls = 3;
        
        // 1. Load Tasks (housework, custom tasks)
        apiService.getTasksByChild(childId).enqueue(new Callback<ApiService.ApiResponseWrapper<List<Task>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<Task>>> call,
                                   Response<ApiService.ApiResponseWrapper<List<Task>>> response) {
                if (!isAdded()) return;
                
                if (response.isSuccessful() && response.body() != null && response.body().success && response.body().data != null) {
                    for (Task task : response.body().data) {
                        WeekTask weekTask = convertTaskToWeekTask(task);
                        if (weekTask != null) {
                            allTasks.add(weekTask);
                        }
                    }
                }
                
                checkAndUpdateUI(++completedCalls[0], totalCalls);
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<Task>>> call, Throwable t) {
                if (!isAdded()) return;
                checkAndUpdateUI(++completedCalls[0], totalCalls);
            }
        });
        
        // 2. Load Habits
        apiService.getHabitsByChild(childId, null).enqueue(new Callback<ApiService.ApiResponseWrapper<List<ApiService.HabitResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.HabitResponse>>> call,
                                   Response<ApiService.ApiResponseWrapper<List<ApiService.HabitResponse>>> response) {
                if (!isAdded()) return;
                
                if (response.isSuccessful() && response.body() != null && response.body().success && response.body().data != null) {
                    for (ApiService.HabitResponse habit : response.body().data) {
                        WeekTask weekTask = convertHabitToWeekTask(habit);
                        if (weekTask != null) {
                            allTasks.add(weekTask);
                        }
                    }
                }
                
                checkAndUpdateUI(++completedCalls[0], totalCalls);
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.HabitResponse>>> call, Throwable t) {
                if (!isAdded()) return;
                checkAndUpdateUI(++completedCalls[0], totalCalls);
            }
        });
        
        // 3. Load Quizzes
        apiService.getQuizzesByChild(childId, null).enqueue(new Callback<ApiService.ApiResponseWrapper<List<ApiService.QuizResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.QuizResponse>>> call,
                                   Response<ApiService.ApiResponseWrapper<List<ApiService.QuizResponse>>> response) {
                if (!isAdded()) return;
                
                if (response.isSuccessful() && response.body() != null && response.body().success && response.body().data != null) {
                    for (ApiService.QuizResponse quiz : response.body().data) {
                        WeekTask weekTask = convertQuizToWeekTask(quiz);
                        if (weekTask != null) {
                            allTasks.add(weekTask);
                        }
                    }
                }
                
                checkAndUpdateUI(++completedCalls[0], totalCalls);
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.QuizResponse>>> call, Throwable t) {
                if (!isAdded()) return;
                checkAndUpdateUI(++completedCalls[0], totalCalls);
            }
        });
    }
    
    /**
     * Kiểm tra và cập nhật UI khi tất cả API calls hoàn thành
     */
    private void checkAndUpdateUI(int completed, int total) {
        if (completed >= total) {
            // Tất cả API đã hoàn thành
            isLoading = false;
            
            // Cập nhật UI
            FakeWeekPlanRepository.updateWeekDaysStats(weekDays, allTasks);
            weekDayAdapter.notifyDataSetChanged();
            loadTasksForSelectedDay();
            updateWeekSummary();
            
            // Show success message
            if (allTasks.isEmpty()) {
                Toast.makeText(requireContext(), "Chưa có nhiệm vụ nào. Hãy thêm nhiệm vụ mới!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Đã tải " + allTasks.size() + " nhiệm vụ", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    /**
     * Convert API Task to WeekTask
     */
    private WeekTask convertTaskToWeekTask(Task task) {
        if (task == null) return null;
        
        // Determine task type for UI
        String type = "habit"; // Default for housework/custom tasks
        if ("exercise".equals(task.getTaskType())) {
            type = "quiz";
        }
        
        // Calculate day index from dueDate
        int dayIndex = calculateDayIndexFromDate(task.getDueDate());
        
        WeekTask weekTask = new WeekTask(
                task.getId(),
                task.getTitle() != null ? task.getTitle() : "Nhiệm vụ",
                task.getDescription() != null ? task.getDescription() : "",
                type,
                task.getPointsReward(),
                task.getPointsReward() / 2,
                dayIndex
        );
        
        // Set status
        if ("completed".equals(task.getStatus()) || "verified".equals(task.getStatus())) {
            weekTask.setCompleted(true);
        }
        
        weekTask.setLevel(task.getPriority());
        
        return weekTask;
    }
    
    /**
     * Convert API HabitResponse to WeekTask
     */
    private WeekTask convertHabitToWeekTask(ApiService.HabitResponse habit) {
        if (habit == null) return null;
        
        // Habits are recurring, so we add them to today or based on reminder time
        int dayIndex = calculateDayIndexFromDate(java.time.LocalDate.now().toString());
        
        WeekTask weekTask = new WeekTask(
                habit.id,
                habit.title != null ? habit.title : "Thói quen",
                habit.description != null ? habit.description : "",
                "habit",
                habit.xpReward != null ? habit.xpReward : 10,
                habit.coinReward != null ? habit.coinReward : 5,
                dayIndex
        );
        
        // Set status
        if (habit.completedToday != null && habit.completedToday) {
            weekTask.setCompleted(true);
        }
        
        // Set level based on streak
        if (habit.currentStreak != null) {
            if (habit.currentStreak >= 7) {
                weekTask.setLevel(3); // High
            } else if (habit.currentStreak >= 3) {
                weekTask.setLevel(2); // Medium
            } else {
                weekTask.setLevel(1); // Low
            }
        }
        
        return weekTask;
    }
    
    /**
     * Convert API QuizResponse to WeekTask
     */
    private WeekTask convertQuizToWeekTask(ApiService.QuizResponse quiz) {
        if (quiz == null) return null;
        
        // Calculate day index from deadline
        int dayIndex = calculateDayIndexFromDate(quiz.deadline);
        
        WeekTask weekTask = new WeekTask(
                quiz.id,
                quiz.title != null ? quiz.title : "Bài tập",
                quiz.description != null ? quiz.description : "",
                "quiz",
                quiz.xpReward != null ? quiz.xpReward : 20,
                quiz.coinReward != null ? quiz.coinReward : 10,
                dayIndex
        );
        
        // Set status
        if ("completed".equals(quiz.status)) {
            weekTask.setCompleted(true);
        }
        
        // Set level based on difficulty
        if ("hard".equals(quiz.level)) {
            weekTask.setLevel(3);
        } else if ("medium".equals(quiz.level)) {
            weekTask.setLevel(2);
        } else {
            weekTask.setLevel(1);
        }
        
        return weekTask;
    }
    
    /**
     * Calculate day index (0=Monday, 6=Sunday) from date string
     */
    private int calculateDayIndexFromDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return 0;
        
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.util.Date date = sdf.parse(dateStr);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(date);
            
            int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
            // Calendar: Sunday=1, Monday=2, ..., Saturday=7
            // We want: Monday=0, Tuesday=1, ..., Sunday=6
            int dayIndex = dayOfWeek - 2;
            if (dayIndex < 0) dayIndex = 6; // Sunday
            return dayIndex;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Load nhiệm vụ cho ngày được chọn
     */
    private void loadTasksForSelectedDay() {
        List<WeekTask> dayTasks = FakeWeekPlanRepository.getTasksByDay(allTasks, selectedDayIndex);
        taskAdapter.setTasks(dayTasks);

        // Hiển thị empty state nếu không có task
        if (dayTasks.isEmpty()) {
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
            binding.recyclerTasks.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyState.setVisibility(View.GONE);
            binding.recyclerTasks.setVisibility(View.VISIBLE);
        }

        // Cập nhật title và số lượng nhiệm vụ
        String[] dayNames = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
        binding.txtTasksTitle.setText("Nhiệm vụ " + dayNames[selectedDayIndex]);
        binding.txtTaskCount.setText(dayTasks.size() + " nhiệm vụ");
    }

    /**
     * Cập nhật tổng quan tuần
     */
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

        // Gợi ý động
        String suggestion;
        if (progress >= 80) {
            suggestion = "💪 Bạn đang duy trì rất tốt!";
        } else if (progress >= 50) {
            suggestion = "👍 Tiếp tục cố gắng nhé!";
        } else {
            suggestion = "🌟 Hãy hoàn thành thêm nhiệm vụ!";
        }
        binding.includeWeekSummary.txtSuggestion.setText(suggestion);
    }

    /**
     * Hiển thị BottomSheet thêm nhiệm vụ - Truyền childId để gọi API
     */
    private void showAddTaskBottomSheet() {
        if (childId == null || childId.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chọn bé trước", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AddTaskBottomSheet bottomSheet = AddTaskBottomSheet.newInstance(selectedDayIndex, childId);
        bottomSheet.setOnTaskAddedListener(task -> {
            allTasks.add(task);
            FakeWeekPlanRepository.updateWeekDaysStats(weekDays, allTasks);
            weekDayAdapter.notifyItemChanged(selectedDayIndex);
            loadTasksForSelectedDay();
            updateWeekSummary();
        });
        bottomSheet.show(getChildFragmentManager(), "AddTaskBottomSheet");
    }

    /**
     * Hiển thị BottomSheet sửa nhiệm vụ
     */
    private void showEditTaskBottomSheet(WeekTask task, int position) {
        EditTaskBottomSheet bottomSheet = EditTaskBottomSheet.newInstance(task, position);
        bottomSheet.setOnTaskUpdatedListener((updatedTask, pos) -> {
            // Gọi API cập nhật task
            if (updatedTask.getId() != null && !updatedTask.getId().isEmpty()) {
                ApiService.UpdateTaskRequest request = new ApiService.UpdateTaskRequest();
                request.pointsReward = updatedTask.getCoins();
                
                taskRepository.updateTask(updatedTask.getId(), request, new TaskAssignmentRepository.OnUpdateTaskCallback() {
                    @Override
                    public void onSuccess(ApiService.TaskAssignmentResponse response) {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> {
                            taskAdapter.updateTask(pos, updatedTask);
                            FakeWeekPlanRepository.updateWeekDaysStats(weekDays, allTasks);
                            weekDayAdapter.notifyItemChanged(selectedDayIndex);
                            updateWeekSummary();
                            Toast.makeText(requireContext(), "Đã cập nhật nhiệm vụ", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onError(String message) {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> {
                            // Vẫn cập nhật local nếu API lỗi
                            taskAdapter.updateTask(pos, updatedTask);
                            FakeWeekPlanRepository.updateWeekDaysStats(weekDays, allTasks);
                            weekDayAdapter.notifyItemChanged(selectedDayIndex);
                            updateWeekSummary();
                            Toast.makeText(requireContext(), "Đã cập nhật (offline)", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            } else {
                // Task chưa có ID (local only)
                taskAdapter.updateTask(pos, updatedTask);
                FakeWeekPlanRepository.updateWeekDaysStats(weekDays, allTasks);
                weekDayAdapter.notifyItemChanged(selectedDayIndex);
                updateWeekSummary();
                Toast.makeText(requireContext(), "Đã cập nhật nhiệm vụ", Toast.LENGTH_SHORT).show();
            }
        });
        bottomSheet.show(getChildFragmentManager(), "EditTaskBottomSheet");
    }

    /**
     * Hiển thị xác nhận xóa nhiệm vụ
     */
    private void showDeleteConfirmation(int position) {
        ConfirmDeleteBottomSheet bottomSheet = ConfirmDeleteBottomSheet.newInstance();
        bottomSheet.setOnDeleteConfirmedListener(() -> {
            List<WeekTask> dayTasks = FakeWeekPlanRepository.getTasksByDay(allTasks, selectedDayIndex);
            if (position >= 0 && position < dayTasks.size()) {
                WeekTask taskToRemove = dayTasks.get(position);
                
                // Gọi API xóa task nếu có ID
                if (taskToRemove.getId() != null && !taskToRemove.getId().isEmpty()) {
                    taskRepository.deleteTask(taskToRemove.getId(), new TaskAssignmentRepository.OnDeleteTaskCallback() {
                        @Override
                        public void onSuccess() {
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() -> {
                                allTasks.remove(taskToRemove);
                                taskAdapter.removeTask(position);
                                FakeWeekPlanRepository.updateWeekDaysStats(weekDays, allTasks);
                                weekDayAdapter.notifyItemChanged(selectedDayIndex);
                                loadTasksForSelectedDay();
                                updateWeekSummary();
                                Toast.makeText(requireContext(), "Đã xóa nhiệm vụ", Toast.LENGTH_SHORT).show();
                            });
                        }

                        @Override
                        public void onError(String message) {
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                } else {
                    // Task local only
                    allTasks.remove(taskToRemove);
                    taskAdapter.removeTask(position);
                    FakeWeekPlanRepository.updateWeekDaysStats(weekDays, allTasks);
                    weekDayAdapter.notifyItemChanged(selectedDayIndex);
                    loadTasksForSelectedDay();
                    updateWeekSummary();
                    Toast.makeText(requireContext(), "Đã xóa nhiệm vụ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bottomSheet.show(getChildFragmentManager(), "ConfirmDeleteBottomSheet");
    }

    /**
     * Hiển thị xác nhận lưu kế hoạch tuần
     */
    private void showSaveConfirmation() {
        ConfirmSaveBottomSheet bottomSheet = ConfirmSaveBottomSheet.newInstance();
        bottomSheet.setOnSaveConfirmedListener(() -> {
            // TODO: Lưu kế hoạch tuần vào database/API
            Toast.makeText(requireContext(),
                    "Đã lưu kế hoạch tuần cho " + childName, Toast.LENGTH_SHORT).show();
        });
        bottomSheet.show(getChildFragmentManager(), "ConfirmSaveBottomSheet");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

