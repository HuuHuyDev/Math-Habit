package com.kidsapp.ui.auth;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.kidsapp.R;
import com.kidsapp.databinding.ActivityLoginBinding;

/**
 * Login Activity - Entry point of the app
 */
public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public boolean onSupportNavigateUp() {
        try {
            navController = Navigation.findNavController(this, R.id.fragmentContainer);
            return navController.navigateUp() || super.onSupportNavigateUp();
        } catch (Exception e) {
            return super.onSupportNavigateUp();
        }
    }
}

