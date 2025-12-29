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
import com.kidsapp.data.response.ChallengeInviteResponse;
import com.kidsapp.databinding.FragmentChallengeInvitesBinding;

import java.util.List;

/**
 * Fragment hiển thị danh sách lời mời thách đấu
 * Cho phép chấp nhận hoặc từ chối lời mời
 */
public class ChallengeInvitesFragment extends Fragment implements ChallengeInviteAdapter.OnInviteActionListener {

    private FragmentChallengeInvitesBinding binding;
    private ChallengeRepository repository;
    private ChallengeInviteAdapter inviteAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChallengeInvitesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        repository = new ChallengeRepository(requireContext());
        setupViews();
        loadInvites();
    }

    private void setupViews() {
        // Back button
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        
        // Title
        binding.txtTitle.setText("Lời mời thách đấu");
        
        // RecyclerView
        inviteAdapter = new ChallengeInviteAdapter(this);
        binding.rvInvites.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvInvites.setAdapter(inviteAdapter);
        
        // Refresh
        binding.swipeRefresh.setOnRefreshListener(this::loadInvites);
    }

    private void loadInvites() {
        showLoading(true);
        
        repository.getMyInvites(new ChallengeRepository.ResultCallback<List<ChallengeInviteResponse>>() {
            @Override
            public void onSuccess(List<ChallengeInviteResponse> invites) {
                showLoading(false);
                
                if (invites == null || invites.isEmpty()) {
                    binding.layoutEmpty.setVisibility(View.VISIBLE);
                    binding.rvInvites.setVisibility(View.GONE);
                } else {
                    binding.layoutEmpty.setVisibility(View.GONE);
                    binding.rvInvites.setVisibility(View.VISIBLE);
                    inviteAdapter.setInvites(invites);
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
    public void onAcceptInvite(ChallengeInviteResponse invite) {
        String inviteId = invite.getId();
        
        android.util.Log.d("ChallengeInvites", "=== onAcceptInvite START ===");
        android.util.Log.d("ChallengeInvites", "invite: " + invite);
        android.util.Log.d("ChallengeInvites", "inviteId: " + inviteId);
        android.util.Log.d("ChallengeInvites", "challengeId: " + invite.getChallengeId());
        android.util.Log.d("ChallengeInvites", "status: " + invite.getStatus());
        
        if (inviteId == null || inviteId.isEmpty()) {
            Toast.makeText(requireContext(), "Lỗi: ID lời mời không hợp lệ", Toast.LENGTH_SHORT).show();
            android.util.Log.e("ChallengeInvites", "InviteId is null or empty!");
            return;
        }
        
        repository.acceptInvite(inviteId, new ChallengeRepository.ResultCallback<com.kidsapp.data.response.ChallengeResponse>() {
            @Override
            public void onSuccess(com.kidsapp.data.response.ChallengeResponse challenge) {
                android.util.Log.d("ChallengeInvites", "=== acceptInvite SUCCESS ===");
                android.util.Log.d("ChallengeInvites", "challenge: " + challenge);
                android.util.Log.d("ChallengeInvites", "challengeId: " + challenge.getId());
                
                Toast.makeText(requireContext(), "Đã chấp nhận thách đấu!", Toast.LENGTH_SHORT).show();
                loadInvites(); // Refresh list
                
                // Navigate to battle screen directly
                navigateToBattleScreen(challenge.getId());
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("ChallengeInvites", "=== acceptInvite ERROR ===");
                android.util.Log.e("ChallengeInvites", "Error: " + error);
                Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeclineInvite(ChallengeInviteResponse invite) {
        repository.declineInvite(invite.getId(), new ChallengeRepository.ResultCallback<String>() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(requireContext(), "Đã từ chối lời mời", Toast.LENGTH_SHORT).show();
                loadInvites(); // Refresh list
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewInviteDetail(ChallengeInviteResponse invite) {
        // Navigate to invite detail fragment
        Bundle args = new Bundle();
        args.putString("invite_id", invite.getId());
        
        ChallengeInviteDetailFragment fragment = new ChallengeInviteDetailFragment();
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

    private void navigateToActiveChallenge(String challengeId) {
        // Navigate to active challenges list
        Bundle args = new Bundle();
        args.putString("challenge_id", challengeId);
        
        ActiveChallengesFragment fragment = new ActiveChallengesFragment();
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
    
    private void navigateToBattleScreen(String challengeId) {
        android.util.Log.d("ChallengeInvites", "=== navigateToBattleScreen ===");
        android.util.Log.d("ChallengeInvites", "challengeId: " + challengeId);
        
        // Navigate directly to QuizBattleFragment
        Bundle args = new Bundle();
        args.putString("challenge_id", challengeId);
        args.putString("challenge_title", "Thách đấu câu đố mẹo");
        args.putString("category_name", "Câu đố mẹo");
        args.putInt("total_questions", 10);
        args.putInt("time_limit", 5); // 5 minutes
        
        try {
            QuizBattleFragment fragment = new QuizBattleFragment();
            fragment.setArguments(args);
            
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.childHomeHost, fragment)
                    .addToBackStack(null)
                    .commit();
                    
            android.util.Log.d("ChallengeInvites", "Navigated to QuizBattleFragment");
        } catch (Exception e) {
            android.util.Log.e("ChallengeInvites", "Error navigating to battle screen", e);
            Toast.makeText(requireContext(), "Lỗi: Không thể vào thách đấu", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}