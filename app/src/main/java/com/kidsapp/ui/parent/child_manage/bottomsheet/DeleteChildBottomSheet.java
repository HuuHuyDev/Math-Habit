package com.kidsapp.ui.parent.child_manage.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kidsapp.databinding.BottomsheetDeleteChildBinding;

/**
 * BottomSheet xác nhận xóa bé
 */
public class DeleteChildBottomSheet extends BottomSheetDialogFragment {
    private BottomsheetDeleteChildBinding binding;
    private OnDeleteConfirmedListener listener;

    public interface OnDeleteConfirmedListener {
        void onDeleteConfirmed();
    }

    public static DeleteChildBottomSheet newInstance(String childName) {
        DeleteChildBottomSheet fragment = new DeleteChildBottomSheet();
        Bundle args = new Bundle();
        args.putString("childName", childName);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnDeleteConfirmedListener(OnDeleteConfirmedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomsheetDeleteChildBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupMessage();
        setupListeners();
    }

    private void setupMessage() {
        if (getArguments() != null) {
            String childName = getArguments().getString("childName", "");
            String message = "Bạn có chắc muốn xóa hồ sơ của " + childName + "?\n" +
                    "Hành động này không thể hoàn tác.";
            binding.txtMessage.setText(message);
        }
    }

    private void setupListeners() {
        binding.btnCancel.setOnClickListener(v -> dismiss());

        binding.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteConfirmed();
            }
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
