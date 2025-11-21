package com.kidsapp.ui.child.quizz;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.model.AnswerOption;
import com.kidsapp.databinding.ItemExamAnswerBinding;

import java.util.ArrayList;
import java.util.List;

public class ExamAnswerAdapter extends RecyclerView.Adapter<ExamAnswerAdapter.ExamAnswerViewHolder> {

    public interface OnAnswerClickListener {
        void onAnswerClick(int position);
    }

    private final List<AnswerOption> options = new ArrayList<>();
    private final OnAnswerClickListener listener;
    private int selectedIndex = -1;

    public ExamAnswerAdapter(@NonNull OnAnswerClickListener listener) {
        this.listener = listener;
    }

    public void submitOptions(@NonNull List<AnswerOption> newOptions) {
        options.clear();
        options.addAll(newOptions);
        notifyDataSetChanged();
    }

    public void setSelectedIndex(int index) {
        selectedIndex = index;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExamAnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExamAnswerBinding binding = ItemExamAnswerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ExamAnswerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamAnswerViewHolder holder, int position) {
        holder.bind(options.get(position), position);
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    class ExamAnswerViewHolder extends RecyclerView.ViewHolder {
        private final ItemExamAnswerBinding binding;

        ExamAnswerViewHolder(@NonNull ItemExamAnswerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AnswerOption option, int position) {
            binding.txtOptionLabel.setText(option.getLabel());
            binding.txtOptionContent.setText(option.getContent());

            boolean isSelected = position == selectedIndex;
            Context context = binding.getRoot().getContext();

            int strokeColor = ContextCompat.getColor(context,
                    isSelected ? R.color.primary : R.color.border);
            int backgroundColor = ContextCompat.getColor(context,
                    isSelected ? R.color.surface_variant : R.color.surface);
            int labelTextColor = ContextCompat.getColor(context,
                    isSelected ? R.color.white : R.color.primary);

            binding.cardAnswer.setStrokeColor(strokeColor);
            binding.cardAnswer.setCardBackgroundColor(backgroundColor);
            binding.txtOptionLabel.setBackgroundResource(
                    isSelected ? R.drawable.bg_button_kids_gradient : R.drawable.bg_round_20);
            binding.txtOptionLabel.setTextColor(labelTextColor);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAnswerClick(position);
                }
            });
        }
    }
}

