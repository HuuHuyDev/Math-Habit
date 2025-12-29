package com.kidsapp.ui.child.profile;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kidsapp.R;
import com.kidsapp.data.model.Badge;

import java.util.List;

/**
 * Adapter hiển thị badges trong trang Profile
 */
public class BadgeProfileAdapter extends RecyclerView.Adapter<BadgeProfileAdapter.ViewHolder> {

    private final List<Badge> badges;

    public BadgeProfileAdapter(List<Badge> badges) {
        this.badges = badges;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_badge_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Badge badge = badges.get(position);
        holder.bind(badge);
    }

    @Override
    public int getItemCount() {
        return badges.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout frameBadge;
        private final ImageView imgBadge;
        private final TextView tvBadgeName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            frameBadge = itemView.findViewById(R.id.frameBadge);
            imgBadge = itemView.findViewById(R.id.imgBadge);
            tvBadgeName = itemView.findViewById(R.id.tvBadgeName);
        }

        void bind(Badge badge) {
            tvBadgeName.setText(badge.getName());
            
            // Set background based on rarity
            int bgRes = getBadgeBackground(badge.getRarity());
            frameBadge.setBackgroundResource(bgRes);
            
            // Load icon
            String iconUrl = badge.getIconUrl();
            if (iconUrl != null && !iconUrl.isEmpty()) {
                if (iconUrl.startsWith("http")) {
                    Glide.with(itemView.getContext())
                            .load(iconUrl)
                            .placeholder(R.drawable.ic_trophy)
                            .into(imgBadge);
                } else {
                    int resId = itemView.getContext().getResources().getIdentifier(
                            iconUrl, "drawable", itemView.getContext().getPackageName());
                    if (resId != 0) {
                        imgBadge.setImageResource(resId);
                    } else {
                        imgBadge.setImageResource(R.drawable.ic_trophy);
                    }
                }
            } else {
                // Default icon based on badge type
                imgBadge.setImageResource(getDefaultIcon(badge.getBadgeType()));
            }
            
            // Set tint based on rarity
            int tintColor = getTintColor(badge.getRarity());
            imgBadge.setColorFilter(ContextCompat.getColor(itemView.getContext(), tintColor));
            
            // Grayscale if not earned
            if (!badge.isEarned()) {
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                imgBadge.setColorFilter(new ColorMatrixColorFilter(matrix));
                frameBadge.setAlpha(0.5f);
            } else {
                imgBadge.clearColorFilter();
                imgBadge.setColorFilter(ContextCompat.getColor(itemView.getContext(), tintColor));
                frameBadge.setAlpha(1f);
            }
        }
        
        private int getBadgeBackground(String rarity) {
            if (rarity == null) {
                return R.drawable.bg_badge_gold;
            }
            if ("LEGENDARY".equals(rarity)) {
                return R.drawable.bg_badge_gold;
            } else if ("EPIC".equals(rarity)) {
                return R.drawable.bg_badge_purple;
            } else if ("RARE".equals(rarity)) {
                return R.drawable.bg_badge_fire;
            } else {
                return R.drawable.bg_badge_green;
            }
        }
        
        private int getTintColor(String rarity) {
            if (rarity == null) {
                return R.color.coin_orange;
            }
            if ("LEGENDARY".equals(rarity)) {
                return R.color.coin_orange;
            } else if ("EPIC".equals(rarity)) {
                return R.color.purple_500;
            } else if ("RARE".equals(rarity)) {
                return R.color.status_error;
            } else {
                return R.color.status_success;
            }
        }
        
        private int getDefaultIcon(String badgeType) {
            if (badgeType == null) {
                return R.drawable.ic_trophy;
            }
            if ("STREAK".equals(badgeType)) {
                return R.drawable.ic_fire;
            } else if ("MILESTONE".equals(badgeType)) {
                return R.drawable.ic_target;
            } else if ("SPECIAL".equals(badgeType)) {
                return R.drawable.ic_star_filled;
            } else {
                return R.drawable.ic_trophy;
            }
        }
    }
}
