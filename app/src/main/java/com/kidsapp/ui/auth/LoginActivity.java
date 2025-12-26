package com.kidsapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.kidsapp.R;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.databinding.ActivityLoginBinding;
import com.kidsapp.ui.child.main.ChildMainActivity;
import com.kidsapp.ui.parent.main.ParentMainActivity;

/**
 * Login Activity - Entry point of the app
 */
public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private NavController navController;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sharedPref = new SharedPref(this);
        
        // Check if user is already logged in
        if (sharedPref.isLoggedIn() && sharedPref.getAuthToken() != null) {
            navigateToMain();
            return;
        }
        
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
    
    private void navigateToMain() {
        String role = sharedPref.getUserRole();
        Intent intent;
        
        if ("CHILD".equalsIgnoreCase(role)) {
            intent = new Intent(this, ChildMainActivity.class);
        } else {
            intent = new Intent(this, ParentMainActivity.class);
        }
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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

