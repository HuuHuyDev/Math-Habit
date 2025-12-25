package com.kidsapp.ui.child.challenge;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.model.LeaderboardItem;
import com.kidsapp.databinding.ItemLeaderboardBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho danh sách xếp hạng
 */
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private final List<LeaderboardItem> items = new ArrayList<>();

    public void submitList(List<LeaderboardItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLeaderboardBinding binding = ItemLeaderboardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new LeaderboardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        private final ItemLeaderboardBinding binding;

        LeaderboardViewHolder(ItemLeaderboardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(LeaderboardItem item) {
            binding.txtRank.setText(String.valueOf(item.getRank()));
            binding.txtPlayerName.setText(item.getPlayerName());
            binding.txtScore.setText(String.valueOf(item.getScore()));
            
            int winRate = (int) ((item.getWins() * 100.0) / (item.getWins() + item.getLosses()));
            binding.txtWinRate.setText("Tỷ lệ thắng: " + winRate + "%");

            // Change rank badge color for top 3
            int rankColor;
            if (item.getRank() == 1) {
                rankColor = R.color.gold;
            } else if (item.getRank() == 2) {
                rankColor = R.color.silver;
            } else if (item.getRank() == 3) {
                rankColor = R.color.bronze;
            } else {
                rankColor = R.color.primary;
            }
            binding.txtRank.setBackgroundTintList(
                binding.getRoot().getContext().getColorStateList(rankColor));
        }
    }
}
