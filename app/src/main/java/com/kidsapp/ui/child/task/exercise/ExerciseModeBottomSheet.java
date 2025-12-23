package com.kidsapp.ui.child.task.exercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kidsapp.R;
import com.kidsapp.databinding.BottomsheetExerciseModeBinding;

/**
 * BottomSheet để chọn chế độ: Luyện tập hoặc Kiểm tra
 */
public class ExerciseModeBottomSheet extends BottomSheetDialogFragment {

    private BottomsheetExerciseModeBinding binding;
    private OnModeSelectedListener listener;
    private String contentTitle;
    private int totalQuestions;
    private int completedQuestions;

    public enum Mode {
        PRACTICE,
        TEST
    }

    public interface OnModeSelectedListener {
        void onModeSelected(Mode mode);
    }

    public static ExerciseModeBottomSheet newInstance(String title, int total, int completed) {
        ExerciseModeBottomSheet fragment = new ExerciseModeBottomSheet();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("total", total);
        args.putInt("completed", completed);
        fragment.setArguments(args);
        return fragment;
    }

    public void setModeListener(OnModeSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomsheetExerciseModeBinding.inflate(inflater, container, false);
        
        loadArguments();
        setupViews();
        setupButtons();
        
        return binding.getRoot();
    }

    private void loadArguments() {
        if (getArguments() != null) {
            contentTitle = getArguments().getString("title", "");
            totalQuestions = getArguments().getInt("total", 0);
            completedQuestions = getArguments().getInt("completed", 0);
        }
    }

    private void setupViews() {
        binding.txtTitle.setText(contentTitle);
        binding.txtDescription.setText(String.format("Bạn đã hoàn thành %d/%d câu hỏi", 
            completedQuestions, totalQuestions));
    }

    private void setupButtons() {
        // Nút Luyện tập
        binding.btnPractice.setOnClickListener(v -> {
            if (listener != null) {
                listener.onModeSelected(Mode.PRACTICE);
            }
            dismiss();
        });

        // Nút Kiểm tra
        binding.btnTest.setOnClickListener(v -> {
            if (listener != null) {
                listener.onModeSelected(Mode.TEST);
            }
            dismiss();
        });

        // Nút Hủy
        binding.btnCancel.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
