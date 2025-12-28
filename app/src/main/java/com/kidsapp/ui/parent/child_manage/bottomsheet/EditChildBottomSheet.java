package com.kidsapp.ui.parent.child_manage.bottomsheet;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kidsapp.databinding.BottomsheetEditChildBinding;
import com.kidsapp.ui.parent.child_manage.model.ChildModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * BottomSheet chỉnh sửa thông tin bé
 */
public class EditChildBottomSheet extends BottomSheetDialogFragment {
    private BottomsheetEditChildBinding binding;
    private ChildModel child;
    private OnChildUpdatedListener listener;
    private Calendar selectedDate = Calendar.getInstance();
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public interface OnChildUpdatedListener {
        void onChildUpdated(ChildModel child);
    }

    public static EditChildBottomSheet newInstance(ChildModel child) {
        EditChildBottomSheet fragment = new EditChildBottomSheet();
        Bundle args = new Bundle();
        args.putString("childId", child.getId());
        args.putString("childName", child.getName());
        args.putString("childClass", child.getClassName());
        args.putString("childAvatar", child.getAvatar());
        args.putBoolean("childGender", child.getGender() != null ? child.getGender() : true);
        args.putString("username", child.getUsername());
        args.putString("password", child.getPassword());
        args.putString("nickname", child.getNickname());
        args.putString("school", child.getSchool());
        args.putString("birthDate", child.getBirthDate());
        args.putInt("childLevel", child.getLevel());
        args.putInt("currentXP", child.getCurrentXP());
        args.putInt("maxXP", child.getMaxXP());
        args.putInt("coins", child.getCoins());
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnChildUpdatedListener(OnChildUpdatedListener listener) {
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
        binding = BottomsheetEditChildBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadChildData();
        setupListeners();
    }

    private void loadChildData() {
        if (getArguments() != null) {
            String name = getArguments().getString("childName", "");
            String className = getArguments().getString("childClass", "");
            String avatar = getArguments().getString("childAvatar", "😊");
            boolean gender = getArguments().getBoolean("childGender", true); // true = Nam
            String username = getArguments().getString("username", "");
            String school = getArguments().getString("school", "");
            String birthDate = getArguments().getString("birthDate", null);

            binding.edtChildName.setText(name);
            binding.edtChildClass.setText(className);
            binding.txtAvatar.setText(avatar);
            binding.edtUsername.setText(username);
            binding.edtSchool.setText(school != null ? school : "");

            // Set spinner selection: 0 = Nam, 1 = Nữ
            binding.spinnerGender.setSelection(gender ? 0 : 1);
            
            // Set ngày sinh
            if (birthDate != null && !birthDate.isEmpty()) {
                try {
                    selectedDate.setTime(apiDateFormat.parse(birthDate));
                    binding.edtBirthDate.setText(displayDateFormat.format(selectedDate.getTime()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setupListeners() {
        binding.btnCancel.setOnClickListener(v -> dismiss());

        binding.btnUpdate.setOnClickListener(v -> {
            if (validateInput()) {
                updateChild();
            }
        });

        binding.btnChangeAvatar.setOnClickListener(v -> {
            // TODO: Show avatar picker
            binding.txtAvatar.setText(getRandomAvatar());
        });
        
        // Date picker cho ngày sinh
        binding.edtBirthDate.setOnClickListener(v -> showDatePicker());
    }
    
    private void showDatePicker() {
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);
        
        // Nếu chưa có ngày sinh, mặc định năm 2015
        if (binding.edtBirthDate.getText().toString().isEmpty()) {
            year = 2015;
            month = 0;
            day = 1;
        }
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
                    binding.edtBirthDate.setText(displayDateFormat.format(selectedDate.getTime()));
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

        if (name.isEmpty()) {
            binding.edtChildName.setError("Vui lòng nhập tên bé");
            return false;
        }

        if (className.isEmpty()) {
            binding.edtChildClass.setError("Vui lòng nhập lớp");
            return false;
        }

        return true;
    }

    private void updateChild() {
        if (getArguments() == null) return;

        String id = getArguments().getString("childId", "");
        String name = binding.edtChildName.getText().toString().trim();
        String className = binding.edtChildClass.getText().toString().trim();
        String avatar = binding.txtAvatar.getText().toString();
        String school = binding.edtSchool.getText().toString().trim();
        // Lấy giới tính: Nam = true, Nữ = false
        String genderStr = binding.spinnerGender.getSelectedItem().toString();
        Boolean gender = "Nam".equals(genderStr);
        String username = binding.edtUsername.getText().toString().trim();
        String newPassword = binding.edtPassword.getText().toString().trim();
        String nickname = getArguments().getString("nickname", "");
        int level = getArguments().getInt("childLevel", 1);
        int currentXP = getArguments().getInt("currentXP", 0);
        int maxXP = getArguments().getInt("maxXP", 100);
        int coins = getArguments().getInt("coins", 0);
        
        // Lấy ngày sinh dạng API (yyyy-MM-dd)
        String birthDate = null;
        if (!binding.edtBirthDate.getText().toString().isEmpty()) {
            birthDate = apiDateFormat.format(selectedDate.getTime());
        }

        ChildModel updatedChild = new ChildModel(id, name, className, level,
                currentXP, maxXP, coins, avatar);
        updatedChild.setGender(gender);
        updatedChild.setUsername(username);
        updatedChild.setNickname(nickname);
        updatedChild.setSchool(school.isEmpty() ? null : school);
        updatedChild.setBirthDate(birthDate);
        
        // Nếu có nhập password mới thì dùng password mới, không thì null (không đổi)
        if (!newPassword.isEmpty()) {
            updatedChild.setPassword(newPassword);
        } else {
            updatedChild.setPassword(null);
        }

        if (listener != null) {
            listener.onChildUpdated(updatedChild);
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
