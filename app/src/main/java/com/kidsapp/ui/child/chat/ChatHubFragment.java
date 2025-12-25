package com.kidsapp.ui.child.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;
import com.kidsapp.R;
import com.kidsapp.databinding.FragmentChatHubBinding;

/**
 * Fragment chính cho Chat Hub - chứa 2 tab: Phụ huynh và Bạn bè
 */
public class ChatHubFragment extends Fragment {

    private FragmentChatHubBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatHubBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupViewPager();
        setupClickListeners();
    }

    private void setupViewPager() {
        ChatPagerAdapter adapter = new ChatPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        // Kết nối TabLayout với ViewPager2
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("👨‍👩‍👧 Phụ huynh");
            } else {
                tab.setText("👫 Bạn bè");
            }
        }).attach();
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnFindFriend.setOnClickListener(v -> {
            // Mở màn hình tìm bạn mới
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.childHomeHost, new FindFriendFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Adapter cho ViewPager2
     */
    private static class ChatPagerAdapter extends FragmentStateAdapter {

        public ChatPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return ChatListFragment.newInstance(ChatListFragment.TYPE_PARENT);
            } else {
                return ChatListFragment.newInstance(ChatListFragment.TYPE_FRIENDS);
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
