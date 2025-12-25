package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kidsapp.R;
import com.kidsapp.data.repository.ChallengeRepository;
import com.kidsapp.databinding.FragmentWaitingRoomBinding;

/**
 * Fragment màn hình chờ đối thủ chấp nhận lời mời
 */
public class WaitingRoomFragment extends Fragment {

    private FragmentWaitingRoomBinding binding;
    private ChallengeRepository repository;
    private Handler handler;
    
    private String opponentId;
    private String opponentName;
    private int opponentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWaitingRoomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        repository = new ChallengeRepository(requireContext());
        handler = new Handler(Looper.getMainLooper());
        
        loadArguments();
        setupViews();
        startWaiting();
    }

    private void loadArguments() {
        Bundle args = getArguments();
        if (args != null) {
            opponentId = args.getString("opponent_id", "");
            opponentName = args.getString("opponent_name", "Đối thủ");
            opponentLevel = args.getInt("opponent_level", 1);
        }
    }

    private void setupViews() {
        binding.txtOpponentName.setText("Đã gửi lời mời đến " + opponentName);
        binding.txtOpponentNameCard.setText(opponentName);
        binding.txtOpponentLevel.setText("Level " + opponentLevel);

        binding.btnClose.setOnClickListener(v -> cancelAndGoBack());
        binding.btnCancel.setOnClickListener(v -> cancelAndGoBack());
    }

    private void startWaiting() {
        // Demo: Auto accept sau 5s
        handler.postDelayed(this::onOpponentAccepted, 5000);
    }

    private void onOpponentAccepted() {
        if (binding == null) return;
        
        binding.txtStatus.setText("Đã chấp nhận! ✓");
        binding.txtStatus.setTextColor(getResources().getColor(R.color.success, null));
        binding.txtStatus.setBackgroundResource(R.drawable.bg_status_accepted);

        Toast.makeText(requireContext(), opponentName + " đã chấp nhận!", Toast.LENGTH_SHORT).show();

        handler.postDelayed(this::navigateToBattle, 1500);
    }

    private void navigateToBattle() {
        if (binding == null) return;
        
        Bundle args = new Bundle();
        args.putString("opponent_id", opponentId);
        args.putString("opponent_name", opponentName);
        args.putInt("opponent_level", opponentLevel);
        args.putBoolean("is_host", true);

        QuizBattleFragment fragment = new QuizBattleFragment();
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.childHomeHost, fragment)
                .commit();
    }

    private void cancelAndGoBack() {
        Toast.makeText(requireContext(), "Đã hủy lời mời", Toast.LENGTH_SHORT).show();
        requireActivity().onBackPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        binding = null;
    }
}
