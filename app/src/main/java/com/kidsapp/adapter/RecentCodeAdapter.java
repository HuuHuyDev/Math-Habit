package com.kidsapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.databinding.ItemRecentCodeBinding;
import com.kidsapp.model.RecentCode;

import java.util.List;

public class RecentCodeAdapter extends RecyclerView.Adapter<RecentCodeAdapter.RecentCodeViewHolder> {
    
    private List<RecentCode> recentCodes;
    private OnRecentCodeClickListener listener;
    
    public interface OnRecentCodeClickListener {
        void onRecentCodeClick(RecentCode recentCode);
    }
    
    public RecentCodeAdapter(List<RecentCode> recentCodes, OnRecentCodeClickListener listener) {
        this.recentCodes = recentCodes;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public RecentCodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecentCodeBinding binding = ItemRecentCodeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new RecentCodeViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecentCodeViewHolder holder, int position) {
        RecentCode recentCode = recentCodes.get(position);
        holder.bind(recentCode);
    }
    
    @Override
    public int getItemCount() {
        return recentCodes.size();
    }
    
    class RecentCodeViewHolder extends RecyclerView.ViewHolder {
        private ItemRecentCodeBinding binding;
        
        public RecentCodeViewHolder(ItemRecentCodeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
        public void bind(RecentCode recentCode) {
            binding.txtCode.setText(recentCode.getCode());
            binding.txtCodeInfo.setText(recentCode.getDisplayInfo());
            
            // Set click listener
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRecentCodeClick(recentCode);
                }
            });
        }
    }
}