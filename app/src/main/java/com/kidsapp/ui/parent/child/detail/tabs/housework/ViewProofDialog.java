package com.kidsapp.ui.parent.child.detail.tabs.housework;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.kidsapp.R;
import com.kidsapp.databinding.DialogViewProofBinding;

/**
 * Dialog để xem minh chứng (ảnh/video) và duyệt/từ chối HABIT task
 */
public class ViewProofDialog extends DialogFragment {

    private DialogViewProofBinding binding;
    private HouseworkTask task;
    private OnProofActionListener listener;

    public interface OnProofActionListener {
        void onApprove(HouseworkTask task);
        void onReject(HouseworkTask task, String reason);
    }

    public static ViewProofDialog newInstance(HouseworkTask task) {
        ViewProofDialog dialog = new ViewProofDialog();
        dialog.task = task;
        return dialog;
    }

    public void setOnProofActionListener(OnProofActionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_App_Dialog_FullWidth);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DialogViewProofBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (task == null) {
            dismiss();
            return;
        }

        setupUI();
        setupClickListeners();
        loadProof();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private void setupUI() {
        binding.tvTaskName.setText(task.getTitle());

        // Ẩn action buttons nếu task đã completed
        if (task.isCompleted()) {
            binding.layoutActions.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        binding.btnClose.setOnClickListener(v -> dismiss());

        binding.btnApprove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApprove(task);
            }
            dismiss();
        });

        binding.btnReject.setOnClickListener(v -> showRejectDialog());

        // Click vào ảnh/video để xem full screen
        binding.imgProof.setOnClickListener(v -> openFullScreen());
        binding.layoutVideoOverlay.setOnClickListener(v -> openFullScreen());
    }

    private void loadProof() {
        String proofUrl = task.getProofUrl();
        if (proofUrl == null || proofUrl.isEmpty()) {
            binding.imgProof.setImageResource(R.drawable.ic_image_placeholder);
            return;
        }

        binding.progressLoading.setVisibility(View.VISIBLE);

        // Check if video
        boolean isVideo = proofUrl.contains("video") || proofUrl.endsWith(".mp4") 
                || proofUrl.endsWith(".mov") || proofUrl.endsWith(".avi");

        if (isVideo) {
            binding.layoutVideoOverlay.setVisibility(View.VISIBLE);
            // Load video thumbnail
            Glide.with(this)
                    .load(proofUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.imgProof);
        } else {
            binding.layoutVideoOverlay.setVisibility(View.GONE);
            // Load image
            Glide.with(this)
                    .load(proofUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.imgProof);
        }

        binding.progressLoading.setVisibility(View.GONE);
    }

    private void openFullScreen() {
        String proofUrl = task.getProofUrl();
        if (proofUrl == null || proofUrl.isEmpty()) return;

        try {
            // Open in browser or default app
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(proofUrl));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Không thể mở minh chứng", Toast.LENGTH_SHORT).show();
        }
    }

    private void showRejectDialog() {
        EditText input = new EditText(requireContext());
        input.setHint("Nhập lý do từ chối (tùy chọn)");
        input.setPadding(48, 32, 48, 32);

        new AlertDialog.Builder(requireContext())
                .setTitle("Từ chối thói quen")
                .setMessage("Bạn có chắc muốn từ chối? Con sẽ cần làm lại.")
                .setView(input)
                .setPositiveButton("Từ chối", (dialog, which) -> {
                    String reason = input.getText().toString().trim();
                    if (listener != null) {
                        listener.onReject(task, reason);
                    }
                    dismiss();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
