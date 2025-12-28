package com.kidsapp.ui.parent.task_plan.bottomsheet;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.model.ExerciseContent;
import com.kidsapp.data.repository.TaskAssignmentRepository;
import com.kidsapp.data.request.CreateTaskRequest;
import com.kidsapp.data.response.TaskResponse;
import com.kidsapp.databinding.BottomsheetAddTaskBinding;
import com.kidsapp.ui.parent.task_plan.model.WeekTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    
    private List<ExerciseContent> availableExercises = new ArrayList<>();
    private List<ApiService.HabitTemplateResponse> availableHabits = new ArrayList<>();
    private int selectedExerciseIndex = 0;
    private int selectedHabitIndex = 0;

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
        
        // Load exercises và habit templates từ API
        loadExercises();
        loadHabitTemplates();
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

        // Time pickers
        binding.edtDueTime.setOnClickListener(v -> showTimePicker(binding.edtDueTime));
        binding.edtReminderTime.setOnClickListener(v -> showTimePicker(binding.edtReminderTime));

        binding.btnCancel.setOnClickListener(v -> dismiss());
        binding.btnSave.setOnClickListener(v -> assignTask());
    }
    
    private void showTimePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        
        // Parse current time if available
        String currentTime = editText.getText().toString();
        if (!currentTime.isEmpty()) {
            try {
                String[] parts = currentTime.split(":");
                hour = Integer.parseInt(parts[0]);
                minute = Integer.parseInt(parts[1]);
            } catch (Exception ignored) {}
        }
        
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    editText.setText(time);
                },
                hour,
                minute,
                true
        );
        timePickerDialog.show();
    }

    private void assignTask() {
        if (childId == null || childId.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chọn bé trước", Toast.LENGTH_SHORT).show();
            return;
        }

        String dueDate = calculateDueDate(dayIndex);
        String dueTime = binding.edtDueTime.getText().toString().trim();
        String reminderTime = binding.edtReminderTime.getText().toString().trim();
        String parentNote = binding.edtParentNote.getText().toString().trim();
        int priority = (int) binding.sliderPriority.getValue();
        boolean isMandatory = binding.switchMandatory.isChecked();

        CreateTaskRequest request;

        switch (selectedTaskType) {
            case "exercise":
                ExerciseContent selectedExercise = getSelectedExercise();
                if (selectedExercise == null) {
                    Toast.makeText(requireContext(), "Vui lòng chọn bài tập", Toast.LENGTH_SHORT).show();
                    return;
                }
                request = CreateTaskRequest.forExercise(childId, selectedExercise.getId());
                break;

            case "habit":
                ApiService.HabitTemplateResponse selectedHabit = getSelectedHabit();
                if (selectedHabit == null) {
                    Toast.makeText(requireContext(), "Vui lòng chọn thói quen", Toast.LENGTH_SHORT).show();
                    return;
                }
                request = CreateTaskRequest.forHabit(childId, selectedHabit.id);
                request.setIsRecurring(binding.switchRecurring.isChecked());
                break;

            default:
                return;
        }

        // Set common fields
        request.setDueDate(dueDate);
        if (!dueTime.isEmpty()) {
            request.setDueTime(dueTime);
        }
        if (!reminderTime.isEmpty()) {
            request.setReminderTime(reminderTime);
        }
        if (!parentNote.isEmpty()) {
            request.setParentNote(parentNote);
        }
        request.setPriority(priority);
        request.setIsMandatory(isMandatory);

        setLoading(true);

        repository.createTask(request, new TaskAssignmentRepository.OnCreateTaskCallback() {
            @Override
            public void onSuccess(TaskResponse response) {
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

    private void loadExercises() {
        if (childId == null || childId.isEmpty()) {
            return;
        }
        
        repository.getExercisesForChild(childId, new TaskAssignmentRepository.OnExercisesCallback() {
            @Override
            public void onSuccess(List<ExerciseContent> exercises) {
                if (!isAdded()) return;
                availableExercises = exercises;
                setupExerciseSpinner(exercises);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Không thể load bài tập: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadHabitTemplates() {
        if (childId == null || childId.isEmpty()) {
            repository.getHabitTemplates(null, new TaskAssignmentRepository.OnHabitsCallback() {
                @Override
                public void onSuccess(List<ApiService.HabitTemplateResponse> habits) {
                    if (!isAdded()) return;
                    availableHabits = habits;
                    setupHabitSpinner(habits);
                }

                @Override
                public void onError(String message) {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), "Không thể load thói quen: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            repository.getHabitTemplatesForChild(childId, null, new TaskAssignmentRepository.OnHabitsCallback() {
                @Override
                public void onSuccess(List<ApiService.HabitTemplateResponse> habits) {
                    if (!isAdded()) return;
                    availableHabits = habits;
                    setupHabitSpinner(habits);
                }

                @Override
                public void onError(String message) {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), "Không thể load thói quen: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupExerciseSpinner(List<ExerciseContent> exercises) {
        List<String> exerciseTitles = new ArrayList<>();
        for (ExerciseContent exercise : exercises) {
            exerciseTitles.add(exercise.getTitle());
        }
        
        if (exerciseTitles.isEmpty()) {
            exerciseTitles.add("Không có bài tập");
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                exerciseTitles
        );
        binding.spinnerExercise.setAdapter(adapter);
        
        // Set default selection
        if (!exerciseTitles.isEmpty()) {
            binding.spinnerExercise.setText(exerciseTitles.get(0), false);
            selectedExerciseIndex = 0;
        }
        
        // Listen for selection
        binding.spinnerExercise.setOnItemClickListener((parent, view, position, id) -> {
            selectedExerciseIndex = position;
        });
    }

    private void setupHabitSpinner(List<ApiService.HabitTemplateResponse> habits) {
        List<String> habitNames = new ArrayList<>();
        for (ApiService.HabitTemplateResponse habit : habits) {
            habitNames.add(habit.name);
        }
        
        if (habitNames.isEmpty()) {
            habitNames.add("Không có thói quen");
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                habitNames
        );
        binding.spinnerHabit.setAdapter(adapter);
        
        // Set default selection
        if (!habitNames.isEmpty()) {
            binding.spinnerHabit.setText(habitNames.get(0), false);
            selectedHabitIndex = 0;
        }
        
        // Listen for selection
        binding.spinnerHabit.setOnItemClickListener((parent, view, position, id) -> {
            selectedHabitIndex = position;
        });
    }

    private ExerciseContent getSelectedExercise() {
        if (availableExercises.isEmpty() || selectedExerciseIndex >= availableExercises.size()) {
            return null;
        }
        return availableExercises.get(selectedExerciseIndex);
    }

    private ApiService.HabitTemplateResponse getSelectedHabit() {
        if (availableHabits.isEmpty() || selectedHabitIndex >= availableHabits.size()) {
            return null;
        }
        return availableHabits.get(selectedHabitIndex);
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

    private WeekTask convertToWeekTask(TaskResponse response) {
        String type = "habit";
        if ("EXERCISE".equalsIgnoreCase(response.getTaskType())) {
            type = "quiz";
        }

        WeekTask task = new WeekTask(
                response.getId(),
                response.getTitle(),
                response.getDescription() != null ? response.getDescription() : "",
                type,
                response.getPointsReward() != null ? response.getPointsReward() : 10,
                response.getPointsReward() != null ? response.getPointsReward() / 2 : 5,
                dayIndex
        );

        if (response.getPriority() != null) {
            task.setLevel(response.getPriority());
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
