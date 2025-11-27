package com.kidsapp.ui.child.equip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.kidsapp.R;
import com.kidsapp.data.model.Skin;
import com.kidsapp.databinding.FragmentEquipBinding;

import java.util.ArrayList;
import java.util.List;

public class equip extends Fragment {

    private FragmentEquipBinding binding;
    private SkinAdapter adapter;
    private final List<Skin> skinList = new ArrayList<>();
    private Skin selectedSkin = null; // Pet đang được chọn
    private int selectedPosition = -1;

    public equip() {
        // Required empty public constructor
    }

    public static equip newInstance() {
        return new equip();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentEquipBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // nút back
        binding.btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        setupSkinData();
        setupRecyclerView();
        setupEquipButton();
    }

    private void setupSkinData() {
        skinList.clear();
        skinList.add(new Skin(R.mipmap.skin4_foreground, "Mặc định"));
        skinList.add(new Skin(R.mipmap.skingau, "Gấu dễ thương"));
        skinList.add(new Skin(R.mipmap.skintho, "Thỏ ngọc"));
        skinList.add(new Skin(R.drawable.ic_pet_dog, "Chó tinh nghịch"));
        // thêm tiếp nếu muốn
    }

    private void setupRecyclerView() {
        adapter = new SkinAdapter(skinList, (skin, position) -> {
            // Khi chọn pet → lưu lại và cập nhật preview
            selectedSkin = skin;
            selectedPosition = position;
            
            // Cập nhật preview
            binding.imgCurrentAvatar.setImageResource(skin.getIconRes());
            binding.tvCurrentSkinName.setText(skin.getName());
            
            // Enable nút Trang bị
            binding.btnEquip.setEnabled(true);
            binding.btnEquip.setText("Trang bị " + skin.getName());
        });

        binding.rvSkins.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvSkins.setAdapter(adapter);
    }
    
    /**
     * Xử lý nút Trang bị
     */
    private void setupEquipButton() {
        // Ban đầu disable nút
        binding.btnEquip.setEnabled(false);

        binding.btnEquip.setText("Chọn pet để trang bị");
        
        binding.btnEquip.setOnClickListener(v -> {
            if (selectedSkin != null) {
                equipPet(selectedSkin);
            }
        });
    }
    
    /**
     * Trang bị pet
     */
    private void equipPet(Skin skin) {
        // Lưu pet đã trang bị vào SharedPreferences
        android.content.SharedPreferences prefs = 
            requireContext().getSharedPreferences("KidsAppPrefs", 0);
        android.content.SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("equipped_pet_icon", skin.getIconRes());
        editor.putString("equipped_pet_name", skin.getName());
        editor.apply();
        
        // Hiển thị thông báo
        android.widget.Toast.makeText(requireContext(),
                "✅ Đã trang bị " + skin.getName() + "!",
                android.widget.Toast.LENGTH_SHORT).show();
        
        // Disable nút sau khi trang bị
        binding.btnEquip.setEnabled(false);
        binding.btnEquip.setText("Đã trang bị");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
