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
import com.kidsapp.data.repository.TaskAssignmentRepository;
import com.kidsapp.data.request.UpdateTaskRequest;
import com.kidsapp.data.response.TaskResponse;
import com.kidsapp.databinding.BottomsheetEditTaskBinding;
import com.kidsapp.ui.parent.task_plan.model.WeekTask;

import java.util.Calendar;

/**
 * BottomSheet để chỉnh sửa nhiệm vụ đã giao
 * Cho phép sửa: dueDate, dueTime, reminderTime, parentNote, priority, isMandatory, isRecurring
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
        binding.edtDueTime.setText("");
        binding.edtReminderTime.setText("");
        binding.edtParentNote.setText("");
        binding.sliderPriority.setValue(task.getLevel() > 0 ? task.getLevel() : 1);
        binding.switchMandatory.setChecked(false);
        binding.switchRecurring.setChecked(false);
    }

    private String getCurrentDueDate() {
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
        binding.edtDueDate.setOnClickListener(v -> showDatePicker());
        binding.btnCancel.setOnClickListener(v -> dismiss());
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
        if (taskId == null || taskId.isEmpty()) {
            Toast.makeText(requireContext(), "Không tìm thấy ID nhiệm vụ", Toast.LENGTH_SHORT).show();
            return;
        }

        String dueDate = binding.edtDueDate.getText().toString().trim();
        String dueTime = binding.edtDueTime.getText().toString().trim();
        String reminderTime = binding.edtReminderTime.getText().toString().trim();
        String parentNote = binding.edtParentNote.getText().toString().trim();
        int priority = (int) binding.sliderPriority.getValue();
        boolean isMandatory = binding.switchMandatory.isChecked();
        boolean isRecurring = binding.switchRecurring.isChecked();

        if (dueDate.isEmpty()) {
            binding.tilDueDate.setError("Vui lòng chọn ngày hết hạn");
            return;
        }

        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setDueDate(dueDate);
        if (!dueTime.isEmpty()) request.setDueTime(dueTime);
        if (!reminderTime.isEmpty()) request.setReminderTime(reminderTime);
        if (!parentNote.isEmpty()) request.setParentNote(parentNote);
        request.setPriority(priority);
        request.setIsMandatory(isMandatory);
        request.setIsRecurring(isRecurring);

        setLoading(true);

        repository.updateTask(taskId, request, new TaskAssignmentRepository.OnCreateTaskCallback() {
            @Override
            public void onSuccess(TaskResponse response) {
                if (!isAdded()) return;
                setLoading(false);

                // Update local task
                task.setLevel(response.getPriority() != null ? response.getPriority() : 1);

                if (listener != null) {
                    listener.onTaskUpdated(task, position);
                }

                Toast.makeText(requireContext(), "Đã cập nhật nhiệm vụ!", Toast.LENGTH_SHORT).show();
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
