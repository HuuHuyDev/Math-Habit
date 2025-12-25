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
            String username = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            
            if (validateInput(username, password)) {
                handleDemoLogin(username, password);
            }
        });
        
        // Forgot password
        binding.tvForgotPassword.setOnClickListener(v -> {
            // Navigate to forgot password fragment
            Navigation.findNavController(v).navigate(R.id.action_login_to_forgot_password);
        });

        binding.tvRegister.setOnClickListener(v -> {
            // Navigate to register fragment
            Navigation.findNavController(v).navigate(R.id.action_login_to_register);
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

    /**
     * Xử lý đăng nhập demo với tài khoản hardcoded
     */
    private void handleDemoLogin(String username, String password) {
        // Tài khoản Parent demo
        if (username.equals("parent") && password.equals("123456")) {
            loginAsParent();
            return;
        }
        
        // Tài khoản Children demo
        if (username.equals("huy") && password.equals("123456")) {
            loginAsChild("1", "Nguyễn Minh Linh");
            return;
        }
        
        if (username.equals("baoankid") && password.equals("123456")) {
            loginAsChild("2", "Trần Bảo An");
            return;
        }
        
        if (username.equals("minhchaukid") && password.equals("123456")) {
            loginAsChild("3", "Lê Minh Châu");
            return;
        }
        
        // Sai tài khoản
        Toast.makeText(requireContext(), 
                "Tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
    }

    /**
     * Đăng nhập với vai trò Parent
     */
    private void loginAsParent() {
//        // Lưu thông tin đăng nhập
//        com.kidsapp.data.local.SharedPref sharedPref =
//                new com.kidsapp.data.local.SharedPref(requireContext());
//        sharedPref.setLoggedIn(true);
////        sharedPref.setUserRole("parent");
////        sharedPref.setUserName("Nguyễn Phương");
        
        Toast.makeText(requireContext(), "Đăng nhập thành công - Parent", Toast.LENGTH_SHORT).show();
        
        // Navigate to ParentMainActivity
        android.content.Intent intent = new android.content.Intent(
                requireContext(), com.kidsapp.ui.parent.main.ParentMainActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | 
                android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    /**
     * Đăng nhập với vai trò Child
     */
    private void loginAsChild(String childId, String childName) {
        // Lưu thông tin đăng nhập
        com.kidsapp.data.local.SharedPref sharedPref = 
                new com.kidsapp.data.local.SharedPref(requireContext());
        sharedPref.setLoggedIn(true);
//        sharedPref.setUserRole("child");
//        sharedPref.setUserName(childName);
//        sharedPref.setUserId(childId);
        
        Toast.makeText(requireContext(), 
                "Đăng nhập thành công - " + childName, Toast.LENGTH_SHORT).show();
        
        // Navigate to ChildMainActivity
        android.content.Intent intent = new android.content.Intent(
                requireContext(), com.kidsapp.ui.child.main.ChildMainActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | 
                android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

