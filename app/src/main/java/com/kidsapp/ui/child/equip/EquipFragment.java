package com.kidsapp.ui.child.equip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.kidsapp.R;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.PurchasedItem;
import com.kidsapp.data.repository.ShopRepository;
import com.kidsapp.databinding.FragmentEquipBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Trang Trang bị - Hiển thị vật phẩm đã mua, cho phép equip/kích hoạt
 */
public class EquipFragment extends Fragment {

    private FragmentEquipBinding binding;
    private ShopRepository shopRepository;
    private SharedPref sharedPref;
    
    private List<PurchasedItem> myAvatars = new ArrayList<>();
    private List<PurchasedItem> myBoosters = new ArrayList<>();
    private PurchasedItemAdapter avatarAdapter;
    private PurchasedItemAdapter boosterAdapter;
    private PurchasedItem selectedItem = null;
    private String childId;

    public static EquipFragment newInstance() {
        return new EquipFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEquipBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shopRepository = new ShopRepository(requireContext());
        sharedPref = new SharedPref(requireContext());
        
        childId = sharedPref.getChildId();
        if (childId == null || childId.isEmpty()) {
            childId = sharedPref.getUserId();
        }

        setupBackButton();
        setupRecyclerViews();
        setupActionButton();
        loadMyItems();
        // loadCurrentAvatar() sẽ được gọi sau khi load xong myAvatars
    }

    private void setupBackButton() {
        binding.btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
    }

    private void setupRecyclerViews() {
        // Avatar adapter
        avatarAdapter = new PurchasedItemAdapter(myAvatars, item -> {
            selectedItem = item;
            updateSelection();
            updateActionButton();
        });
        binding.rvAvatars.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.rvAvatars.setAdapter(avatarAdapter);

        // Booster adapter
        boosterAdapter = new PurchasedItemAdapter(myBoosters, item -> {
            selectedItem = item;
            updateSelection();
            updateActionButton();
        });
        binding.rvBoosters.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.rvBoosters.setAdapter(boosterAdapter);
    }

    private void setupActionButton() {
        binding.btnAction.setOnClickListener(v -> {
            if (selectedItem == null) {
                Toast.makeText(requireContext(), "Vui lòng chọn vật phẩm!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedItem.isAvatar()) {
                if (selectedItem.isEquipped()) {
                    Toast.makeText(requireContext(), "Avatar này đang được sử dụng!", Toast.LENGTH_SHORT).show();
                    return;
                }
                equipAvatar();
            } else {
                if (selectedItem.isActive()) {
                    Toast.makeText(requireContext(), "Booster này đang hoạt động!", Toast.LENGTH_SHORT).show();
                    return;
                }
                activateBooster();
            }
        });
        
        // Disable button initially
        binding.btnAction.setEnabled(false);
        binding.btnAction.setText("Chọn vật phẩm");
    }

    private void loadMyItems() {
        // Load avatars đã mua
        shopRepository.getMyItems(childId, "AVATAR").observe(getViewLifecycleOwner(), items -> {
            myAvatars.clear();
            if (items != null && !items.isEmpty()) {
                myAvatars.addAll(items);
                binding.tvEmptyAvatars.setVisibility(View.GONE);
                binding.rvAvatars.setVisibility(View.VISIBLE);
            } else {
                binding.tvEmptyAvatars.setVisibility(View.VISIBLE);
                binding.rvAvatars.setVisibility(View.GONE);
            }
            avatarAdapter.notifyDataSetChanged();
            // Load current avatar sau khi có danh sách
            loadCurrentAvatar();
        });

        // Load boosters đã mua
        shopRepository.getMyItems(childId, "BOOSTER").observe(getViewLifecycleOwner(), items -> {
            myBoosters.clear();
            if (items != null && !items.isEmpty()) {
                myBoosters.addAll(items);
                binding.tvEmptyBoosters.setVisibility(View.GONE);
                binding.rvBoosters.setVisibility(View.VISIBLE);
            } else {
                binding.tvEmptyBoosters.setVisibility(View.VISIBLE);
                binding.rvBoosters.setVisibility(View.GONE);
            }
            boosterAdapter.notifyDataSetChanged();
        });
    }

    private void loadCurrentAvatar() {
        // Tìm avatar đang equipped từ danh sách myAvatars
        PurchasedItem equippedAvatar = null;
        for (PurchasedItem item : myAvatars) {
            if (item.isEquipped()) {
                equippedAvatar = item;
                break;
            }
        }
        
        if (equippedAvatar != null) {
            binding.tvCurrentSkinName.setText(equippedAvatar.getItemName());
            loadImageToView(equippedAvatar.getImageUrl(), binding.imgCurrentAvatar);
        } else {
            // Fallback to SharedPref
            String avatarUrl = sharedPref.getString("current_avatar_url", null);
            String avatarName = sharedPref.getString("current_avatar_name", "Mặc định");
            
            binding.tvCurrentSkinName.setText(avatarName);
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                loadImageToView(avatarUrl, binding.imgCurrentAvatar);
            } else {
                binding.imgCurrentAvatar.setImageResource(R.drawable.ic_avatar_default);
            }
        }
    }
    
    /**
     * Load image - hỗ trợ cả URL và drawable name
     */
    private void loadImageToView(String imageUrl, android.widget.ImageView imageView) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageView.setImageResource(R.drawable.ic_avatar_default);
            return;
        }
        
        if (imageUrl.startsWith("http")) {
            // Load từ URL
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_avatar_default)
                    .error(R.drawable.ic_avatar_default)
                    .into(imageView);
        } else {
            // Load từ drawable name (vd: ic_avatar_boy)
            int resId = getResources().getIdentifier(imageUrl, "drawable", requireContext().getPackageName());
            if (resId != 0) {
                imageView.setImageResource(resId);
            } else {
                imageView.setImageResource(R.drawable.ic_avatar_default);
            }
        }
    }

    private void updateSelection() {
        // Clear all selections
        for (PurchasedItem item : myAvatars) {
            // Use a transient field for selection state
        }
        for (PurchasedItem item : myBoosters) {
            // Use a transient field for selection state
        }
        
        avatarAdapter.setSelectedItem(selectedItem);
        boosterAdapter.setSelectedItem(selectedItem);
        
        // Update preview if avatar selected
        if (selectedItem != null && selectedItem.isAvatar()) {
            binding.tvCurrentSkinName.setText(selectedItem.getItemName());
            loadImageToView(selectedItem.getImageUrl(), binding.imgCurrentAvatar);
        }
    }

    private void updateActionButton() {
        if (selectedItem == null) {
            binding.btnAction.setEnabled(false);
            binding.btnAction.setText("Chọn vật phẩm");
            return;
        }

        binding.btnAction.setEnabled(true);
        
        if (selectedItem.isAvatar()) {
            if (selectedItem.isEquipped()) {
                binding.btnAction.setText("Đang sử dụng");
                binding.btnAction.setEnabled(false);
            } else {
                binding.btnAction.setText("Trang bị Avatar");
            }
        } else {
            if (selectedItem.isActive()) {
                binding.btnAction.setText("Đang hoạt động - " + selectedItem.getRemainingMinutes() + " phút");
                binding.btnAction.setEnabled(false);
            } else {
                binding.btnAction.setText("Kích hoạt Booster");
            }
        }
    }

    private void equipAvatar() {
        shopRepository.equipAvatar(childId, selectedItem.getId(), new ShopRepository.ActionCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(requireContext(), "🎭 Đã trang bị avatar!", Toast.LENGTH_SHORT).show();
                
                // Save to SharedPref
                sharedPref.saveString("current_avatar_url", selectedItem.getImageUrl());
                sharedPref.saveString("current_avatar_name", selectedItem.getItemName());
                
                // Reload data
                loadMyItems();
                loadCurrentAvatar();
                selectedItem = null;
                updateActionButton();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void activateBooster() {
        shopRepository.activateBooster(childId, selectedItem.getId(), new ShopRepository.ActionCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(requireContext(), "⚡ Đã kích hoạt booster!", Toast.LENGTH_SHORT).show();
                
                // Reload data
                loadMyItems();
                selectedItem = null;
                updateActionButton();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
