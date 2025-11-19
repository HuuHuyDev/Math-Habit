package com.kidsapp.ui.child.main;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kidsapp.R;
import com.kidsapp.databinding.ActivityChildMainBinding;

/**
 * Child Main Activity with Bottom Navigation
 */
public class ChildMainActivity extends AppCompatActivity {
    private ActivityChildMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();
    }

    private void setupNavigation() {
        // Get NavController
        navController = Navigation.findNavController(this, R.id.navHostFragment);
        
        // Setup Bottom Navigation
        BottomNavigationView bottomNav = binding.bottomNavigationView;
        NavigationUI.setupWithNavController(bottomNav, navController);
        
        // Configure AppBar
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
            R.id.nav_home,
            R.id.nav_tasks,
            R.id.nav_quiz,
            R.id.nav_reward,
            R.id.nav_shop,
            R.id.nav_profile
        ).build();
        
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}

