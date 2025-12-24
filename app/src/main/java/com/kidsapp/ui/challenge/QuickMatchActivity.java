package com.kidsapp.ui.challenge;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.kidsapp.R;
import com.kidsapp.ui.child.challenge.QuickMatchFragment;

public class QuickMatchActivity extends AppCompatActivity {
    
    private String challengeCode;
    private String challengeMode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_match);
        
        // Get data from intent
        challengeCode = getIntent().getStringExtra("challenge_code");
        challengeMode = getIntent().getStringExtra("challenge_mode");
        
        setupViews();
        loadFragment();
    }
    
    private void setupViews() {
        // Setup back button
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }
    }
    
    private void loadFragment() {
        // Create QuickMatchFragment with data
        QuickMatchFragment fragment = new QuickMatchFragment();
        
        // Pass data to fragment via Bundle
        Bundle args = new Bundle();
        args.putString("challenge_code", challengeCode);
        args.putString("challenge_mode", challengeMode);
        fragment.setArguments(args);
        
        // Replace fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Add animation when going back
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}