package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.data.model.ChallengeResult;
import com.kidsapp.databinding.FragmentChallengeHistoryBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment lịch sử thách đấu
 * Hiển thị danh sách các trận đấu đã chơi
 */
public class ChallengeHistoryFragment extends Fragment {

    private FragmentChallengeHistoryBinding binding;
    private ChallengeHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChallengeHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        binding.btnBack.setOnClickListener(v -> 
            requireActivity().onBackPressed());

        setupRecyclerView();
        loadHistory();

        binding.swipeRefresh.setOnRefreshListener(() -> {
            loadHistory();
            binding.swipeRefresh.setRefreshing(false);
        });
    }

    private void setupRecyclerView() {
        adapter = new ChallengeHistoryAdapter();
        binding.recyclerHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerHistory.setAdapter(adapter);
    }

    private void loadHistory() {
        // TODO: Load from repository
        List<ChallengeResult> results = generateSampleData();
        adapter.submitList(results);
    }

    private List<ChallengeResult> generateSampleData() {
        List<ChallengeResult> results = new ArrayList<>();
        
        // Sample data
        results.add(new ChallengeResult(
            "1", "Bạn", "Nguyễn Văn A", 850, 720, 8, 2, 272000, true, "24/12/2025"
        ));
        results.add(new ChallengeResult(
            "2", "Bạn", "Trần Thị B", 680, 820, 6, 4, 315000, false, "23/12/2025"
        ));
        results.add(new ChallengeResult(
            "3", "Bạn", "Lê Văn C", 920, 650, 9, 1, 245000, true, "22/12/2025"
        ));
        results.add(new ChallengeResult(
            "4", "Bạn", "Phạm Thị D", 750, 780, 7, 3, 298000, false, "21/12/2025"
        ));
        results.add(new ChallengeResult(
            "5", "Bạn", "Hoàng Văn E", 880, 620, 8, 2, 265000, true, "20/12/2025"
        ));

        return results;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
