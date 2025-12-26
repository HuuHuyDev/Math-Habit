package com.kidsapp.ui.parent.task_plan.bottomsheet;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.repository.TaskAssignmentRepository;
import com.kidsapp.databinding.BottomsheetEditTaskBinding;
import com.kidsapp.ui.parent.task_plan.model.WeekTask;

import java.util.Calendar;

/**
 * BottomSheet để chỉnh sửa nhiệm vụ đã giao
 * Chỉ cho phép sửa: dueDate, dueTime, pointsReward, isRecurring
 */
public class EditTaskBottomSheet extends BottomSheetDialogFragment {

    private BottomsheetEditTaskBinding binding;
    private OnTaskUpdatedListener listener;
    private TaskAssignmentRepository repository;
    private WeekTask task;
    private int position;
    private String taskId;

    public interface OnTaskUpdatedListener {
        void onTaskUpdated(WeekTask task, int position);
    }

    public static EditTaskBottomSheet newInstance(WeekTask task, int position) {
        EditTaskBottomSheet fragment = new EditTaskBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable("task", task);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            task = (WeekTask) getArguments().getSerializable("task");
            position = getArguments().getInt("position", 0);
        }
        repository = new TaskAssignmentRepository(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomsheetEditTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadTaskData();
        setupListeners();
    }

    private void loadTaskData() {
        if (task == null) return;

        taskId = task.getId();

        // Task Type (READ ONLY)
        if (task.isHabit()) {
            binding.chipTaskType.setText("Thói quen");
            binding.chipTaskType.setChipIconResource(R.drawable.ic_habit);
        } else {
            binding.chipTaskType.setText("Bài tập");
            binding.chipTaskType.setChipIconResource(R.drawable.ic_quiz);
        }

        // Task Name & Description (READ ONLY)
        binding.tvTaskName.setText(task.getTitle());
        binding.tvTaskDescription.setText(task.getDescription());

        // Editable fields
        binding.edtDueDate.setText(getCurrentDueDate());
        binding.edtDueTime.setText(""); // TODO: Get from task if available
        binding.edtPoints.setText(String.valueOf(task.getCoins()));
        binding.switchRecurring.setChecked(false); // TODO: Get from task if available
    }

    private String getCurrentDueDate() {
        // Calculate due date based on dayIndex
        Calendar calendar = Calendar.getInstance();
        int todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int targetDayOfWeek = (task.getDayIndex() + 2) % 7;
        if (targetDayOfWeek == 0) targetDayOfWeek = 7;
        
        int daysToAdd = targetDayOfWeek - todayDayOfWeek;
        if (daysToAdd < 0) daysToAdd += 7;
        
        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
        return String.format("%04d-%02d-%02d", 
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void setupListeners() {
        // Date picker
        binding.edtDueDate.setOnClickListener(v -> showDatePicker());

        // Nút Hủy
        binding.btnCancel.setOnClickListener(v -> dismiss());

        // Nút Cập nhật
        binding.btnUpdate.setOnClickListener(v -> updateTask());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    binding.edtDueDate.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateTask() {
        String dueDate = binding.edtDueDate.getText().toString().trim();
        String dueTime = binding.edtDueTime.getText().toString().trim();
        String pointsStr = binding.edtPoints.getText().toString().trim();
        boolean isRecurring = binding.switchRecurring.isChecked();

        // Validation
        if (dueDate.isEmpty()) {
            binding.tilDueDate.setError("Vui lòng chọn ngày hết hạn");
            return;
        }

        Integer points = pointsStr.isEmpty() ? null : Integer.parseInt(pointsStr);

        // Create update request
        ApiService.UpdateTaskRequest request = new ApiService.UpdateTaskRequest(
                dueDate,
                dueTime.isEmpty() ? null : dueTime,
                points,
                isRecurring,
                isRecurring ? "daily" : null
        );

        setLoading(true);

        repository.updateTask(taskId, request, new TaskAssignmentRepository.OnUpdateTaskCallback() {
            @Override
            public void onSuccess(ApiService.TaskAssignmentResponse response) {
                if (!isAdded()) return;
                setLoading(false);

                // Update local task object
                if (points != null) {
                    task.setCoins(points);
                    task.setXp(points / 2);
                }

                if (listener != null) {
                    listener.onTaskUpdated(task, position);
                }

                Toast.makeText(requireContext(), "Đã cập nhật nhiệm vụ thành công!", Toast.LENGTH_SHORT).show();
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

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnUpdate.setEnabled(!loading);
        binding.btnCancel.setEnabled(!loading);
    }

    public void setOnTaskUpdatedListener(OnTaskUpdatedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
