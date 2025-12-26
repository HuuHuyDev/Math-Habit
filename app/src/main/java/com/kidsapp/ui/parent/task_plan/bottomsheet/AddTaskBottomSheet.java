package com.kidsapp.ui.parent.task_plan.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.repository.TaskAssignmentRepository;
import com.kidsapp.databinding.BottomsheetAddTaskBinding;
import com.kidsapp.ui.parent.task_plan.model.WeekTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTaskBottomSheet extends BottomSheetDialogFragment {

    private BottomsheetAddTaskBinding binding;
    private OnTaskAddedListener listener;
    private TaskAssignmentRepository repository;

    private int dayIndex;
    private String childId;
    private String selectedTaskType = "exercise";
    
    private List<String> availableSubjects;
    private List<String> availableHabitCategories;

    public interface OnTaskAddedListener {
        void onTaskAdded(WeekTask task);
    }

    public static AddTaskBottomSheet newInstance(int dayIndex) {
        AddTaskBottomSheet fragment = new AddTaskBottomSheet();
        Bundle args = new Bundle();
        args.putInt("dayIndex", dayIndex);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddTaskBottomSheet newInstance(int dayIndex, String childId) {
        AddTaskBottomSheet fragment = new AddTaskBottomSheet();
        Bundle args = new Bundle();
        args.putInt("dayIndex", dayIndex);
        args.putString("childId", childId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dayIndex = getArguments().getInt("dayIndex", 0);
            childId = getArguments().getString("childId");
        }
        repository = new TaskAssignmentRepository(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomsheetAddTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        setupListeners();
    }

    private void setupViews() {
        binding.chipGroupTaskType.check(R.id.chipExercise);
        binding.layoutExercise.setVisibility(View.VISIBLE);
        binding.layoutHabit.setVisibility(View.GONE);
        
        // Load subjects và habit categories từ API
        loadAvailableSubjects();
        loadAvailableHabitCategories();
    }

    private void setupListeners() {
        binding.chipGroupTaskType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipExercise) {
                selectedTaskType = "exercise";
                binding.layoutExercise.setVisibility(View.VISIBLE);
                binding.layoutHabit.setVisibility(View.GONE);
            } else if (checkedId == R.id.chipHabit) {
                selectedTaskType = "habit";
                binding.layoutExercise.setVisibility(View.GONE);
                binding.layoutHabit.setVisibility(View.VISIBLE);
            }
        });

        binding.btnCancel.setOnClickListener(v -> dismiss());
        binding.btnSave.setOnClickListener(v -> assignTask());
    }

    private void assignTask() {
        if (childId == null || childId.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chọn bé trước", Toast.LENGTH_SHORT).show();
            return;
        }

        String dueDate = calculateDueDate(dayIndex);
        String dueTime = binding.edtTime.getText().toString().trim();
        String pointsStr = binding.edtPoints.getText().toString().trim();
        Integer points = pointsStr.isEmpty() ? null : Integer.parseInt(pointsStr);

        ApiService.AssignTaskRequest request;

        switch (selectedTaskType) {
            case "exercise":
                String subject = getSelectedSubject();
                int difficulty = (int) binding.sliderDifficulty.getValue();
                request = ApiService.AssignTaskRequest.createExercise(childId, subject, difficulty, dueDate);
                if (points != null) request.pointsReward = points;
                if (!dueTime.isEmpty()) request.dueTime = dueTime;
                break;

            case "habit":
                String category = getSelectedHabitCategory();
                boolean isRecurring = binding.switchRecurring.isChecked();
                request = ApiService.AssignTaskRequest.createHabit(childId, category, dueDate, isRecurring);
                if (points != null) request.pointsReward = points;
                if (!dueTime.isEmpty()) request.dueTime = dueTime;
                break;

            default:
                return;
        }

        setLoading(true);

        repository.assignTask(request, new TaskAssignmentRepository.OnAssignTaskCallback() {
            @Override
            public void onSuccess(ApiService.TaskAssignmentResponse response) {
                if (!isAdded()) return;
                setLoading(false);

                WeekTask task = convertToWeekTask(response);

                if (listener != null) {
                    listener.onTaskAdded(task);
                }

                Toast.makeText(requireContext(), "Đã giao bài tập thành công!", Toast.LENGTH_SHORT).show();
                dismiss();
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                setLoading(false);
                Toast.makeText(requireContext(), "Lỗi: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAvailableSubjects() {
        repository.getAvailableSubjects(new TaskAssignmentRepository.OnSubjectsCallback() {
            @Override
            public void onSuccess(List<String> subjects) {
                if (!isAdded()) return;
                availableSubjects = subjects;
                populateSubjectChips(subjects);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Không thể load môn học: " + message, Toast.LENGTH_SHORT).show();
                // Fallback to default subjects
                availableSubjects = java.util.Arrays.asList("math", "vietnamese", "english");
                populateSubjectChips(availableSubjects);
            }
        });
    }

    private void loadAvailableHabitCategories() {
        repository.getAvailableHabitCategories(new TaskAssignmentRepository.OnHabitCategoriesCallback() {
            @Override
            public void onSuccess(List<String> categories) {
                if (!isAdded()) return;
                availableHabitCategories = categories;
                populateHabitCategoryChips(categories);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Không thể load loại thói quen: " + message, Toast.LENGTH_SHORT).show();
                // Fallback to default categories
                availableHabitCategories = java.util.Arrays.asList("health", "study", "housework", "sport", "creativity");
                populateHabitCategoryChips(availableHabitCategories);
            }
        });
    }

    private void populateSubjectChips(List<String> subjects) {
        binding.chipGroupSubject.removeAllViews();
        
        for (int i = 0; i < subjects.size(); i++) {
            String subject = subjects.get(i);
            Chip chip = new Chip(requireContext());
            chip.setId(View.generateViewId());
            chip.setText(getSubjectDisplayName(subject));
            chip.setCheckable(true);
            chip.setChipIcon(getResources().getDrawable(R.drawable.ic_quiz, null));
            
            // Set first chip as checked
            if (i == 0) {
                chip.setChecked(true);
            }
            
            binding.chipGroupSubject.addView(chip);
        }
    }

    private void populateHabitCategoryChips(List<String> categories) {
        binding.chipGroupHabitCategory.removeAllViews();
        
        for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);
            Chip chip = new Chip(requireContext());
            chip.setId(View.generateViewId());
            chip.setText(getCategoryDisplayName(category));
            chip.setCheckable(true);
            chip.setChipIcon(getResources().getDrawable(R.drawable.ic_habit, null));
            
            // Set first chip as checked
            if (i == 0) {
                chip.setChecked(true);
            }
            
            binding.chipGroupHabitCategory.addView(chip);
        }
    }

    private String getSubjectDisplayName(String subject) {
        switch (subject.toLowerCase()) {
            case "math": return "Toán";
            case "vietnamese": return "Tiếng Việt";
            case "english": return "Tiếng Anh";
            case "science": return "Khoa học";
            case "history": return "Lịch sử";
            case "geography": return "Địa lý";
            default: return subject;
        }
    }

    private String getCategoryDisplayName(String category) {
        switch (category.toLowerCase()) {
            case "health": return "Sức khỏe";
            case "study": return "Học tập";
            case "housework": return "Việc nhà";
            case "sport": return "Thể thao";
            case "creativity": return "Sáng tạo";
            case "responsibility": return "Trách nhiệm";
            case "self_discipline": return "Kỷ luật";
            default: return category;
        }
    }

    private String getSelectedSubject() {
        int checkedId = binding.chipGroupSubject.getCheckedChipId();
        if (checkedId == View.NO_ID || availableSubjects == null || availableSubjects.isEmpty()) {
            return "math"; // fallback
        }
        
        // Find the selected chip and get its position
        for (int i = 0; i < binding.chipGroupSubject.getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroupSubject.getChildAt(i);
            if (chip.getId() == checkedId) {
                return availableSubjects.get(i);
            }
        }
        
        return availableSubjects.get(0); // fallback to first
    }

    private String getSelectedHabitCategory() {
        int checkedId = binding.chipGroupHabitCategory.getCheckedChipId();
        if (checkedId == View.NO_ID || availableHabitCategories == null || availableHabitCategories.isEmpty()) {
            return "health"; // fallback
        }
        
        // Find the selected chip and get its position
        for (int i = 0; i < binding.chipGroupHabitCategory.getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroupHabitCategory.getChildAt(i);
            if (chip.getId() == checkedId) {
                return availableHabitCategories.get(i);
            }
        }
        
        return availableHabitCategories.get(0); // fallback to first
    }

    private String calculateDueDate(int dayIndex) {
        Calendar calendar = Calendar.getInstance();
        int todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // Calendar: Sunday=1, Monday=2, ..., Saturday=7
        // dayIndex: 0=Monday, 1=Tuesday, ..., 6=Sunday
        int targetDayOfWeek = (dayIndex + 2) % 7;
        if (targetDayOfWeek == 0) targetDayOfWeek = 7;
        
        int daysToAdd = targetDayOfWeek - todayDayOfWeek;
        if (daysToAdd < 0) daysToAdd += 7;
        
        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private WeekTask convertToWeekTask(ApiService.TaskAssignmentResponse response) {
        String type = "habit";
        if ("exercise".equals(response.taskType)) {
            type = "quiz";
        }

        WeekTask task = new WeekTask(
                response.id,
                response.title,
                response.description != null ? response.description : "",
                type,
                response.pointsReward != null ? response.pointsReward : 10,
                response.pointsReward != null ? response.pointsReward / 2 : 5,
                dayIndex
        );

        if (response.priority != null) {
            task.setLevel(response.priority);
        }

        return task;
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnSave.setEnabled(!loading);
        binding.btnCancel.setEnabled(!loading);
    }

    public void setOnTaskAddedListener(OnTaskAddedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
