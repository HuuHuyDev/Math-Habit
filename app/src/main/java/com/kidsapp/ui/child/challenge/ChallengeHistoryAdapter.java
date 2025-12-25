package com.kidsapp.ui.child.challenge;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.model.ChallengeResult;
import com.kidsapp.databinding.ItemChallengeHistoryBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter cho lịch sử thách đấu
 */
public class ChallengeHistoryAdapter extends RecyclerView.Adapter<ChallengeHistoryAdapter.HistoryViewHolder> {

    private final List<ChallengeResult> results = new ArrayList<>();

    public void submitList(List<ChallengeResult> newResults) {
        results.clear();
        results.addAll(newResults);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChallengeHistoryBinding binding = ItemChallengeHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new HistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(results.get(position));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemChallengeHistoryBinding binding;

        HistoryViewHolder(ItemChallengeHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChallengeResult result) {
            // Result badge
            if (result.isWin()) {
                binding.txtResultBadge.setText("Thắng");
                binding.txtResultBadge.setBackgroundTintList(
                    binding.getRoot().getContext().getColorStateList(R.color.success));
            } else {
                binding.txtResultBadge.setText("Thua");
                binding.txtResultBadge.setBackgroundTintList(
                    binding.getRoot().getContext().getColorStateList(R.color.error));
            }

            // Date
            binding.txtDate.setText(result.getDate());

            // Players
            binding.txtPlayer1Name.setText(result.getPlayer1Name());
            binding.txtPlayer2Name.setText(result.getPlayer2Name());
            binding.txtPlayer1Score.setText(result.getPlayer1Score() + " điểm");
            binding.txtPlayer2Score.setText(result.getPlayer2Score() + " điểm");

            // Stats
            int totalQuestions = result.getCorrectAnswers() + result.getWrongAnswers();
            long seconds = result.getTotalTime() / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            String timeStr = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
            
            binding.txtStats.setText(
                result.getCorrectAnswers() + "/" + totalQuestions + " đúng • " + timeStr
            );
        }
    }
}
