package com.kidsapp.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.kidsapp.R;
import com.kidsapp.databinding.FragmentLoginBinding;
import com.kidsapp.utils.Constants;
import com.kidsapp.viewmodel.AuthViewModel;

/**
 * Login Fragment
 */
public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        setupClickListeners();
        observeViewModel();
    }

    private void setupClickListeners() {
        // Login button
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            
            if (validateInput(email, password)) {
                authViewModel.login(email, password);
            }
        });
        
        // Forgot password
        binding.tvForgotPassword.setOnClickListener(v -> {
            // Navigate to forgot password fragment
            Navigation.findNavController(v).navigate(R.id.action_login_to_forgot_password);
        });
        
        // Google login
        binding.btnLoginGoogle.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đăng nhập với Google", Toast.LENGTH_SHORT).show();
            // TODO: Implement Google login
        });
        
        // Facebook login
        binding.btnLoginFacebook.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đăng nhập với Facebook", Toast.LENGTH_SHORT).show();
            // TODO: Implement Facebook login
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            binding.tilEmail.setError("Vui lòng nhập email");
            return false;
        }
        
        if (password.isEmpty()) {
            binding.tilPassword.setError("Vui lòng nhập mật khẩu");
            return false;
        }
        
        if (password.length() < 6) {
            binding.tilPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }
        
        return true;
    }

    private void observeViewModel() {
        authViewModel.getAuthResponse().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                // Navigate to choose role screen
                Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_login_to_choose_role);
            }
        });
        
        authViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        
        authViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.btnLogin.setEnabled(!isLoading);
            // TODO: Show loading indicator
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

