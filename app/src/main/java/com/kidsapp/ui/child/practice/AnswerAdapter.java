package com.kidsapp.ui.child.practice;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.model.AnswerOption;
import com.kidsapp.databinding.ItemAnswerBinding;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(int position);
    }

    private final List<AnswerOption> options;
    private final OnAnswerSelectedListener listener;
    private int selectedIndex = -1;
    private int wrongIndex = -1;
    private int justSelectedIndex = -1; // Cho API data (chưa biết đúng/sai)

    public AnswerAdapter(List<AnswerOption> options, OnAnswerSelectedListener listener) {
        this.options = options;
        this.listener = listener;
    }

    public void resetState() {
        selectedIndex = -1;
        wrongIndex = -1;
        justSelectedIndex = -1;
        notifyDataSetChanged();
    }

    public void markCorrect(int index) {
        selectedIndex = index;
        wrongIndex = -1;
        justSelectedIndex = -1;
        notifyDataSetChanged();
    }

    public void markWrong(int index) {
        wrongIndex = index;
        selectedIndex = -1;
        justSelectedIndex = -1;
        notifyDataSetChanged();
    }
    
    /**
     * Đánh dấu đáp án đã chọn (cho API data - chưa biết đúng/sai)
     */
    public void markSelected(int index) {
        justSelectedIndex = index;
        selectedIndex = -1;
        wrongIndex = -1;
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

            // Reset states
            binding.layoutAnswer.setSelected(false);
            binding.layoutAnswer.setActivated(false);
            
            // Set state dựa trên index
            if (position == selectedIndex) {
                // Đúng (màu xanh lá)
                binding.layoutAnswer.setSelected(true);
            } else if (position == wrongIndex) {
                // Sai (màu đỏ)
                binding.layoutAnswer.setActivated(true);
            } else if (position == justSelectedIndex) {
                // Đã chọn nhưng chưa biết đúng/sai (màu xanh nhạt)
                binding.layoutAnswer.setBackgroundResource(R.drawable.bg_answer_selected);
            } else {
                // Chưa chọn (màu trắng)
                binding.layoutAnswer.setBackgroundResource(R.drawable.bg_answer_default);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAnswerSelected(position);
                }
            });

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

