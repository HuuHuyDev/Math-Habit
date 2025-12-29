package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.R;
import com.kidsapp.data.repository.ChallengeRepository;
import com.kidsapp.data.response.ChallengeResponse;
import com.kidsapp.databinding.FragmentActiveChallengesBinding;

import java.util.List;

/**
 * Fragment hiển thị danh sách challenge đang active
 * Cho phép tham gia challenge đã được chấp nhận
 */
public class ActiveChallengesFragment extends Fragment implements ActiveChallengeAdapter.OnChallengeActionListener {

    private FragmentActiveChallengesBinding binding;
    private ChallengeRepository repository;
    private ActiveChallengeAdapter challengeAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentActiveChallengesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        repository = new ChallengeRepository(requireContext());
        setupViews();
        loadActiveChallenges();
    }

    private void setupViews() {
        // Back button
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        
        // Title
        binding.txtTitle.setText("Thách đấu đang diễn ra");
        
        // RecyclerView
        challengeAdapter = new ActiveChallengeAdapter(this);
        binding.rvChallenges.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChallenges.setAdapter(challengeAdapter);
        
        // Refresh
        binding.swipeRefresh.setOnRefreshListener(this::loadActiveChallenges);
    }

    private void loadActiveChallenges() {
        showLoading(true);
        
        repository.getActiveChallenges(new ChallengeRepository.ResultCallback<List<ChallengeResponse>>() {
            @Override
            public void onSuccess(List<ChallengeResponse> challenges) {
                showLoading(false);
                
                if (challenges == null || challenges.isEmpty()) {
                    binding.layoutEmpty.setVisibility(View.VISIBLE);
                    binding.rvChallenges.setVisibility(View.GONE);
                } else {
                    binding.layoutEmpty.setVisibility(View.GONE);
                    binding.rvChallenges.setVisibility(View.VISIBLE);
                    challengeAdapter.setChallenges(challenges);
                }
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        binding.swipeRefresh.setRefreshing(show);
        if (show) {
            binding.layoutEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onJoinChallenge(ChallengeResponse challenge) {
        repository.joinActiveChallenge(challenge.getId(), new ChallengeRepository.ResultCallback<ChallengeResponse>() {
            @Override
            public void onSuccess(ChallengeResponse updatedChallenge) {
                Toast.makeText(requireContext(), "Đã tham gia thách đấu!", Toast.LENGTH_SHORT).show();
                
                // Navigate to battle screen
                navigateToBattle(updatedChallenge);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewChallengeDetail(ChallengeResponse challenge) {
        // Navigate to challenge detail
        Bundle args = new Bundle();
        args.putString("challenge_id", challenge.getId());
        
        ChallengeDetailFragment fragment = new ChallengeDetailFragment();
        fragment.setArguments(args);
        
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.childHomeHost, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToBattle(ChallengeResponse challenge) {
        // Navigate to QuizBattleFragment with challenge data
        Bundle args = new Bundle();
        args.putString("challenge_id", challenge.getId());
        args.putString("challenge_title", challenge.getTitle());
        args.putString("category_name", challenge.getCategoryName());
        args.putInt("total_questions", challenge.getTotalQuestions());
        args.putInt("time_limit", challenge.getTimeLimitMinutes());
        
        QuizBattleFragment fragment = new QuizBattleFragment();
        fragment.setArguments(args);
        
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.childHomeHost, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}