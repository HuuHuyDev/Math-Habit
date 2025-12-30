package com.kidsapp.ui.child.shop;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.PurchasedItem;
import com.kidsapp.data.model.ShopItem;
import com.kidsapp.data.repository.ShopRepository;
import com.kidsapp.databinding.FragmentShopBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Shop Fragment - Mua vật phẩm (Avatar & Booster)
 */
public class ShopFragment extends Fragment {
    private FragmentShopBinding binding;
    private ShopRepository shopRepository;
    private SharedPref sharedPref;
    private ApiService apiService;

    private List<ShopItem> avatarItems = new ArrayList<>();
    private List<ShopItem> boosterItems = new ArrayList<>();
    private ShopItem selectedItem = null;
    private String childId;
    private int currentCoins = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentShopBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPref = new SharedPref(requireContext());
        shopRepository = new ShopRepository(requireContext());
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();

        // Lấy childId
        childId = sharedPref.getChildId();
        if (childId == null || childId.isEmpty()) {
            childId = sharedPref.getUserId();
        }

        setupBackButton();
        setupBuyButton();
        loadChildInfo();
        loadShopItems();
    }

    private void loadChildInfo() {
        // Gọi API để lấy thông tin child (coins, xp)
        apiService.getMyProfile().enqueue(new Callback<ApiService.ApiResponseWrapper<com.kidsapp.data.model.Child>>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<com.kidsapp.data.model.Child>> call,
                                   @NonNull Response<ApiService.ApiResponseWrapper<com.kidsapp.data.model.Child>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    com.kidsapp.data.model.Child child = response.body().data;
                    currentCoins = child.getCoins();
                    int xp = child.getTotalXp();

                    updateCoinsDisplay();
                    binding.tvXpAmount.setText(String.format("%,d", xp));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<com.kidsapp.data.model.Child>> call,
                                  @NonNull Throwable t) {
                // Fallback to 0
                updateCoinsDisplay();
            }
        });
    }

    private void updateCoinsDisplay() {
        binding.tvCoinAmount.setText(String.format("%,d", currentCoins));
    }

    private void setupBackButton() {
        binding.btnBackShop.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
    }

    private void setupBuyButton() {
        binding.btnBuySkin.setOnClickListener(v -> {
            if (selectedItem == null) {
                Toast.makeText(requireContext(), "Vui lòng chọn vật phẩm!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedItem.isPurchased()) {
                Toast.makeText(requireContext(), "Bạn đã sở hữu vật phẩm này!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentCoins < selectedItem.getPrice()) {
                showInsufficientCoinsDialog();
                return;
            }

            showPurchaseConfirmDialog();
        });
    }

    private void loadShopItems() {
        // Load avatars
        shopRepository.getShopItems(childId, "AVATAR").observe(getViewLifecycleOwner(), items -> {
            avatarItems.clear();
            if (items != null && !items.isEmpty()) {
                avatarItems.addAll(items);
                binding.tvEmptyAvatars.setVisibility(View.GONE);
                binding.gridSkins.setVisibility(View.VISIBLE);
            } else {
                binding.tvEmptyAvatars.setVisibility(View.VISIBLE);
                binding.gridSkins.setVisibility(View.GONE);
            }
            setupAvatarGrid();
        });

        // Load boosters
        shopRepository.getShopItems(childId, "BOOSTER").observe(getViewLifecycleOwner(), items -> {
            boosterItems.clear();
            if (items != null && !items.isEmpty()) {
                boosterItems.addAll(items);
                binding.tvEmptyBoosters.setVisibility(View.GONE);
                binding.gridBoosters.setVisibility(View.VISIBLE);
            } else {
                binding.tvEmptyBoosters.setVisibility(View.VISIBLE);
                binding.gridBoosters.setVisibility(View.GONE);
            }
            setupBoosterGrid();
        });
    }

    private void setupAvatarGrid() {
        binding.gridSkins.removeAllViews();
        for (ShopItem item : avatarItems) {
            View itemView = createShopItemView(item);
            binding.gridSkins.addView(itemView);
        }
    }

    private void setupBoosterGrid() {
        binding.gridBoosters.removeAllViews();
        for (ShopItem item : boosterItems) {
            View itemView = createShopItemView(item);
            binding.gridBoosters.addView(itemView);
        }
    }

    private View createShopItemView(ShopItem item) {
        LinearLayout container = new LinearLayout(requireContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        container.setLayoutParams(params);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
        int padding = (int) getResources().getDimension(R.dimen.spacing_10);
        container.setPadding(padding, padding, padding, padding);

        if (item.isSelected()) {
            container.setBackgroundResource(R.drawable.bg_action_blue);
        }

        // Image
        ImageView imageView = new ImageView(requireContext());
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                (int) (80 * getResources().getDisplayMetrics().density),
                (int) (80 * getResources().getDisplayMetrics().density)
        );
        imageView.setLayoutParams(imgParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        // Load image - hỗ trợ cả URL và drawable name
        String imageUrl = item.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
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
        } else {
            imageView.setImageResource(R.drawable.ic_avatar_default);
        }
        container.addView(imageView);

        // Name
        TextView nameView = new TextView(requireContext());
        nameView.setText(item.getName());
        nameView.setGravity(android.view.Gravity.CENTER);
        nameView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
        nameView.setTextSize(12);
        nameView.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        nameParams.topMargin = (int) getResources().getDimension(R.dimen.spacing_6);
        nameView.setLayoutParams(nameParams);
        container.addView(nameView);

        // Price layout
        LinearLayout priceLayout = new LinearLayout(requireContext());
        priceLayout.setOrientation(LinearLayout.HORIZONTAL);
        priceLayout.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams priceLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        priceLayoutParams.topMargin = (int) getResources().getDimension(R.dimen.spacing_4);
        priceLayout.setLayoutParams(priceLayoutParams);

        ImageView coinIcon = new ImageView(requireContext());
        coinIcon.setImageResource(R.mipmap.coin_foreground);
        LinearLayout.LayoutParams coinParams = new LinearLayout.LayoutParams(
                (int) (18 * getResources().getDisplayMetrics().density),
                (int) (18 * getResources().getDisplayMetrics().density)
        );
        coinIcon.setLayoutParams(coinParams);
        priceLayout.addView(coinIcon);

        TextView priceView = new TextView(requireContext());
        priceView.setText(String.valueOf(item.getPrice()));
        priceView.setTextColor(ContextCompat.getColor(requireContext(), R.color.coin_orange));
        priceView.setTextSize(12);
        priceView.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams priceParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        priceParams.leftMargin = (int) getResources().getDimension(R.dimen.spacing_4);
        priceView.setLayoutParams(priceParams);
        priceLayout.addView(priceView);

        container.addView(priceLayout);

        // Purchased badge
        if (item.isPurchased()) {
            TextView ownedBadge = new TextView(requireContext());
            ownedBadge.setText("✓ Đã mua");
            ownedBadge.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.status_success));
            ownedBadge.setTextColor(Color.WHITE);
            ownedBadge.setPadding(12, 4, 12, 4);
            ownedBadge.setTextSize(10);
            LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            badgeParams.topMargin = (int) getResources().getDimension(R.dimen.spacing_4);
            ownedBadge.setLayoutParams(badgeParams);
            container.addView(ownedBadge);
        }

        container.setOnClickListener(v -> {
            selectItem(item);
            setupAvatarGrid();
            setupBoosterGrid();
        });

        return container;
    }

    private void selectItem(ShopItem item) {
        // Deselect all
        for (ShopItem shopItem : avatarItems) {
            shopItem.setSelected(false);
        }
        for (ShopItem shopItem : boosterItems) {
            shopItem.setSelected(false);
        }

        item.setSelected(true);
        selectedItem = item;

        if (item.isPurchased()) {
            binding.btnBuySkin.setText("Đã sở hữu");
            binding.btnBuySkin.setEnabled(false);
        } else {
            binding.btnBuySkin.setText("Mua - " + item.getPrice() + " 🪙");
            binding.btnBuySkin.setEnabled(true);
        }
    }

    private void showPurchaseConfirmDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận mua")
                .setMessage("Bạn có chắc muốn mua \"" + selectedItem.getName() +
                        "\" với giá " + selectedItem.getPrice() + " 🪙?")
                .setPositiveButton("Mua", (dialog, which) -> purchaseItem())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void purchaseItem() {
        shopRepository.purchaseItem(childId, selectedItem.getId(), new ShopRepository.PurchaseCallback() {
            @Override
            public void onSuccess(PurchasedItem item, String message) {
                currentCoins -= selectedItem.getPrice();
                selectedItem.setPurchased(true);

                updateCoinsDisplay();
                setupAvatarGrid();
                setupBoosterGrid();

                String itemType = selectedItem.isAvatar() ? "avatar" : "booster";
                Toast.makeText(requireContext(),
                        "🎉 Mua thành công! Bạn đã có " + itemType + " " + selectedItem.getName(),
                        Toast.LENGTH_LONG).show();

                // Reset selection
                selectedItem = null;
                binding.btnBuySkin.setText("Chọn vật phẩm để mua");
                binding.btnBuySkin.setEnabled(true);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showInsufficientCoinsDialog() {
        int needed = selectedItem.getPrice() - currentCoins;
        new AlertDialog.Builder(requireContext())
                .setTitle("Không đủ Coins")
                .setMessage("Bạn cần thêm " + needed + " 🪙 để mua vật phẩm này.\n\n" +
                        "Hãy hoàn thành nhiệm vụ để kiếm thêm Coins nhé!")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
