package com.kidsapp.ui.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.kidsapp.R;
import com.kidsapp.data.repository.AuthRepository;
import com.kidsapp.databinding.FragmentResetPasswordBinding;

public class ResetPasswordFragment extends Fragment {
    private FragmentResetPasswordBinding binding;
    private String email;
    private String otp;
    private AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        authRepository = new AuthRepository(requireContext());

        if (getArguments() != null) {
            email = getArguments().getString("email", "");
            otp = getArguments().getString("otp", "");
        }

        setupPasswordValidation();
        setupClickListeners();
    }

    private void setupPasswordValidation() {
        binding.etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void validatePassword(String password) {
        int greenColor = ContextCompat.getColor(requireContext(), R.color.status_success);
        int grayColor = ContextCompat.getColor(requireContext(), R.color.text_hint);

        // Check length >= 8
        if (password.length() >= 8) {
            binding.tvReqLength.setTextColor(greenColor);
        } else {
            binding.tvReqLength.setTextColor(grayColor);
        }

        // Check uppercase and lowercase
        boolean hasUpper = !password.equals(password.toLowerCase());
        boolean hasLower = !password.equals(password.toUpperCase());
        if (hasUpper && hasLower) {
            binding.tvReqUppercase.setTextColor(greenColor);
        } else {
            binding.tvReqUppercase.setTextColor(grayColor);
        }

        // Check number
        boolean hasNumber = password.matches(".*\\d.*");
        if (hasNumber) {
            binding.tvReqNumber.setTextColor(greenColor);
        } else {
            binding.tvReqNumber.setTextColor(grayColor);
        }
    }

    private void setupClickListeners() {
        binding.btnResetPassword.setOnClickListener(v -> resetPassword());

        binding.tvBackToLogin.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_resetPassword_to_login)
        );
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 8) return false;
        
        boolean hasUpper = !password.equals(password.toLowerCase());
        boolean hasLower = !password.equals(password.toUpperCase());
        boolean hasNumber = password.matches(".*\\d.*");
        
        return hasUpper && hasLower && hasNumber;
    }

    private void resetPassword() {
        String newPassword = binding.etNewPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        // Clear previous errors
        binding.tilNewPassword.setError(null);
        binding.tilConfirmPassword.setError(null);

        if (newPassword.isEmpty()) {
            binding.tilNewPassword.setError("Vui lòng nhập mật khẩu mới");
            return;
        }

        if (newPassword.length() < 8) {
            binding.tilNewPassword.setError("Mật khẩu phải có ít nhất 8 ký tự");
            return;
        }

        // Check uppercase
        if (newPassword.equals(newPassword.toLowerCase())) {
            binding.tilNewPassword.setError("Mật khẩu phải có ít nhất 1 chữ hoa");
            return;
        }

        // Check lowercase
        if (newPassword.equals(newPassword.toUpperCase())) {
            binding.tilNewPassword.setError("Mật khẩu phải có ít nhất 1 chữ thường");
            return;
        }

        // Check number
        if (!newPassword.matches(".*\\d.*")) {
            binding.tilNewPassword.setError("Mật khẩu phải có ít nhất 1 số");
            return;
        }

        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        binding.btnResetPassword.setEnabled(false);
        binding.btnResetPassword.setText("Đang xử lý...");

        authRepository.resetPassword(email, otp, newPassword, new AuthRepository.SimpleCallback() {
            @Override
            public void onSuccess(String message) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    binding.btnResetPassword.setEnabled(true);
                    binding.btnResetPassword.setText("Đặt lại mật khẩu");
                    Toast.makeText(requireContext(), "Đặt lại mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigate(R.id.action_resetPassword_to_login);
                });
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    binding.btnResetPassword.setEnabled(true);
                    binding.btnResetPassword.setText("Đặt lại mật khẩu");
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
