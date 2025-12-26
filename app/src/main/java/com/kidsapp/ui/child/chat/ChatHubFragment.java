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
 * Fragment chính cho Chat Hub - chứa 3 tab: Phụ huynh, Bạn bè, AI
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
            switch (position) {
                case 0:
                    tab.setText("👨‍👩‍👧 Phụ huynh");
                    break;
                case 1:
                    tab.setText("👶 Anh chị em");
                    break;
                case 2:
                    tab.setText("🤖 Trợ lý AI");
                    break;
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
            switch (position) {
                case 0:
                    return ChatListFragment.newInstance(ChatListFragment.TYPE_PARENT);
                case 1:
                    return ChatListFragment.newInstance(ChatListFragment.TYPE_SIBLINGS);
                case 2:
                    return ChatWithAiFragment.newInstance("CHILD");
                default:
                    return ChatListFragment.newInstance(ChatListFragment.TYPE_PARENT);
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
