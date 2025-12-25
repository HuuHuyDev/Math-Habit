package com.kidsapp.ui.auth;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.kidsapp.R;
import com.kidsapp.databinding.FragmentVerifyOtpBinding;

public class VerifyOtpFragment extends Fragment {
    private FragmentVerifyOtpBinding binding;
    private String email;
    private CountDownTimer countDownTimer;
    private EditText[] otpFields;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentVerifyOtpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            email = getArguments().getString("email", "");
            binding.tvEmail.setText(email);
        }

        otpFields = new EditText[]{
                binding.etOtp1, binding.etOtp2, binding.etOtp3,
                binding.etOtp4, binding.etOtp5, binding.etOtp6
        };

        setupOtpInputs();
        setupClickListeners();
        startCountdownTimer();
    }

    private void setupOtpInputs() {
        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpFields.length - 1) {
                        otpFields[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            otpFields[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL && 
                    otpFields[index].getText().toString().isEmpty() && index > 0) {
                    otpFields[index - 1].requestFocus();
                    return true;
                }
                return false;
            });
        }
    }

    private void setupClickListeners() {
        binding.btnVerifyOtp.setOnClickListener(v -> verifyOtp());

        binding.tvResendOtp.setOnClickListener(v -> resendOtp());

        binding.tvBackToForgot.setOnClickListener(v -> 
            Navigation.findNavController(v).popBackStack()
        );

        binding.ivBack.setOnClickListener(v -> 
            Navigation.findNavController(v).popBackStack()
        );
    }

    private void startCountdownTimer() {
        countDownTimer = new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                binding.tvTimer.setText(String.format("Mã hết hạn sau: %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                binding.tvTimer.setText("Mã đã hết hạn");
                binding.tvResendOtp.setEnabled(true);
            }
        }.start();
    }

    private String getOtpCode() {
        StringBuilder otp = new StringBuilder();
        for (EditText field : otpFields) {
            otp.append(field.getText().toString());
        }
        return otp.toString();
    }

    private void verifyOtp() {
        String otp = getOtpCode();
        if (otp.length() != 6) {
            Toast.makeText(requireContext(), "Vui lòng nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Call API to verify OTP
        // For now, navigate to reset password screen
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("otp", otp);
        Navigation.findNavController(requireView()).navigate(R.id.action_verifyOtp_to_resetPassword, bundle);
    }

    private void resendOtp() {
        // TODO: Call API to resend OTP
        Toast.makeText(requireContext(), "Đã gửi lại mã OTP", Toast.LENGTH_SHORT).show();
        
        for (EditText field : otpFields) {
            field.setText("");
        }
        otpFields[0].requestFocus();
        
        startCountdownTimer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        binding = null;
    }
}
