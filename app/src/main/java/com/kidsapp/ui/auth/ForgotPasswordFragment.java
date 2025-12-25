package com.kidsapp.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.kidsapp.R;
import com.kidsapp.databinding.FragmentForgotPasswordBinding;

/**
 * Forgot Password Fragment
 */
public class ForgotPasswordFragment extends Fragment {
    private FragmentForgotPasswordBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.tvBackToLogin.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_forgotPassword_to_login)
        );

        binding.btnSendResetLink.setOnClickListener(v -> sendResetLink());
    }

    private void sendResetLink() {
        String email = binding.etForgotEmail.getText().toString().trim();

        if (email.isEmpty()) {
            binding.tilForgotEmail.setError("Vui lòng nhập email");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilForgotEmail.setError("Email không hợp lệ");
            return;
        }

        binding.tilForgotEmail.setError(null);

        // TODO: Call API to send OTP
        Toast.makeText(requireContext(), "Đang gửi mã OTP...", Toast.LENGTH_SHORT).show();

        // Navigate to OTP verification screen
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        Navigation.findNavController(requireView()).navigate(R.id.action_forgotPassword_to_verifyOtp, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

