package com.kidsapp.ui.parent.task_plan.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kidsapp.R;
import com.kidsapp.databinding.BottomsheetEditTaskBinding;
import com.kidsapp.ui.parent.task_plan.model.WeekTask;

/**
 * BottomSheet để chỉnh sửa nhiệm vụ
 */
public class EditTaskBottomSheet extends BottomSheetDialogFragment {

    private BottomsheetEditTaskBinding binding;
    private OnTaskUpdatedListener listener;
    private WeekTask task;
    private int position;

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

        binding.edtTaskName.setText(task.getTitle());
        binding.edtTaskDescription.setText(task.getDescription());
        binding.edtCoins.setText(String.valueOf(task.getCoins()));
        binding.edtXp.setText(String.valueOf(task.getXp()));

        if (task.isHabit()) {
            binding.chipGroupTaskType.check(R.id.chipHabit);
            binding.layoutLevel.setVisibility(View.GONE);
        } else {
            binding.chipGroupTaskType.check(R.id.chipQuiz);
            binding.layoutLevel.setVisibility(View.VISIBLE);
            binding.sliderLevel.setValue(task.getLevel());
        }
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

        // Nút Cập nhật
        binding.btnUpdate.setOnClickListener(v -> updateTask());
    }

    private void updateTask() {
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

        task.setTitle(title);
        task.setDescription(description);
        task.setCoins(coins);
        task.setXp(xp);
        task.setType(type);

        // Set level nếu là quiz
        if (type.equals("quiz")) {
            task.setLevel((int) binding.sliderLevel.getValue());
        }

        if (listener != null) {
            listener.onTaskUpdated(task, position);
        }

        dismiss();
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
