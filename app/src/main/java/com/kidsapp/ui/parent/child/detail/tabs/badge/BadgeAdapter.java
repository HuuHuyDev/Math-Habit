package com.kidsapp.ui.parent.child.detail.tabs.badge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kidsapp.R;

import java.util.List;

/**
 * Adapter cho RecyclerView hiển thị danh sách huy hiệu (Grid 3 cột)
 */
public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder> {

    private List<BadgeItem> badgeList;

    public BadgeAdapter(List<BadgeItem> badgeList) {
        this.badgeList = badgeList;
    }

    @NonNull
    @Override
    public BadgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_badge, parent, false);
        return new BadgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BadgeViewHolder holder, int position) {
        BadgeItem badge = badgeList.get(position);
        
        holder.txtName.setText(badge.getName());
        
        // Load icon từ URL hoặc dùng default
        if (badge.getIconUrl() != null && !badge.getIconUrl().isEmpty()) {
            holder.imgIcon.setVisibility(View.VISIBLE);
            holder.txtEmoji.setVisibility(View.GONE);
            Glide.with(holder.itemView.getContext())
                    .load(badge.getIconUrl())
                    .placeholder(R.drawable.ic_trophy)
                    .error(R.drawable.ic_trophy)
                    .into(holder.imgIcon);
        } else {
            // Dùng emoji dựa trên rarity
            holder.imgIcon.setVisibility(View.GONE);
            holder.txtEmoji.setVisibility(View.VISIBLE);
            holder.txtEmoji.setText(getEmojiForRarity(badge.getRarity()));
        }
        
        if (holder.txtCount != null) {
            holder.txtCount.setVisibility(View.GONE);
        }
        
        // Đổi background và alpha dựa trên trạng thái unlock
        if (badge.isUnlocked()) {
            holder.iconBackground.setBackgroundResource(R.drawable.bg_badge_unlocked);
            holder.imgIcon.setAlpha(1.0f);
            holder.txtEmoji.setAlpha(1.0f);
            holder.txtName.setAlpha(1.0f);
            holder.txtName.setTextColor(0xFF2D3748);
            
            // Ẩn progress khi đã unlock
            holder.progressBar.setVisibility(View.GONE);
            holder.txtProgress.setVisibility(View.GONE);
        } else {
            holder.iconBackground.setBackgroundResource(R.drawable.bg_badge_locked);
            holder.imgIcon.setAlpha(0.3f);
            holder.txtEmoji.setAlpha(0.3f);
            holder.txtName.setAlpha(0.4f);
            holder.txtName.setTextColor(0xFFBDBDBD);
            
            // Hiện progress khi chưa unlock
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.txtProgress.setVisibility(View.VISIBLE);
            holder.progressBar.setProgress(badge.getProgressPercent());
            holder.txtProgress.setText(badge.getProgressText());
        }
    }
    
    private String getEmojiForRarity(String rarity) {
        if (rarity == null) return "🏆";
        switch (rarity.toUpperCase()) {
            case "COMMON":
                return "⭐";
            case "RARE":
                return "🌟";
            case "EPIC":
                return "💎";
            case "LEGENDARY":
                return "👑";
            default:
                return "🏆";
        }
    }

    @Override
    public int getItemCount() {
        return badgeList != null ? badgeList.size() : 0;
    }

    public void updateData(List<BadgeItem> newBadgeList) {
        this.badgeList = newBadgeList;
        notifyDataSetChanged();
    }

    static class BadgeViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtName;
        TextView txtEmoji;
        TextView txtCount;
        TextView txtProgress;
        ProgressBar progressBar;
        View iconBackground;

        BadgeViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgBadgeIcon);
            txtName = itemView.findViewById(R.id.txtBadgeName);
            txtEmoji = itemView.findViewById(R.id.txtBadgeEmoji);
            txtCount = itemView.findViewById(R.id.txtBadgeCount);
            txtProgress = itemView.findViewById(R.id.txtProgress);
            progressBar = itemView.findViewById(R.id.progressBadge);
            iconBackground = itemView.findViewById(R.id.iconBackground);
        }
    }
}
