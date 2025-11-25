package com.kidsapp.ui.parent.report.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.ui.parent.report.model.Achievement;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho RecyclerView hiển thị danh sách thành tích
 */
public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder> {

    private List<Achievement> achievementList = new ArrayList<>();

    public void setAchievementList(List<Achievement> achievementList) {
        this.achievementList = achievementList != null ? achievementList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_badge, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Achievement achievement = achievementList.get(position);
        holder.bind(achievement);
    }

    @Override
    public int getItemCount() {
        return achievementList.size();
    }

    static class AchievementViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgBadgeIcon;
        private TextView txtBadgeEmoji;
        private TextView txtBadgeName;
        private TextView txtBadgeCount;
        private View iconBackground;

        public AchievementViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBadgeIcon = itemView.findViewById(R.id.imgBadgeIcon);
            txtBadgeEmoji = itemView.findViewById(R.id.txtBadgeEmoji);
            txtBadgeName = itemView.findViewById(R.id.txtBadgeName);
            txtBadgeCount = itemView.findViewById(R.id.txtBadgeCount);
            iconBackground = itemView.findViewById(R.id.iconBackground);
        }

        public void bind(Achievement achievement) {
            // Hiển thị emoji, ẩn ImageView
            txtBadgeEmoji.setText(achievement.getIcon());
            txtBadgeEmoji.setVisibility(View.VISIBLE);
            imgBadgeIcon.setVisibility(View.GONE);
            
            // Hiển thị name và count
            txtBadgeName.setText(achievement.getName());
            txtBadgeCount.setText(achievement.getCountText());
            txtBadgeCount.setVisibility(View.VISIBLE);
            
            // Set background unlocked
            iconBackground.setBackgroundResource(R.drawable.bg_badge_unlocked);
        }
    }
}
