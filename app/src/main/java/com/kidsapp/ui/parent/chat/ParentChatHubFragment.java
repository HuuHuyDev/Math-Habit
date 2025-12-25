package com.kidsapp.ui.parent.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;
import com.kidsapp.databinding.FragmentParentChatHubBinding;

/**
 * Fragment chính cho Parent Chat Hub - chứa 2 tab: Con & Trợ lý AI
 */
public class ParentChatHubFragment extends Fragment {

    private FragmentParentChatHubBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentParentChatHubBinding.inflate(inflater, container, false);
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
                    tab.setText("👶 Các con");
                    break;
                case 1:
                    tab.setText("🤖 Trợ lý AI");
                    break;
            }
        }).attach();
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> {
            // Sử dụng Navigation để quay lại
            try {
                Navigation.findNavController(requireView()).popBackStack();
            } catch (Exception e) {
                requireActivity().onBackPressed();
            }
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
                    return new ParentChatListFragment();
                case 1:
                    return ParentChatWithAiFragment.newInstance();
                default:
                    return new ParentChatListFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
