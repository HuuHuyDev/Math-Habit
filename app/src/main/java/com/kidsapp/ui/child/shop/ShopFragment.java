package com.kidsapp.ui.child.shop;

import android.content.SharedPreferences;
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

import com.kidsapp.R;
import com.kidsapp.data.model.ShopItem;
import com.kidsapp.databinding.FragmentShopBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Shop Fragment - Mua vật phẩm (Skin)
 */
public class ShopFragment extends Fragment {
    private FragmentShopBinding binding;
    private List<ShopItem> shopItems = new ArrayList<>();
    private ShopItem selectedItem = null;
    private int currentCoins = 1250;
    private SharedPreferences sharedPreferences;

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

        sharedPreferences = requireContext().getSharedPreferences("KidsAppPrefs", 0);
        loadCoins();
        initShopItems();
        updateCoinsDisplay();
        setupBackButton();
        setupBuyButton();
        setupShopGrid();
    }

    private void loadCoins() {
        currentCoins = sharedPreferences.getInt("child_coins", 1250);
    }

    private void saveCoins() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("child_coins", currentCoins);
        editor.apply();
    }

    private void initShopItems() {
        shopItems.clear();
        shopItems.add(new ShopItem(1, "Mèo\nTinh nghịch", 500, R.drawable.ic_pet_cat));
        shopItems.add(new ShopItem(2, "Thỏ\nNhanh nhẹn", 600, R.drawable.ic_pet_rabbit));
        shopItems.add(new ShopItem(3, "Gấu trúc\nĐáng yêu", 750, R.drawable.ic_pet_panda));
        shopItems.add(new ShopItem(4, "Chó\nTrung thành", 900, R.drawable.ic_pet_dog));
    }

    private void updateCoinsDisplay() {
        binding.tvCoinAmount.setText(String.format("%,d", currentCoins));
    }

    private void setupBackButton() {
        binding.btnBackShop.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
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

    private void setupShopGrid() {
        GridLayout gridLayout = binding.gridSkins;
        gridLayout.removeAllViews();

        for (ShopItem item : shopItems) {
            View itemView = createShopItemView(item);
            gridLayout.addView(itemView);
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
            container.setBackgroundResource(R.drawable.bg_round_20);
            container.setBackgroundResource(R.drawable.bg_action_blue);
        }

        ImageView imageView = new ImageView(requireContext());
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                (int) (150 * getResources().getDisplayMetrics().density),
                (int) (150 * getResources().getDisplayMetrics().density)
        );
        imageView.setLayoutParams(imgParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(item.getImageRes());
        container.addView(imageView);

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
                (int) getResources().getDimension(R.dimen.icon_size_small),
                (int) getResources().getDimension(R.dimen.icon_size_small)
        );
        coinIcon.setLayoutParams(coinParams);
        priceLayout.addView(coinIcon);

        TextView priceView = new TextView(requireContext());
        priceView.setText(String.valueOf(item.getPrice()));
        priceView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
        priceView.setTextSize(12);
        LinearLayout.LayoutParams priceParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        priceParams.leftMargin = (int) getResources().getDimension(R.dimen.spacing_4);
        priceView.setLayoutParams(priceParams);
        priceLayout.addView(priceView);

        container.addView(priceLayout);

        if (item.isPurchased()) {
            TextView ownedBadge = new TextView(requireContext());
            ownedBadge.setText("✓ Đã mua");
            ownedBadge.setTextColor(Color.WHITE);
            ownedBadge.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.status_success));
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
            setupShopGrid();
        });

        return container;
    }

    private void selectItem(ShopItem item) {
        for (ShopItem shopItem : shopItems) {
            shopItem.setSelected(false);
        }

        item.setSelected(true);
        selectedItem = item;

        if (item.isPurchased()) {
            binding.btnBuySkin.setText("Đã sở hữu");
            binding.btnBuySkin.setEnabled(false);
        } else {
            binding.btnBuySkin.setText("Mua - " + item.getPrice() + " Coins");
            binding.btnBuySkin.setEnabled(true);
        }
    }

    private void showPurchaseConfirmDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận mua")
                .setMessage("Bạn có chắc muốn mua \"" + selectedItem.getName().replace("\n", " ") +
                        "\" với giá " + selectedItem.getPrice() + " Coins?")
                .setPositiveButton("Mua", (dialog, which) -> purchaseItem())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void purchaseItem() {
        currentCoins -= selectedItem.getPrice();
        selectedItem.setPurchased(true);
        saveCoins();
        updateCoinsDisplay();
        setupShopGrid();

        Toast.makeText(requireContext(),
                "🎉 Mua thành công! Bạn đã có thú cưng " + selectedItem.getName().replace("\n", " "),
                Toast.LENGTH_LONG).show();
    }

    private void showInsufficientCoinsDialog() {
        int needed = selectedItem.getPrice() - currentCoins;
        new AlertDialog.Builder(requireContext())
                .setTitle("Không đủ Coins")
                .setMessage("Bạn cần thêm " + needed + " Coins để mua vật phẩm này.\n\n" +
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
