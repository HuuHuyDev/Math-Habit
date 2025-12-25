package com.kidsapp.ui.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kidsapp.adapter.RecentCodeAdapter;
import com.kidsapp.databinding.DialogJoinByCodeBinding;
import com.kidsapp.model.RecentCode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class JoinByChallengeCodeActivity extends AppCompatActivity {
    
    private DialogJoinByCodeBinding binding;
    private RecentCodeAdapter recentCodeAdapter;
    private List<RecentCode> recentCodes;
    
    // Pattern for valid challenge code (8-12 characters, letters and numbers)
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z0-9]{8,12}$");
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        showJoinByCodeDialog();
    }
    
    private void showJoinByCodeDialog() {
        binding = DialogJoinByCodeBinding.inflate(getLayoutInflater());
        
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(binding.getRoot());
        
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        
        setupViews();
        setupRecyclerView();
        loadRecentCodes();
        
        // Setup click listeners
        binding.btnClose.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });
        
        binding.btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });
        
        binding.btnJoinChallenge.setOnClickListener(v -> {
            String code = binding.edtChallengeCode.getText().toString().trim().toUpperCase();
            if (validateCode(code)) {
                joinChallenge(code);
                dialog.dismiss();
            }
        });
        
        // Text watcher for code input
        binding.edtChallengeCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String code = s.toString().trim().toUpperCase();
                boolean isValid = CODE_PATTERN.matcher(code).matches();
                
                binding.btnJoinChallenge.setEnabled(isValid);
                
                if (s.length() > 0 && !isValid && s.length() >= 8) {
                    binding.layoutCodeInput.setError("Mã không hợp lệ");
                } else {
                    binding.layoutCodeInput.setError(null);
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        dialog.show();
    }
    
    private void setupViews() {
        // Auto-uppercase input
        binding.edtChallengeCode.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = binding.edtChallengeCode.getText().toString().toUpperCase();
                binding.edtChallengeCode.setText(text);
            }
        });
    }
    
    private void setupRecyclerView() {
        recentCodes = new ArrayList<>();
        recentCodeAdapter = new RecentCodeAdapter(recentCodes, this::onRecentCodeClick);
        
        binding.recyclerRecentCodes.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerRecentCodes.setAdapter(recentCodeAdapter);
    }
    
    private void loadRecentCodes() {
        // Mock data - replace with actual data from SharedPreferences or database
        recentCodes.clear();
        recentCodes.add(new RecentCode("ABC123XYZ", "Nguyễn Văn A", "2 giờ trước"));
        recentCodes.add(new RecentCode("DEF456UVW", "Trần Thị B", "1 ngày trước"));
        recentCodes.add(new RecentCode("GHI789RST", "Lê Văn C", "3 ngày trước"));
        
        if (!recentCodes.isEmpty()) {
            binding.txtRecentCodesTitle.setVisibility(android.view.View.VISIBLE);
            binding.recyclerRecentCodes.setVisibility(android.view.View.VISIBLE);
            recentCodeAdapter.notifyDataSetChanged();
        }
    }
    
    private void onRecentCodeClick(RecentCode recentCode) {
        binding.edtChallengeCode.setText(recentCode.getCode());
        binding.edtChallengeCode.setSelection(recentCode.getCode().length());
    }
    
    private boolean validateCode(String code) {
        if (code.isEmpty()) {
            binding.layoutCodeInput.setError("Vui lòng nhập mã thách đấu");
            return false;
        }
        
        if (!CODE_PATTERN.matcher(code).matches()) {
            binding.layoutCodeInput.setError("Mã không hợp lệ. Mã phải có 8-12 ký tự (chữ và số)");
            return false;
        }
        
        binding.layoutCodeInput.setError(null);
        return true;
    }
    
    private void joinChallenge(String code) {
        // Show loading
        binding.btnJoinChallenge.setEnabled(false);
        binding.btnJoinChallenge.setText("Đang tham gia...");
        
        // Simulate API call
        new android.os.Handler().postDelayed(() -> {
            // Mock response - replace with actual API call
            if (isValidChallengeCode(code)) {
                // Save to recent codes
                saveRecentCode(code);
                
                // Navigate to challenge screen
                Intent intent = new Intent(this, ChallengeActivity.class);
                intent.putExtra("challenge_code", code);
                intent.putExtra("challenge_mode", "joined");
                startActivity(intent);
                
                Toast.makeText(this, "Tham gia thách đấu thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // Show error
                binding.layoutCodeInput.setError("Mã thách đấu không tồn tại hoặc đã hết hạn");
                binding.btnJoinChallenge.setEnabled(true);
                binding.btnJoinChallenge.setText("Tham gia");
                
                Toast.makeText(this, "Không thể tham gia thách đấu", Toast.LENGTH_SHORT).show();
            }
        }, 2000); // Simulate network delay
    }
    
    private boolean isValidChallengeCode(String code) {
        // Mock validation - replace with actual API call
        // For demo, accept codes that start with "ABC" or "DEF"
        return code.startsWith("ABC") || code.startsWith("DEF") || code.startsWith("GHI");
    }
    
    private void saveRecentCode(String code) {
        // Save to SharedPreferences or database
        // For now, just add to current list if not exists
        boolean exists = recentCodes.stream().anyMatch(rc -> rc.getCode().equals(code));
        if (!exists) {
            recentCodes.add(0, new RecentCode(code, "Bạn", "Vừa xong"));
            if (recentCodes.size() > 5) {
                recentCodes.remove(recentCodes.size() - 1);
            }
        }
    }
}