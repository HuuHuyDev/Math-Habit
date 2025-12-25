package com.kidsapp.ui.child.chat;

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
import com.kidsapp.ui.child.challenge.InviteFriendAdapter;
import com.kidsapp.ui.child.challenge.MockData;

import java.util.List;

/**
 * Fragment tìm bạn mới để chat
 */
public class FindFriendFragment extends Fragment implements InviteFriendAdapter.OnInviteClickListener {

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
        // Adapter với custom listener cho chat
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
                adapter.setFriends(MockData.getFriends());
                updateEmptyState();
            }
        });
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
        // Mở chat room với bạn được chọn
        Bundle args = new Bundle();
        args.putString("chat_id", friend.getId());
        args.putString("chat_name", friend.getName());
        args.putInt("chat_type", Conversation.TYPE_FRIEND);

        ChatRoomFragment fragment = new ChatRoomFragment();
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
