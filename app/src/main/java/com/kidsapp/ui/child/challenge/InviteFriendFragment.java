package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.R;
import com.kidsapp.data.model.Child;
import com.kidsapp.data.repository.ChallengeRepository;
import com.kidsapp.databinding.FragmentInviteFriendBinding;

import java.util.List;

/**
 * Fragment hiển thị danh sách bạn bè để mời thách đấu
 */
public class InviteFriendFragment extends Fragment implements InviteFriendAdapter.OnInviteClickListener {

    private FragmentInviteFriendBinding binding;
    private InviteFriendAdapter adapter;
    private ChallengeRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInviteFriendBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        repository = new ChallengeRepository(requireContext());
        setupViews();
        loadFriends();
    }

    private void setupViews() {
        // RecyclerView
        adapter = new InviteFriendAdapter(this);
        binding.rvFriends.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFriends.setAdapter(adapter);

        // Search
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
                updateEmptyState();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Back
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void loadFriends() {
        showLoading(true);
        
        repository.getFriendsList(new ChallengeRepository.ResultCallback<List<Child>>() {
            @Override
            public void onSuccess(List<Child> friends) {
                showLoading(false);
                adapter.setFriends(friends);
                updateEmptyState();
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                loadMockData();
            }
        });
    }

    private void loadMockData() {
        adapter.setFriends(MockData.getFriends());
        updateEmptyState();
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.rvFriends.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateEmptyState() {
        boolean isEmpty = adapter.isEmpty();
        binding.layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.rvFriends.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onInviteClick(Child friend) {
        adapter.markAsInvited(friend.getId());
        Toast.makeText(requireContext(), "Đã gửi lời mời đến " + friend.getName(), Toast.LENGTH_SHORT).show();

        // Chuyển sang WaitingRoom sau 1s
        binding.getRoot().postDelayed(() -> navigateToWaitingRoom(friend), 1000);
    }

    private void navigateToWaitingRoom(Child opponent) {
        Bundle args = new Bundle();
        args.putString("opponent_id", opponent.getId());
        args.putString("opponent_name", opponent.getName());
        args.putInt("opponent_level", opponent.getLevel());

        WaitingRoomFragment fragment = new WaitingRoomFragment();
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
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
