package com.kidsapp.ui.parent.child_manage.bottomsheet;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kidsapp.databinding.BottomsheetAddChildBinding;
import com.kidsapp.ui.parent.child_manage.model.ChildModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * BottomSheet thêm bé mới
 */
public class AddChildBottomSheet extends BottomSheetDialogFragment {
    private BottomsheetAddChildBinding binding;
    private OnChildAddedListener listener;
    private Calendar selectedDate = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public interface OnChildAddedListener {
        void onChildAdded(ChildModel child);
    }

    public static AddChildBottomSheet newInstance() {
        return new AddChildBottomSheet();
    }

    public void setOnChildAddedListener(OnChildAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
                // Set max height to 90% of screen
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                bottomSheet.setMinimumHeight((int) (screenHeight * 0.9));
            }
        });
        return dialog;
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
        
        // Date picker cho ngày sinh
        binding.edtBirthDate.setOnClickListener(v -> showDatePicker());
    }
    
    private void showDatePicker() {
        // Mặc định hiển thị năm 2015 (khoảng 10 tuổi)
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);
        
        // Nếu chưa chọn, mặc định năm 2015
        if (binding.edtBirthDate.getText().toString().isEmpty()) {
            year = 2015;
            month = 0;
            day = 1;
        }
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
                    binding.edtBirthDate.setText(dateFormat.format(selectedDate.getTime()));
                },
                year, month, day
        );
        
        // Giới hạn: từ 2005 đến 2020 (5-20 tuổi)
        Calendar minDate = Calendar.getInstance();
        minDate.set(2005, 0, 1);
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(2020, 11, 31);
        
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        
        datePickerDialog.show();
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
        String school = binding.edtSchool.getText().toString().trim();
        // Lấy giới tính: Nam = true, Nữ = false
        String genderStr = binding.spinnerGender.getSelectedItem().toString();
        Boolean gender = "Nam".equals(genderStr);
        String username = binding.edtUsername.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();
        
        // Lấy ngày sinh dạng API (yyyy-MM-dd)
        String birthDate = null;
        if (!binding.edtBirthDate.getText().toString().isEmpty()) {
            birthDate = apiDateFormat.format(selectedDate.getTime());
        }

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
        child.setSchool(school.isEmpty() ? null : school);
        child.setBirthDate(birthDate);

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
