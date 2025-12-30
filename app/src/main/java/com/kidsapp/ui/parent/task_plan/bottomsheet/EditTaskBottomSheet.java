package com.kidsapp.ui.parent.task_plan.bottomsheet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.Task;
import com.kidsapp.data.repository.TaskAssignmentRepository;
import com.kidsapp.data.request.UpdateTaskRequest;
import com.kidsapp.data.response.TaskResponse;
import com.kidsapp.databinding.BottomsheetEditTaskBinding;
import com.kidsapp.ui.parent.task_plan.model.WeekTask;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * BottomSheet để chỉnh sửa nhiệm vụ đã giao
 * Cho phép sửa: dueDate, dueTime, reminderTime, parentNote, priority, isMandatory, isRecurring
 */
public class EditTaskBottomSheet extends BottomSheetDialogFragment {

    private BottomsheetEditTaskBinding binding;
    private OnTaskUpdatedListener listener;
    private TaskAssignmentRepository repository;
    private ApiService apiService;
    private WeekTask task;
    private int position;
    private String taskId;
    
    // Lưu dữ liệu task từ API
    private Task taskDetail;

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
        SharedPref sharedPref = new SharedPref(requireContext());
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();
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
        setupListeners();
        
        // Load task detail từ API
        if (task != null && task.getId() != null) {
            taskId = task.getId();
            loadTaskDetail();
        } else {
            loadTaskData();
        }
    }
    
    /**
     * Load chi tiết task từ API để fill đúng dữ liệu
     */
    private void loadTaskDetail() {
        setLoading(true);
        
        apiService.getTaskDetail(taskId).enqueue(new Callback<ApiService.ApiResponseWrapper<Task>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<Task>> call,
                                   Response<ApiService.ApiResponseWrapper<Task>> response) {
                if (!isAdded()) return;
                setLoading(false);
                
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    taskDetail = response.body().data;
                    fillTaskData(taskDetail);
                } else {
                    // Fallback to local data
                    loadTaskData();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<Task>> call, Throwable t) {
                if (!isAdded()) return;
                setLoading(false);
                // Fallback to local data
                loadTaskData();
            }
        });
    }
    
    /**
     * Fill dữ liệu từ API response
     */
    private void fillTaskData(Task taskData) {
        // Task Type (READ ONLY)
        if ("HABIT".equalsIgnoreCase(taskData.getTaskType())) {
            binding.chipTaskType.setText("Thói quen");
            binding.chipTaskType.setChipIconResource(R.drawable.ic_habit);
        } else {
            binding.chipTaskType.setText("Bài tập");
            binding.chipTaskType.setChipIconResource(R.drawable.ic_quiz);
        }

        // Task Name & Description (READ ONLY)
        binding.tvTaskName.setText(taskData.getTitle() != null ? taskData.getTitle() : "");
        binding.tvTaskDescription.setText(taskData.getDescription() != null ? taskData.getDescription() : "");

        // Editable fields - fill với dữ liệu hiện tại
        binding.edtDueDate.setText(taskData.getDueDate() != null ? taskData.getDueDate() : getCurrentDueDate());
        binding.edtDueTime.setText(taskData.getDueTime() != null ? taskData.getDueTime() : "");
        binding.edtReminderTime.setText(taskData.getReminderTime() != null ? taskData.getReminderTime() : "");
        binding.edtParentNote.setText(taskData.getParentNote() != null ? taskData.getParentNote() : "");
        binding.sliderPriority.setValue(taskData.getPriority() > 0 ? taskData.getPriority() : 1);
        binding.switchRecurring.setChecked(taskData.isRecurring());
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
        binding.edtDueTime.setOnClickListener(v -> showTimePicker(binding.edtDueTime));
        binding.edtReminderTime.setOnClickListener(v -> showTimePicker(binding.edtReminderTime));
        binding.btnCancel.setOnClickListener(v -> dismiss());
        binding.btnUpdate.setOnClickListener(v -> updateTask());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        
        // Parse current date if available
        String currentDate = binding.edtDueDate.getText().toString();
        if (!currentDate.isEmpty()) {
            try {
                String[] parts = currentDate.split("-");
                calendar.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
            } catch (Exception ignored) {}
        }
        
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
    
    private void showTimePicker(com.google.android.material.textfield.TextInputEditText editText) {
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
                    String time = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    editText.setText(time);
                },
                hour,
                minute,
                true
        );
        timePickerDialog.show();
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
        boolean isRecurring = binding.switchRecurring.isChecked();

        if (dueDate.isEmpty()) {
            binding.tilDueDate.setError("Vui lòng chọn ngày hết hạn");
            return;
        }

        // Validate: giờ nhắc nhở phải trước giờ hoàn thành
        if (!dueTime.isEmpty() && !reminderTime.isEmpty()) {
            if (!isReminderBeforeDueTime(reminderTime, dueTime)) {
                Toast.makeText(requireContext(), "Giờ nhắc nhở phải trước giờ hoàn thành", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setDueDate(dueDate);
        if (!dueTime.isEmpty()) request.setDueTime(dueTime);
        if (!reminderTime.isEmpty()) request.setReminderTime(reminderTime);
        if (!parentNote.isEmpty()) request.setParentNote(parentNote);
        request.setPriority(priority);
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
    
    /**
     * Kiểm tra giờ nhắc nhở có trước giờ hoàn thành không
     */
    private boolean isReminderBeforeDueTime(String reminderTime, String dueTime) {
        try {
            String[] reminderParts = reminderTime.split(":");
            String[] dueParts = dueTime.split(":");
            
            int reminderMinutes = Integer.parseInt(reminderParts[0]) * 60 + Integer.parseInt(reminderParts[1]);
            int dueMinutes = Integer.parseInt(dueParts[0]) * 60 + Integer.parseInt(dueParts[1]);
            
            return reminderMinutes < dueMinutes;
        } catch (Exception e) {
            return true; // Nếu parse lỗi thì cho qua
        }
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
