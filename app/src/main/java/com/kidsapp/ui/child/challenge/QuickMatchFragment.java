package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kidsapp.R;
import com.kidsapp.databinding.FragmentQuickMatchBinding;

import java.util.Random;

/**
 * Fragment tìm đối thủ nhanh
 * Hiển thị loading và tips trong khi tìm kiếm
 */
public class QuickMatchFragment extends Fragment {

    private FragmentQuickMatchBinding binding;
    private Handler handler = new Handler();
    private String[] tips = {
        "Mẹo: Đọc kỹ câu hỏi trước khi chọn đáp án nhé!",
        "Mẹo: Trả lời nhanh để ghi nhiều điểm hơn!",
        "Mẹo: Giữ bình tĩnh và suy nghĩ kỹ!",
        "Mẹo: Loại trừ đáp án sai trước khi chọn!",
        "Mẹo: Đừng vội vàng, hãy đọc hết các đáp án!"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentQuickMatchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        showRandomTip();
        startMatchmaking();
        
        binding.btnCancel.setOnClickListener(v -> {
            handler.removeCallbacksAndMessages(null);
            requireActivity().onBackPressed();
        });
    }

    /**
     * Hiển thị tip ngẫu nhiên
     */
    private void showRandomTip() {
        Random random = new Random();
        String tip = tips[random.nextInt(tips.length)];
        binding.txtTip.setText(tip);
    }

    /**
     * Bắt đầu tìm đối thủ
     * Giả lập: sau 2-4 giây sẽ tìm thấy đối thủ
     */
    private void startMatchmaking() {
        Random random = new Random();
        int delay = 2000 + random.nextInt(2000); // 2-4 seconds
        
        handler.postDelayed(() -> {
            if (binding != null) {
                onMatchFound();
            }
        }, delay);
    }

    /**
     * Khi tìm thấy đối thủ
     */
    private void onMatchFound() {
        Toast.makeText(requireContext(), 
            "Đã tìm thấy đối thủ! Chuẩn bị nào 🎉", 
            Toast.LENGTH_SHORT).show();
        
        // Navigate to QuizBattleFragment after short delay
        handler.postDelayed(() -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.childHomeHost, new QuizBattleFragment())
                    .addToBackStack(null)
                    .commit();
            }
        }, 1500);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        binding = null;
    }
}
