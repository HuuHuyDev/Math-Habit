package com.kidsapp.ui.child.reward;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.kidsapp.R;
import com.kidsapp.data.model.Badge;

import java.util.List;

/**
 * Adapter hiển thị badges trong trang Thành tựu
 */
public class BadgeRewardAdapter extends RecyclerView.Adapter<BadgeRewardAdapter.ViewHolder> {

    public enum DisplayMode {
        EARNED,      // Đã đạt
        IN_PROGRESS, // Đang tiến hành
        LOCKED       // Chưa mở khóa
    }

    private final List<Badge> badges;
    private final DisplayMode displayMode;

    public BadgeRewardAdapter(List<Badge> badges, DisplayMode displayMode) {
        this.badges = badges;
        this.displayMode = displayMode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_badge_reward, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Badge badge = badges.get(position);
        holder.bind(badge, displayMode);
    }

    @Override
    public int getItemCount() {
        return badges.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout frameBadge;
        private final ImageView imgBadge;
        private final TextView tvBadgeName;
        private final TextView tvStatusEarned;
        private final LinearLayout layoutProgress;
        private final LinearProgressIndicator progressBadge;
        private final TextView tvProgressText;
        private final TextView tvRequirement;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            frameBadge = itemView.findViewById(R.id.frameBadge);
            imgBadge = itemView.findViewById(R.id.imgBadge);
            tvBadgeName = itemView.findViewById(R.id.tvBadgeName);
            tvStatusEarned = itemView.findViewById(R.id.tvStatusEarned);
            layoutProgress = itemView.findViewById(R.id.layoutProgress);
            progressBadge = itemView.findViewById(R.id.progressBadge);
            tvProgressText = itemView.findViewById(R.id.tvProgressText);
            tvRequirement = itemView.findViewById(R.id.tvRequirement);
        }

        void bind(Badge badge, DisplayMode mode) {
            tvBadgeName.setText(badge.getName());
            
            // Set background based on rarity
            int bgRes = getBadgeBackground(badge.getRarity(), mode);
            frameBadge.setBackgroundResource(bgRes);
            
            // Load icon
            loadBadgeIcon(badge, mode);
            
            // Hide all status views first
            tvStatusEarned.setVisibility(View.GONE);
            layoutProgress.setVisibility(View.GONE);
            tvRequirement.setVisibility(View.GONE);
            
            // Show appropriate status based on mode
            if (mode == DisplayMode.EARNED) {
                tvStatusEarned.setVisibility(View.VISIBLE);
                tvBadgeName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_primary));
            } else if (mode == DisplayMode.IN_PROGRESS) {
                layoutProgress.setVisibility(View.VISIBLE);
                progressBadge.setProgress(badge.getProgressPercent());
                tvProgressText.setText(badge.getProgressValue() + "/" + badge.getRequirementValue());
                tvBadgeName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_primary));
                
                // Set progress color based on rarity
                int progressColor = getProgressColor(badge.getRarity());
                progressBadge.setIndicatorColor(ContextCompat.getColor(itemView.getContext(), progressColor));
            } else {
                // LOCKED
                tvRequirement.setVisibility(View.VISIBLE);
                tvRequirement.setText(getRequirementText(badge));
                tvBadgeName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_tertiary));
                
                // Grayscale icon
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                imgBadge.setColorFilter(new ColorMatrixColorFilter(matrix));
                frameBadge.setAlpha(0.6f);
            }
        }
        
        private void loadBadgeIcon(Badge badge, DisplayMode mode) {
            String iconUrl = badge.getIconUrl();
            
            // Clear previous filter
            if (mode != DisplayMode.LOCKED) {
                imgBadge.clearColorFilter();
                frameBadge.setAlpha(1f);
            }
            
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
                imgBadge.setImageResource(getDefaultIcon(badge.getBadgeType()));
            }
            
            // Set tint based on rarity (only for non-locked)
            if (mode != DisplayMode.LOCKED) {
                int tintColor = getTintColor(badge.getRarity());
                imgBadge.setColorFilter(ContextCompat.getColor(itemView.getContext(), tintColor));
            }
        }
        
        private int getBadgeBackground(String rarity, DisplayMode mode) {
            if (mode == DisplayMode.LOCKED) {
                return R.drawable.bg_achievement_locked;
            }
            if (rarity == null) {
                return R.drawable.bg_achievement_gold;
            }
            if ("LEGENDARY".equals(rarity)) {
                return R.drawable.bg_achievement_gold;
            } else if ("EPIC".equals(rarity)) {
                return R.drawable.bg_achievement_purple;
            } else if ("RARE".equals(rarity)) {
                return R.drawable.bg_achievement_orange;
            } else {
                return R.drawable.bg_achievement_green;
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
        
        private int getProgressColor(String rarity) {
            if (rarity == null) {
                return R.color.coin_orange;
            }
            if ("LEGENDARY".equals(rarity)) {
                return R.color.coin_orange;
            } else if ("EPIC".equals(rarity)) {
                return R.color.purple_500;
            } else if ("RARE".equals(rarity)) {
                return R.color.coin_orange;
            } else {
                return R.color.primary;
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
        
        private String getRequirementText(Badge badge) {
            String type = badge.getBadgeType();
            int value = badge.getRequirementValue();
            
            if (type == null) {
                return "Hoàn thành " + value;
            }
            if ("STREAK".equals(type)) {
                return "Streak " + value + " ngày";
            } else if ("MILESTONE".equals(type)) {
                return "Đạt level " + value;
            } else {
                return "Hoàn thành " + value + " bài";
            }
        }
    }
}
