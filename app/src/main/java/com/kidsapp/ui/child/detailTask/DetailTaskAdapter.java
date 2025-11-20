package com.kidsapp.ui.child.detailTask;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.databinding.ItemDetailStepBinding;

import java.util.List;
public class DetailTaskAdapter extends RecyclerView.Adapter<DetailTaskAdapter.StepViewHolder> {

    private final List<Step> steps;

    public DetailTaskAdapter(@NonNull List<Step> steps) {
        this.steps = steps;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDetailStepBinding binding = ItemDetailStepBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new StepViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        holder.bind(steps.get(position));
    }

    @Override
    public int getItemCount() {
        return steps != null ? steps.size() : 0;
    }

    static class StepViewHolder extends RecyclerView.ViewHolder {
        private final ItemDetailStepBinding binding;

        StepViewHolder(@NonNull ItemDetailStepBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(@NonNull Step step) {
            binding.txtIndex.setText(String.valueOf(step.getIndex()));
            binding.txtStepTitle.setText(step.getTitle());
        }
    }
}