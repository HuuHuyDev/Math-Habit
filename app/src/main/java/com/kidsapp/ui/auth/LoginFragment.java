package com.kidsapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kidsapp.R;
import com.kidsapp.databinding.FragmentLoginBinding;
import com.kidsapp.ui.child.main.ChildMainActivity;
import com.kidsapp.ui.parent.main.ParentMainActivity;
import com.kidsapp.viewmodel.AuthViewModel;

import java.util.Arrays;

/**
 * Login Fragment with Google and Facebook login support
 */
public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    
    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;
    
    // Google Sign-In
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    
    // Facebook Login
    private CallbackManager callbackManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupGoogleSignIn();
        setupFacebookLogin();
    }

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

    private void setupGoogleSignIn() {
        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // Register activity result launcher for Google Sign-In
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleGoogleSignInResult(task);
                });
    }

    private void setupFacebookLogin() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "Facebook login success");
                        AccessToken accessToken = loginResult.getAccessToken();
                        handleFacebookAccessToken(accessToken);
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "Facebook login cancelled");
                        Toast.makeText(getContext(), "Đăng nhập Facebook bị hủy", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull FacebookException error) {
                        Log.e(TAG, "Facebook login error", error);
                        Toast.makeText(getContext(), "Lỗi đăng nhập Facebook: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
            Navigation.findNavController(v).navigate(R.id.action_login_to_forgot_password);
        });

        binding.tvRegister.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_login_to_register);
        });

        // Google login
        binding.btnLoginGoogle.setOnClickListener(v -> signInWithGoogle());

        // Facebook login
        binding.btnLoginFacebook.setOnClickListener(v -> signInWithFacebook());
    }

    private void signInWithGoogle() {
        // Sign out first to allow user to choose account
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String idToken = account.getIdToken();
                Log.d(TAG, "Google sign in success, idToken: " + (idToken != null ? "received" : "null"));
                
                if (idToken != null) {
                    // Send token to backend
                    authViewModel.loginWithGoogle(idToken);
                } else {
                    Toast.makeText(getContext(), "Không lấy được token từ Google", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (ApiException e) {
            Log.e(TAG, "Google sign in failed", e);
            String errorMessage;
            switch (e.getStatusCode()) {
                case 12501:
                    errorMessage = "Đăng nhập bị hủy";
                    break;
                case 12502:
                    errorMessage = "Đang xử lý đăng nhập...";
                    break;
                case 10:
                    errorMessage = "Cấu hình Google Sign-In chưa đúng";
                    break;
                default:
                    errorMessage = "Lỗi đăng nhập Google: " + e.getStatusCode();
            }
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                callbackManager,
                Arrays.asList("email", "public_profile")
        );
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken: " + token.getToken());
        authViewModel.loginWithFacebook(token.getToken());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result to Facebook CallbackManager
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private boolean validateInput(String email, String password) {
        boolean isValid = true;

        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);

        if (email.isEmpty()) {
            binding.tilEmail.setError("Vui lòng nhập email");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError("Email không hợp lệ");
            isValid = false;
        }

        if (password.isEmpty()) {
            binding.tilPassword.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        } else if (password.length() < 6) {
            binding.tilPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        }

        return isValid;
    }

    private void observeViewModel() {
        authViewModel.getAuthResponse().observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.user != null) {
                Toast.makeText(getContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                navigateByRole(response.user.role);
            }
        });

        authViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        authViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.btnLogin.setEnabled(!isLoading);
            binding.btnLoginGoogle.setEnabled(!isLoading);
            binding.btnLoginFacebook.setEnabled(!isLoading);
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    private void navigateByRole(String role) {
        Intent intent;
        if ("PARENT".equalsIgnoreCase(role)) {
            intent = new Intent(requireContext(), ParentMainActivity.class);
        } else if ("CHILD".equalsIgnoreCase(role)) {
            intent = new Intent(requireContext(), ChildMainActivity.class);
        } else {
            intent = new Intent(requireContext(), ParentMainActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
