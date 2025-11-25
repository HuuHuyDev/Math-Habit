package com.kidsapp.ui.parent.child.detail.tabs.badge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.imgIcon.setImageResource(badge.getIconRes());
        
        // Đổi background và alpha dựa trên trạng thái unlock
        if (badge.isUnlocked()) {
            holder.iconBackground.setBackgroundResource(R.drawable.bg_badge_unlocked);
            holder.imgIcon.setAlpha(1.0f);
            holder.txtName.setAlpha(1.0f);
            holder.txtName.setTextColor(0xFF2D3748);
        } else {
            holder.iconBackground.setBackgroundResource(R.drawable.bg_badge_locked);
            holder.imgIcon.setAlpha(0.3f);
            holder.txtName.setAlpha(0.4f);
            holder.txtName.setTextColor(0xFFBDBDBD);
        }
    }

    @Override
    public int getItemCount() {
        return badgeList != null ? badgeList.size() : 0;
    }

    static class BadgeViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtName;
        View iconBackground;

        BadgeViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgBadgeIcon);
            txtName = itemView.findViewById(R.id.txtBadgeName);
            iconBackground = itemView.findViewById(R.id.iconBackground);
        }
    }
}
