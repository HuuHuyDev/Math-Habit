package com.kidsapp.ui.parent.task_plan.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kidsapp.R;
import com.kidsapp.databinding.BottomsheetAddTaskBinding;
import com.kidsapp.ui.parent.task_plan.model.WeekTask;

import java.util.UUID;

/**
 * BottomSheet để thêm nhiệm vụ mới
 */
public class AddTaskBottomSheet extends BottomSheetDialogFragment {

    private BottomsheetAddTaskBinding binding;
    private OnTaskAddedListener listener;
    private int dayIndex;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dayIndex = getArguments().getInt("dayIndex", 0);
        }
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
        // Mặc định chọn Habit
        binding.chipGroupTaskType.check(R.id.chipHabit);
        binding.layoutLevel.setVisibility(View.GONE);
    }

    private void setupListeners() {
        // Chuyển đổi giữa Habit và Quiz
        binding.chipGroupTaskType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipQuiz) {
                binding.layoutLevel.setVisibility(View.VISIBLE);
            } else {
                binding.layoutLevel.setVisibility(View.GONE);
            }
        });

        // Nút Hủy
        binding.btnCancel.setOnClickListener(v -> dismiss());

        // Nút Lưu
        binding.btnSave.setOnClickListener(v -> saveTask());
    }

    private void saveTask() {
        String title = binding.edtTaskName.getText().toString().trim();
        String description = binding.edtTaskDescription.getText().toString().trim();
        String coinsStr = binding.edtCoins.getText().toString().trim();
        String xpStr = binding.edtXp.getText().toString().trim();

        // Validation
        if (title.isEmpty()) {
            binding.tilTaskName.setError("Vui lòng nhập tên nhiệm vụ");
            return;
        }

        int coins = coinsStr.isEmpty() ? 10 : Integer.parseInt(coinsStr);
        int xp = xpStr.isEmpty() ? 5 : Integer.parseInt(xpStr);

        String type = binding.chipGroupTaskType.getCheckedChipId() == R.id.chipHabit
                ? "habit" : "quiz";

        WeekTask task = new WeekTask(
                UUID.randomUUID().toString(),
                title,
                description,
                type,
                coins,
                xp,
                dayIndex
        );

        // Set level nếu là quiz
        if (type.equals("quiz")) {
            task.setLevel((int) binding.sliderLevel.getValue());
        }

        if (listener != null) {
            listener.onTaskAdded(task);
        }

        dismiss();
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
