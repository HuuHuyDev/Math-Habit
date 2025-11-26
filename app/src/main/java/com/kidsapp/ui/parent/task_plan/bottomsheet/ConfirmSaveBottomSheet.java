package com.kidsapp.ui.parent.task_plan.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kidsapp.databinding.BottomsheetConfirmSaveBinding;

/**
 * BottomSheet xác nhận lưu kế hoạch tuần
 */
public class ConfirmSaveBottomSheet extends BottomSheetDialogFragment {

    private BottomsheetConfirmSaveBinding binding;
    private OnSaveConfirmedListener listener;

    public interface OnSaveConfirmedListener {
        void onSaveConfirmed();
    }

    public static ConfirmSaveBottomSheet newInstance() {
        return new ConfirmSaveBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomsheetConfirmSaveBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
    }

    private void setupListeners() {
        binding.btnCancel.setOnClickListener(v -> dismiss());

        binding.btnSave.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSaveConfirmed();
            }
            dismiss();
        });
    }

    public void setOnSaveConfirmedListener(OnSaveConfirmedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
