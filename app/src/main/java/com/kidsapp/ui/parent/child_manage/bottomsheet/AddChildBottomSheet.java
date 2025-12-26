package com.kidsapp.ui.parent.child_manage.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kidsapp.databinding.BottomsheetAddChildBinding;
import com.kidsapp.ui.parent.child_manage.model.ChildModel;

/**
 * BottomSheet thêm bé mới
 */
public class AddChildBottomSheet extends BottomSheetDialogFragment {
    private BottomsheetAddChildBinding binding;
    private OnChildAddedListener listener;

    public interface OnChildAddedListener {
        void onChildAdded(ChildModel child);
    }

    public static AddChildBottomSheet newInstance() {
        return new AddChildBottomSheet();
    }

    public void setOnChildAddedListener(OnChildAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomsheetAddChildBinding.inflate(inflater, container, false);
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
            if (validateInput()) {
                saveChild();
            }
        });
    }

    private boolean validateInput() {
        String name = binding.edtChildName.getText().toString().trim();
        String className = binding.edtChildClass.getText().toString().trim();
        String username = binding.edtUsername.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();
        String confirmPassword = binding.edtConfirmPassword.getText().toString().trim();

        if (name.isEmpty()) {
            binding.edtChildName.setError("Vui lòng nhập tên bé");
            return false;
        }

        if (className.isEmpty()) {
            binding.edtChildClass.setError("Vui lòng nhập lớp");
            return false;
        }

        if (username.isEmpty()) {
            binding.edtUsername.setError("Vui lòng nhập tên đăng nhập");
            return false;
        }

        if (password.isEmpty()) {
            binding.edtPassword.setError("Vui lòng nhập mật khẩu");
            return false;
        }

        if (password.length() < 6) {
            binding.edtPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            binding.edtConfirmPassword.setError("Mật khẩu không khớp");
            return false;
        }

        return true;
    }

    private void saveChild() {
        String name = binding.edtChildName.getText().toString().trim();
        String className = binding.edtChildClass.getText().toString().trim();
        // Lấy giới tính: Nam = true, Nữ = false
        String genderStr = binding.spinnerGender.getSelectedItem().toString();
        Boolean gender = "Nam".equals(genderStr);
        String username = binding.edtUsername.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();

        // Tạo child mới với dữ liệu mặc định
        ChildModel child = new ChildModel(
                null, // ID sẽ được server tạo
                name,
                className,
                1, // Level mặc định
                0, // XP hiện tại
                100, // Max XP
                0, // Coins
                getRandomAvatar() // Avatar ngẫu nhiên
        );
        child.setGender(gender);
        child.setUsername(username);
        child.setPassword(password);
        child.setNickname(name); // Nickname mặc định = tên

        if (listener != null) {
            listener.onChildAdded(child);
        }

        dismiss();
    }

    private String getRandomAvatar() {
        String[] avatars = {"😊", "😄", "😁", "🥰", "😎", "🤗", "🤩", "😇"};
        return avatars[(int) (Math.random() * avatars.length)];
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
