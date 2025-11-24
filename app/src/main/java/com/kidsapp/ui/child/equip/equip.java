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
    }

    private void setupSkinData() {
        skinList.clear();
        skinList.add(new Skin(R.mipmap.avatar_foreground, "Mặc định"));
        skinList.add(new Skin(R.mipmap.skin1_foreground, "Nhà thám hiểm"));
        skinList.add(new Skin(R.mipmap.skin2_foreground, "Cướp biển"));
        skinList.add(new Skin(R.mipmap.skin3_foreground, "Bóng chày"));
        // thêm tiếp nếu muốn
    }

    private void setupRecyclerView() {
        adapter = new SkinAdapter(skinList, skin -> {
            // khi chọn skin → cập nhật preview
            binding.imgCurrentAvatar.setImageResource(skin.getIconRes());
            binding.tvCurrentSkinName.setText(skin.getName());
        });

        binding.rvSkins.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvSkins.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
