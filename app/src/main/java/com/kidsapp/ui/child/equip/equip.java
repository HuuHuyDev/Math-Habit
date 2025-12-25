package com.kidsapp.ui.child.equip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kidsapp.R;
import com.kidsapp.databinding.FragmentEquipBinding;

/**
 * Fragment trang bị
 * Quản lý avatar, skin, vật phẩm của trẻ em
 */
public class equip extends Fragment {

    private FragmentEquipBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEquipBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        Toast.makeText(requireContext(), "Chào mừng đến trang Trang bị! 🎒", Toast.LENGTH_SHORT).show();
        
        setupClickListeners();
    }
    
    private void setupClickListeners() {
        // Setup back button
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        
        // Setup category clicks
        binding.layoutAvatars.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "👤 Avatar - Đang phát triển", Toast.LENGTH_SHORT).show();
        });
        
        binding.layoutSkins.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "🎨 Giao diện - Đang phát triển", Toast.LENGTH_SHORT).show();
        });
        
        binding.layoutItems.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "🎁 Vật phẩm - Đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}