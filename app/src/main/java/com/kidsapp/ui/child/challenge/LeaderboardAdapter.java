package com.kidsapp.ui.child.challenge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;

import java.util.List;

/**
 * Adapter cho RecyclerView hiển thị bảng xếp hạng
 */
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {
    
    private List<ApiService.LeaderboardResponse> leaderboardList;
    private String currentUserId;
    
    public LeaderboardAdapter(List<ApiService.LeaderboardResponse> leaderboardList) {
        this.leaderboardList = leaderboardList;
    }
    
    public void updateData(List<ApiService.LeaderboardResponse> newData) {
        this.leaderboardList = newData;
        notifyDataSetChanged();
    }
    
    public void setCurrentUserId(String userId) {
        this.currentUserId = userId;
        notifyDataSetChanged();
    }
    
    public int getCurrentUserPosition() {
        if (currentUserId == null) return -1;
        
        for (int i = 0; i < leaderboardList.size(); i++) {
            if (currentUserId.equals(leaderboardList.get(i).childId)) {
                return i;
            }
        }
        return -1;
    }
    
    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard, parent, false);
        return new LeaderboardViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        ApiService.LeaderboardResponse item = leaderboardList.get(position);
        holder.bind(item, currentUserId);
    }
    
    @Override
    public int getItemCount() {
        return leaderboardList.size();
    }
    
    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        private TextView textRank;
        private TextView textBadge;
        private ImageView imageAvatar;
        private TextView textName;
        private TextView textLevel;
        private TextView textXp;
        private TextView textStats;
        private View layoutItem;
        
        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            textRank = itemView.findViewById(R.id.textRank);
            textBadge = itemView.findViewById(R.id.textBadge);
            imageAvatar = itemView.findViewById(R.id.imageAvatar);
            textName = itemView.findViewById(R.id.textName);
            textLevel = itemView.findViewById(R.id.textLevel);
            textXp = itemView.findViewById(R.id.textXp);
            textStats = itemView.findViewById(R.id.textStats);
            layoutItem = itemView.findViewById(R.id.layoutItem);
        }
        
        public void bind(ApiService.LeaderboardResponse item, String currentUserId) {
            // Set rank
            textRank.setText(item.rankDisplay != null ? item.rankDisplay : "#" + item.rank);
            
            // Set badge and rank background based on position
            if (item.rank != null) {
                if (item.rank == 1) {
                    textRank.setBackgroundResource(R.drawable.bg_rank_gold);
                    textBadge.setText("🥇");
                    textBadge.setVisibility(View.VISIBLE);
                } else if (item.rank == 2) {
                    textRank.setBackgroundResource(R.drawable.bg_rank_silver);
                    textBadge.setText("🥈");
                    textBadge.setVisibility(View.VISIBLE);
                } else if (item.rank == 3) {
                    textRank.setBackgroundResource(R.drawable.bg_rank_bronze);
                    textBadge.setText("🥉");
                    textBadge.setVisibility(View.VISIBLE);
                } else if (item.rank <= 10) {
                    textRank.setBackgroundResource(R.drawable.bg_rank_top10);
                    textBadge.setText("⭐");
                    textBadge.setVisibility(View.VISIBLE);
                } else {
                    textRank.setBackgroundResource(R.drawable.bg_rank_default);
                    textBadge.setVisibility(View.GONE);
                }
            }
            
            // Set avatar (placeholder for now)
            if (item.childAvatar != null && !item.childAvatar.isEmpty()) {
                // TODO: Load avatar with Glide or Picasso
                imageAvatar.setImageResource(R.drawable.ic_child_avatar);
            } else {
                imageAvatar.setImageResource(R.drawable.ic_child_avatar);
            }
            
            // Set name
            textName.setText(item.childName != null ? item.childName : "Unknown");
            
            // Set level
            textLevel.setText("Level " + (item.childLevel != null ? item.childLevel : 1));
            
            // Set XP
            textXp.setText((item.totalXp != null ? item.totalXp : 0) + " XP");
            
            // Set stats
            int exercises = item.exercisesCompleted != null ? item.exercisesCompleted : 0;
            int challenges = item.challengesWon != null ? item.challengesWon : 0;
            textStats.setText(exercises + " bài • " + challenges + " thắng");
            
            // Highlight current user
            boolean isCurrentUser = currentUserId != null && currentUserId.equals(item.childId);
            if (isCurrentUser) {
                layoutItem.setBackgroundResource(R.drawable.bg_leaderboard_current_user);
                textName.setTextColor(itemView.getContext().getColor(R.color.primary));
            } else {
                layoutItem.setBackgroundResource(R.drawable.bg_leaderboard_item);
                textName.setTextColor(itemView.getContext().getColor(R.color.text_primary));
            }
            
            // Add click animation
            layoutItem.setOnClickListener(v -> {
                v.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        v.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .start();
                    })
                    .start();
            });
        }
    }
}