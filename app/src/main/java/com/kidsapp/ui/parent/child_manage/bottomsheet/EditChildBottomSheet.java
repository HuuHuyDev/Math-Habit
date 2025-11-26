package com.kidsapp.ui.parent.child_manage.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kidsapp.databinding.BottomsheetEditChildBinding;
import com.kidsapp.ui.parent.child_manage.model.ChildModel;

/**
 * BottomSheet chỉnh sửa thông tin bé
 */
public class EditChildBottomSheet extends BottomSheetDialogFragment {
    private BottomsheetEditChildBinding binding;
    private ChildModel child;
    private OnChildUpdatedListener listener;

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
        args.putString("childGender", child.getGender());
        args.putString("username", child.getUsername());
        args.putString("password", child.getPassword());
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
            String gender = getArguments().getString("childGender", "Nam");
            String username = getArguments().getString("username", "");

            binding.edtChildName.setText(name);
            binding.edtChildClass.setText(className);
            binding.txtAvatar.setText(avatar);
            binding.edtUsername.setText(username);

            // Set spinner selection
            String[] genders = {"Nam", "Nữ", "Khác"};
            for (int i = 0; i < genders.length; i++) {
                if (genders[i].equals(gender)) {
                    binding.spinnerGender.setSelection(i);
                    break;
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
        String gender = binding.spinnerGender.getSelectedItem().toString();
        String username = binding.edtUsername.getText().toString().trim();
        String newPassword = binding.edtPassword.getText().toString().trim();
        String oldPassword = getArguments().getString("password", "");
        int level = getArguments().getInt("childLevel", 1);
        int currentXP = getArguments().getInt("currentXP", 0);
        int maxXP = getArguments().getInt("maxXP", 100);
        int coins = getArguments().getInt("coins", 0);

        ChildModel updatedChild = new ChildModel(id, name, className, level,
                currentXP, maxXP, coins, avatar);
        updatedChild.setGender(gender);
        updatedChild.setUsername(username);
        
        // Nếu có nhập password mới thì dùng password mới, không thì giữ password cũ
        if (!newPassword.isEmpty()) {
            updatedChild.setPassword(newPassword);
        } else {
            updatedChild.setPassword(oldPassword);
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
