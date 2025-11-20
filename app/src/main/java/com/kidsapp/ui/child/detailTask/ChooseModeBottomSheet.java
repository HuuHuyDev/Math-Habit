package com.kidsapp.ui.child.detailTask;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.kidsapp.R;

/**
 * Bottom sheet cho phép chọn chế độ làm bài (Ôn tập / Bắt đầu).
 */
public class ChooseModeBottomSheet extends BottomSheetDialogFragment {

    public interface ModeListener {
        void onModeSelected(@NonNull DetailTaskFragment.TaskMode mode);
    }

    private ModeListener modeListener;

    public void setModeListener(ModeListener modeListener) {
        this.modeListener = modeListener;
    }

    @Override
    public int getTheme() {
        return R.style.ThemeOverlay_Kids_BottomSheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_choose_mode, container, false);

        MaterialButton btnPractice = view.findViewById(R.id.btnPractice);
        MaterialButton btnStart = view.findViewById(R.id.btnStart);
        TextView btnCancel = view.findViewById(R.id.btnCancel);

        btnPractice.setOnClickListener(v -> {
            if (modeListener != null) {
                modeListener.onModeSelected(DetailTaskFragment.TaskMode.PRACTICE);
            }
            dismiss();
        });

        btnStart.setOnClickListener(v -> {
            if (modeListener != null) {
                modeListener.onModeSelected(DetailTaskFragment.TaskMode.START);
            }
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(d -> {
            BottomSheetDialog sheetDialog = (BottomSheetDialog) d;
            View bottomSheet = sheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                bottomSheet.setBackground(new ColorDrawable(Color.TRANSPARENT));
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }
}

