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
 * Phụ huynh chọn từ template (ExerciseContent hoặc HabitTemplate)
 */
public class AssignTaskFragment extends Fragment {

    private FragmentAssignTaskBinding binding;
    private SharedPref sharedPref;
    private ApiService apiService;
    
    // Data lists
    private List<ApiService.ChildResponse> children = new ArrayList<>();
    private List<ExerciseContent> exercises = new ArrayList<>();
    private List<ApiService.HabitTemplateResponse> habitTemplates = new ArrayList<>();
    
    // Selected values
    private String selectedChildId;
    private String selectedExerciseId;
    private String selectedHabitTemplateId;
    private String selectedDate;
    private String selectedTime;
    private boolean isExerciseType = true;
    
    // Date formatters
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
        setupAssignButton();
        
        // Load data from API
        loadChildren();
    }

    private void setupHeader() {
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    /**
     * Setup chọn loại bài tập (EXERCISE hoặc HABIT)
     */
    private void setupTaskTypeSelection() {
        binding.radioGroupTaskType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioExercise) {
                isExerciseType = true;
                binding.layoutExerciseSelection.setVisibility(View.VISIBLE);
                binding.layoutHabitSelection.setVisibility(View.GONE);
                selectedHabitTemplateId = null;
                updateTemplateInfo();
            } else if (checkedId == R.id.radioHabit) {
                isExerciseType = false;
                binding.layoutExerciseSelection.setVisibility(View.GONE);
                binding.layoutHabitSelection.setVisibility(View.VISIBLE);
                selectedExerciseId = null;
                // Load habit templates if not loaded
                if (habitTemplates.isEmpty()) {
                    loadHabitTemplates();
                }
                updateTemplateInfo();
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
     * Load danh sách con từ API
     */
    private void loadChildren() {
        showLoading(true);
        
        apiService.getParentChildren().enqueue(new Callback<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> call,
                                 Response<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    children.clear();
                    children.addAll(response.body().data);
                    setupChildrenSpinner();
                    
                    // Load exercises for first child
                    if (!children.isEmpty()) {
                        selectedChildId = children.get(0).id;
                        loadExercises(selectedChildId);
                    }
                } else {
                    showLoading(false);
                    Toast.makeText(requireContext(), "Không thể tải danh sách con", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Setup spinner chọn con
     */
    private void setupChildrenSpinner() {
        List<String> childNames = new ArrayList<>();
        for (ApiService.ChildResponse child : children) {
            childNames.add(child.name);
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            childNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerChild.setAdapter(adapter);
        
        binding.spinnerChild.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String newChildId = children.get(position).id;
                if (!newChildId.equals(selectedChildId)) {
                    selectedChildId = newChildId;
                    // Reload exercises for new child
                    loadExercises(selectedChildId);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    /**
     * Load danh sách bài tập từ API
     */
    private void loadExercises(String childId) {
        showLoading(true);
        
        apiService.getExercisesForChild(childId, null).enqueue(new Callback<ApiService.ApiResponseWrapper<List<ExerciseContent>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ExerciseContent>>> call,
                                 Response<ApiService.ApiResponseWrapper<List<ExerciseContent>>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    exercises.clear();
                    exercises.addAll(response.body().data);
                    setupExerciseSpinner();
                } else {
                    Toast.makeText(requireContext(), "Không thể tải danh sách bài tập", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ExerciseContent>>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Setup spinner chọn bài tập
     */
    private void setupExerciseSpinner() {
        List<String> exerciseTitles = new ArrayList<>();
        for (ExerciseContent exercise : exercises) {
            exerciseTitles.add(exercise.getTitle());
        }
        
        if (exerciseTitles.isEmpty()) {
            exerciseTitles.add("Không có bài tập");
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            exerciseTitles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerExercise.setAdapter(adapter);
        
        binding.spinnerExercise.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (!exercises.isEmpty() && position < exercises.size()) {
                    selectedExerciseId = exercises.get(position).getId();
                    updateTemplateInfo();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
        
        // Select first exercise
        if (!exercises.isEmpty()) {
            selectedExerciseId = exercises.get(0).getId();
            updateTemplateInfo();
        }
    }

    /**
     * Load danh sách thói quen từ API
     */
    private void loadHabitTemplates() {
        showLoading(true);
        
        apiService.getHabitTemplates(null).enqueue(new Callback<ApiService.ApiResponseWrapper<List<ApiService.HabitTemplateResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.HabitTemplateResponse>>> call,
                                 Response<ApiService.ApiResponseWrapper<List<ApiService.HabitTemplateResponse>>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    habitTemplates.clear();
                    habitTemplates.addAll(response.body().data);
                    setupHabitSpinner();
                } else {
                    Toast.makeText(requireContext(), "Không thể tải danh sách thói quen", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.HabitTemplateResponse>>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Setup spinner chọn thói quen
     */
    private void setupHabitSpinner() {
        List<String> habitNames = new ArrayList<>();
        for (ApiService.HabitTemplateResponse habit : habitTemplates) {
            habitNames.add(habit.name);
        }
        
        if (habitNames.isEmpty()) {
            habitNames.add("Không có thói quen");
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            habitNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerHabit.setAdapter(adapter);
        
        binding.spinnerHabit.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (!habitTemplates.isEmpty() && position < habitTemplates.size()) {
                    selectedHabitTemplateId = habitTemplates.get(position).id;
                    updateTemplateInfo();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
        
        // Select first habit
        if (!habitTemplates.isEmpty()) {
            selectedHabitTemplateId = habitTemplates.get(0).id;
            updateTemplateInfo();
        }
    }

    /**
     * Cập nhật thông tin template đã chọn
     */
    private void updateTemplateInfo() {
        if (isExerciseType && selectedExerciseId != null && !exercises.isEmpty()) {
            // Find selected exercise
            ExerciseContent selected = null;
            for (ExerciseContent ex : exercises) {
                if (ex.getId().equals(selectedExerciseId)) {
                    selected = ex;
                    break;
                }
            }
            
            if (selected != null) {
                binding.cardSelectedTemplate.setVisibility(View.VISIBLE);
                binding.tvTemplateTitle.setText(selected.getTitle());
                binding.tvTemplateDescription.setText(selected.getDescription() != null ? 
                    selected.getDescription() : "Không có mô tả");
                binding.tvTemplateReward.setText("🎯 " + selected.getPointsReward() + " điểm");
            }
        } else if (!isExerciseType && selectedHabitTemplateId != null && !habitTemplates.isEmpty()) {
            // Find selected habit
            ApiService.HabitTemplateResponse selected = null;
            for (ApiService.HabitTemplateResponse habit : habitTemplates) {
                if (habit.id.equals(selectedHabitTemplateId)) {
                    selected = habit;
                    break;
                }
            }
            
            if (selected != null) {
                binding.cardSelectedTemplate.setVisibility(View.VISIBLE);
                binding.tvTemplateTitle.setText(selected.name);
                binding.tvTemplateDescription.setText(selected.description != null ? 
                    selected.description : "Không có mô tả");
                binding.tvTemplateReward.setText("🎯 " + selected.xpReward + " XP | 💰 " + selected.coinsReward + " xu");
            }
        } else {
            binding.cardSelectedTemplate.setVisibility(View.GONE);
        }
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
        
        if (isExerciseType) {
            if (selectedExerciseId == null || selectedExerciseId.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn bài tập", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            if (selectedHabitTemplateId == null || selectedHabitTemplateId.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn thói quen", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        
        return true;
    }

    /**
     * Giao bài cho con
     */
    private void assignTask() {
        binding.btnAssignTask.setEnabled(false);
        showLoading(true);
        
        // Tạo request từ factory method
        CreateTaskRequest request;
        if (isExerciseType) {
            request = CreateTaskRequest.forExercise(selectedChildId, selectedExerciseId);
        } else {
            request = CreateTaskRequest.forHabit(selectedChildId, selectedHabitTemplateId);
        }
        
        // Set optional fields
        request.setDueDate(selectedDate);
        request.setDueTime(selectedTime);
        request.setParentNote(binding.edtParentNote.getText().toString().trim());
        request.setIsMandatory(binding.cbMandatory.isChecked());
        
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
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), "Đã giao bài thành công!", Toast.LENGTH_SHORT).show();
                    
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
                showLoading(false);
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (binding != null) {
            binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
