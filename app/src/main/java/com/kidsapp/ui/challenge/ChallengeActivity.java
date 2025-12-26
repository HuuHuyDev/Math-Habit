package com.kidsapp.ui.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kidsapp.databinding.ActivityChallengeBinding;

public class ChallengeActivity extends AppCompatActivity {

    private ActivityChallengeBinding binding;
    private String challengeCode;
    private String challengeMode;
    private boolean isChallengeStarted = false;
    private boolean isWaitingForOpponent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChallengeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get data from intent
        challengeCode = getIntent().getStringExtra("challenge_code");
        challengeMode = getIntent().getStringExtra("challenge_mode");

        setupViews();
        loadChallengeData();
    }

    /**
     * Setup UI components
     */
    private void setupViews() {

        // Back button (đồng bộ với Back hệ thống)
        ImageButton btnBack = findViewById(com.kidsapp.R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> handleBackAction());
        }

        // Start challenge button
        if (binding.btnStartChallenge != null) {
            binding.btnStartChallenge.setOnClickListener(v -> startChallenge());
        }
    }

    /**
     * Xử lý Back (dùng chung cho btnBack & Back hệ thống)
     */
    private void handleBackAction() {
        if (isChallengeStarted) {
            showExitConfirmationDialog();
        } else {
            finishWithAnimation();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handleBackAction();
    }

    /**
     * Dialog xác nhận thoát khi đang thi đấu
     */
    private void showExitConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Thoát thách đấu")
                .setMessage("Thách đấu đang diễn ra. Nếu thoát bây giờ, bạn sẽ thua cuộc. Bạn có chắc chắn muốn thoát không?")
                .setPositiveButton("Thoát (Thua cuộc)", (dialog, which) -> handleChallengeExit())
                .setNegativeButton("Ở lại", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    /**
     * Xử lý khi người chơi thoát giữa chừng
     */
    private void handleChallengeExit() {
        // TODO: Call API báo thua
        Toast.makeText(this, "Bạn đã thoát thách đấu", Toast.LENGTH_SHORT).show();
        finishWithAnimation();
    }

    /**
     * Finish activity với animation
     */
    private void finishWithAnimation() {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    /**
     * Load dữ liệu thách đấu (mock)
     */
    private void loadChallengeData() {
        if (challengeCode == null) return;

        binding.txtChallengeTitle.setText("Thách đấu: " + challengeCode);

        if ("joined".equals(challengeMode)) {
            binding.txtChallengeDesc.setText("Bạn đã tham gia thách đấu thành công!");
            binding.btnStartChallenge.setText("Bắt đầu thách đấu");
            binding.btnStartChallenge.setEnabled(true);
            isWaitingForOpponent = false;
        } else {
            binding.txtChallengeDesc.setText("Thách đấu đã được tạo");
            binding.btnStartChallenge.setText("Chờ đối thủ tham gia");
            binding.btnStartChallenge.setEnabled(false);
            isWaitingForOpponent = true;
        }

        binding.txtChallengeInfo.setText(
                "• 20 câu hỏi toán học\n" +
                        "• Thời gian: 10 phút\n" +
                        "• Chế độ: Thách đấu tiêu chuẩn"
        );
    }

    /**
     * Bắt đầu thách đấu - chuyển đến QuickMatchFragment
     */
    private void startChallenge() {
        isChallengeStarted = true;
        isWaitingForOpponent = false;

        binding.btnStartChallenge.setText("Đang khởi động...");
        binding.btnStartChallenge.setEnabled(false);

        // Chuyển đến QuickMatchActivity
        Intent intent = new Intent(this, QuickMatchActivity.class);
        intent.putExtra("challenge_code", challengeCode);
        intent.putExtra("challenge_mode", challengeMode);
        startActivity(intent);
        
        // Kết thúc Activity hiện tại
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
