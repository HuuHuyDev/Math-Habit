package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kidsapp.R;
import com.kidsapp.databinding.FragmentChallengeResultBinding;
import com.kidsapp.ui.child.home.ChildHomeFragment;

import java.util.Locale;

/**
 * Fragment hiển thị kết quả battle
 * Hiển thị điểm số, thống kê, phần thưởng
 */
public class ChallengeResultFragment extends Fragment {

    private static final String ARG_PLAYER1_SCORE = "player1_score";
    private static final String ARG_PLAYER2_SCORE = "player2_score";
    private static final String ARG_CORRECT = "correct";
    private static final String ARG_WRONG = "wrong";
    private static final String ARG_TIME = "time";
    private static final String ARG_IS_WIN = "is_win";

    private FragmentChallengeResultBinding binding;
    private int player1Score;
    private int player2Score;
    private int correctCount;
    private int wrongCount;
    private long totalTime;
    private boolean isWin;

    public static ChallengeResultFragment newInstance(int player1Score, int player2Score,
                                                      int correct, int wrong, long time, boolean isWin) {
        ChallengeResultFragment fragment = new ChallengeResultFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PLAYER1_SCORE, player1Score);
        args.putInt(ARG_PLAYER2_SCORE, player2Score);
        args.putInt(ARG_CORRECT, correct);
        args.putInt(ARG_WRONG, wrong);
        args.putLong(ARG_TIME, time);
        args.putBoolean(ARG_IS_WIN, isWin);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChallengeResultBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        loadArguments();
        displayResult();
        setupButtons();
    }

    private void loadArguments() {
        if (getArguments() != null) {
            player1Score = getArguments().getInt(ARG_PLAYER1_SCORE);
            player2Score = getArguments().getInt(ARG_PLAYER2_SCORE);
            correctCount = getArguments().getInt(ARG_CORRECT);
            wrongCount = getArguments().getInt(ARG_WRONG);
            totalTime = getArguments().getLong(ARG_TIME);
            isWin = getArguments().getBoolean(ARG_IS_WIN);
        }
    }

    private void displayResult() {
        // Result icon and title
        if (isWin) {
            binding.imgResult.setImageResource(R.drawable.ic_trophy);
            binding.txtResultTitle.setText("Chiến thắng!");
            binding.txtResultTitle.setTextColor(getResources().getColor(R.color.primary, null));
            binding.txtResultMessage.setText("Bạn đã chiến thắng đối thủ!");
        } else {
            binding.imgResult.setImageResource(R.drawable.ic_close_circle);
            binding.txtResultTitle.setText("Thua cuộc");
            binding.txtResultTitle.setTextColor(getResources().getColor(R.color.error, null));
            binding.txtResultMessage.setText("Cố gắng hơn lần sau nhé!");
        }

        // Scores
        binding.txtPlayer1Score.setText(String.valueOf(player1Score));
        binding.txtPlayer2Score.setText(String.valueOf(player2Score));

        // Stats
        binding.txtCorrectCount.setText(String.valueOf(correctCount));
        binding.txtWrongCount.setText(String.valueOf(wrongCount));
        binding.txtTotalTime.setText(formatTime(totalTime));

        // Rewards
        int rewardCoins = isWin ? 50 : 20;
        binding.txtRewardCoins.setText("+" + rewardCoins + " xu");
        
        // Hide rewards card if lost
        if (!isWin) {
            binding.cardRewards.setVisibility(View.GONE);
        }
    }

    private String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    private void setupButtons() {
        binding.btnClose.setOnClickListener(v -> 
            requireActivity().onBackPressed());

        binding.btnPlayAgain.setOnClickListener(v -> {
            // Start new quick match
            QuickMatchFragment fragment = new QuickMatchFragment();
            requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.childHomeHost, fragment)
                .commit();
        });

        binding.btnGoHome.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.childHomeHost, new ChildHomeFragment())
                .commit();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
