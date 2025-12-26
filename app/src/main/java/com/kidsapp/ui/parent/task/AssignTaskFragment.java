package com.kidsapp.ui.parent.task;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.Child;
import com.kidsapp.data.model.ExerciseContent;
import com.kidsapp.data.request.CreateTaskRequest;
import com.kidsapp.data.response.TaskResponse;
import com.kidsapp.databinding.FragmentAssignTaskBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment để parent giao bài cho con
 */
public class AssignTaskFragment extends Fragment {

    private FragmentAssignTaskBinding binding;
    private SharedPref sharedPref;
    private ApiService apiService;
    
    private List<Child> children = new ArrayList<>();
    private List<ExerciseContent> exercises = new ArrayList<>();
    
    private String selectedChildId;
    private String selectedExerciseId;
    private String selectedDate;
    private String selectedTime;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat displayTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAssignTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        sharedPref = new SharedPref(requireContext());
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();
        
        setupHeader();
        setupTaskTypeSelection();
        setupDateTimePickers();
        setupChildrenSpinner();
        setupExerciseSpinner();
        setupAssignButton();
    }

    private void setupHeader() {
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    /**
     * Setup chọn loại bài tập
     */
    private void setupTaskTypeSelection() {
        binding.radioGroupTaskType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioExercise) {
                // Hiện spinner chọn bài tập
                binding.layoutExerciseSelection.setVisibility(View.VISIBLE);
                binding.edtTitle.setEnabled(false);
                binding.edtTitle.setText(""); // Clear title, sẽ auto-fill từ exercise
            } else {
                // Ẩn spinner, cho phép nhập title tự do
                binding.layoutExerciseSelection.setVisibility(View.GONE);
                binding.edtTitle.setEnabled(true);
                selectedExerciseId = null;
            }
        });
    }

    /**
     * Setup date/time pickers
     */
    private void setupDateTimePickers() {
        // Date picker
        binding.edtDueDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    selectedDate = dateFormat.format(calendar.getTime());
                    binding.edtDueDate.setText(displayDateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        // Time picker
        binding.edtDueTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    selectedTime = timeFormat.format(calendar.getTime());
                    binding.edtDueTime.setText(displayTimeFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            );
            timePickerDialog.show();
        });
    }

    /**
     * Setup spinner chọn con
     * TODO: Load từ API
     */
    private void setupChildrenSpinner() {
        // Mock data - TODO: Load từ API
        children.clear();
        Child child1 = new Child();
        child1.setId("child-1");
        child1.setName("Hồ Hữu Huy");
        children.add(child1);
        
        Child child2 = new Child();
        child2.setId("child-2");
        child2.setName("Linh");
        children.add(child2);
        
        List<String> childNames = new ArrayList<>();
        for (Child child : children) {
            childNames.add(child.getName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            childNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerChild.setAdapter(adapter);
        
        // Set selected child ID
        binding.spinnerChild.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedChildId = children.get(position).getId();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
        
        if (!children.isEmpty()) {
            selectedChildId = children.get(0).getId();
        }
    }

    /**
     * Setup spinner chọn bài tập
     * TODO: Load từ API
     */
    private void setupExerciseSpinner() {
        // Mock data - TODO: Load từ API
        exercises.clear();
        
        List<String> exerciseTitles = new ArrayList<>();
        exerciseTitles.add("Bài 1: Luyện phép cộng");
        exerciseTitles.add("Bài 2: Luyện phép trừ");
        exerciseTitles.add("Bài 3: Bài toán minh họa");
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            exerciseTitles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerExercise.setAdapter(adapter);
        
        // Auto-fill title khi chọn exercise
        binding.spinnerExercise.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String title = exerciseTitles.get(position);
                binding.edtTitle.setText(title);
                // TODO: Set selectedExerciseId từ exercises list
                selectedExerciseId = "exercise-" + (position + 1);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    /**
     * Setup nút giao bài
     */
    private void setupAssignButton() {
        binding.btnAssignTask.setOnClickListener(v -> {
            if (validateInput()) {
                assignTask();
            }
        });
    }

    /**
     * Validate input
     */
    private boolean validateInput() {
        if (selectedChildId == null || selectedChildId.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chọn con", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        String title = binding.edtTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
            binding.edtTitle.requestFocus();
            return false;
        }
        
        return true;
    }

    /**
     * Giao bài cho con
     */
    private void assignTask() {
        // Disable button để tránh double click
        binding.btnAssignTask.setEnabled(false);
        
        // Tạo request
        CreateTaskRequest request = new CreateTaskRequest();
        request.setChildId(selectedChildId);
        request.setTitle(binding.edtTitle.getText().toString().trim());
        request.setDescription(binding.edtDescription.getText().toString().trim());
        
        // Task type
        int checkedId = binding.radioGroupTaskType.getCheckedRadioButtonId();
        if (checkedId == R.id.radioExercise) {
            request.setTaskType("exercise");
            request.setExerciseId(selectedExerciseId);
        } else if (checkedId == R.id.radioHousework) {
            request.setTaskType("housework");
        } else if (checkedId == R.id.radioHabit) {
            request.setTaskType("habit");
        }
        
        // Due date/time
        request.setDueDate(selectedDate);
        request.setDueTime(selectedTime);
        
        // Points reward
        String pointsStr = binding.edtPointsReward.getText().toString().trim();
        if (!pointsStr.isEmpty()) {
            request.setPointsReward(Integer.parseInt(pointsStr));
        } else {
            request.setPointsReward(10); // Default
        }
        
        // Priority
        int priorityId = binding.radioGroupPriority.getCheckedRadioButtonId();
        if (priorityId == R.id.radioPriorityLow) {
            request.setPriority(1);
        } else if (priorityId == R.id.radioPriorityMedium) {
            request.setPriority(2);
        } else if (priorityId == R.id.radioPriorityHigh) {
            request.setPriority(3);
        }
        
        // Call API
        apiService.createTask(request).enqueue(new Callback<ApiService.ApiResponseWrapper<TaskResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<TaskResponse>> call,
                                 Response<ApiService.ApiResponseWrapper<TaskResponse>> response) {
                binding.btnAssignTask.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), "Đã giao bài thành công!", Toast.LENGTH_SHORT).show();
                    
                    // Quay lại màn hình trước
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                } else {
                    Toast.makeText(requireContext(), "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<TaskResponse>> call, Throwable t) {
                binding.btnAssignTask.setEnabled(true);
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
