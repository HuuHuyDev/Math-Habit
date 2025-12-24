package com.kidsapp.ui.child.challenge;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.model.AnswerOption;
import com.kidsapp.databinding.ItemAnswerBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho đáp án trong battle
 * Tương tự AnswerAdapter nhưng tối ưu cho battle mode
 */
public class BattleAnswerAdapter extends RecyclerView.Adapter<BattleAnswerAdapter.AnswerViewHolder> {

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(int position);
    }

    private final List<AnswerOption> options = new ArrayList<>();
    private final OnAnswerSelectedListener listener;
    private int selectedIndex = -1;
    private int wrongIndex = -1;
    private boolean isLocked = false;

    public BattleAnswerAdapter(OnAnswerSelectedListener listener) {
        this.listener = listener;
    }

    public void submitOptions(List<AnswerOption> newOptions) {
        options.clear();
        options.addAll(newOptions);
        notifyDataSetChanged();
    }

    public void resetState() {
        selectedIndex = -1;
        wrongIndex = -1;
        isLocked = false;
        notifyDataSetChanged();
    }

    public void markCorrect(int index) {
        selectedIndex = index;
        wrongIndex = -1;
        isLocked = true;
        notifyDataSetChanged();
    }

    public void markWrong(int index) {
        wrongIndex = index;
        selectedIndex = -1;
        isLocked = true;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAnswerBinding binding = ItemAnswerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new AnswerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        holder.bind(options.get(position), position);
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    class AnswerViewHolder extends RecyclerView.ViewHolder {
        private final ItemAnswerBinding binding;

        AnswerViewHolder(ItemAnswerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AnswerOption option, int position) {
            binding.txtOptionLabel.setText(option.getLabel());
            binding.txtOptionContent.setText(option.getContent());

            // Set state
            binding.layoutAnswer.setSelected(position == selectedIndex);
            binding.layoutAnswer.setActivated(position == wrongIndex);

            // Click listener
            binding.getRoot().setOnClickListener(v -> {
                if (!isLocked && listener != null) {
                    listener.onAnswerSelected(position);
                }
            });

            // Animation
            binding.getRoot().clearAnimation();
            if (position == selectedIndex) {
                Animation scale = AnimationUtils.loadAnimation(
                        binding.getRoot().getContext(), R.anim.scale_correct);
                binding.getRoot().startAnimation(scale);
            } else if (position == wrongIndex) {
                Animation shake = AnimationUtils.loadAnimation(
                        binding.getRoot().getContext(), R.anim.shake_wrong);
                binding.getRoot().startAnimation(shake);
            }
        }
    }
}
