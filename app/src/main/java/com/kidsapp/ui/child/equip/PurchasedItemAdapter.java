package com.kidsapp.ui.child.equip;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.kidsapp.R;
import com.kidsapp.data.model.PurchasedItem;

import java.util.List;

/**
 * Adapter cho danh sách vật phẩm đã mua (Trang Trang bị)
 */
public class PurchasedItemAdapter extends RecyclerView.Adapter<PurchasedItemAdapter.ViewHolder> {

    private final List<PurchasedItem> items;
    private final OnItemClickListener listener;
    private PurchasedItem selectedItem = null;

    public interface OnItemClickListener {
        void onItemClick(PurchasedItem item);
    }

    public PurchasedItemAdapter(List<PurchasedItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void setSelectedItem(PurchasedItem item) {
        this.selectedItem = item;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_purchased, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PurchasedItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final ImageView imgItem;
        private final TextView tvName;
        private final TextView tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardItem);
            imgItem = itemView.findViewById(R.id.imgItem);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        void bind(PurchasedItem item) {
            tvName.setText(item.getItemName());
            
            // Load image - hỗ trợ cả URL và drawable name
            String imageUrl = item.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                if (imageUrl.startsWith("http")) {
                    // Load từ URL
                    Glide.with(itemView.getContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_avatar_default)
                            .error(R.drawable.ic_avatar_default)
                            .into(imgItem);
                } else {
                    // Load từ drawable name (vd: ic_avatar_boy)
                    int resId = itemView.getContext().getResources().getIdentifier(
                            imageUrl, "drawable", itemView.getContext().getPackageName());
                    if (resId != 0) {
                        imgItem.setImageResource(resId);
                    } else {
                        imgItem.setImageResource(R.drawable.ic_avatar_default);
                    }
                }
            } else {
                imgItem.setImageResource(R.drawable.ic_avatar_default);
            }

            // Status
            if (item.isAvatar()) {
                if (item.isEquipped()) {
                    tvStatus.setText("Đang dùng");
                    tvStatus.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.primary));
                    tvStatus.setVisibility(View.VISIBLE);
                } else {
                    tvStatus.setVisibility(View.GONE);
                }
            } else {
                // Booster
                if (item.isActive()) {
                    Long remaining = item.getRemainingMinutes();
                    tvStatus.setText(remaining != null ? remaining + " phút" : "Đang hoạt động");
                    tvStatus.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.status_success));
                    tvStatus.setVisibility(View.VISIBLE);
                } else {
                    tvStatus.setVisibility(View.GONE);
                }
            }

            // Selection state
            boolean isSelected = selectedItem != null && selectedItem.getId().equals(item.getId());
            if (isSelected) {
                cardView.setStrokeColor(ContextCompat.getColor(itemView.getContext(), R.color.primary));
                cardView.setStrokeWidth(4);
                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.primary_light));
            } else {
                cardView.setStrokeColor(Color.TRANSPARENT);
                cardView.setStrokeWidth(0);
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
