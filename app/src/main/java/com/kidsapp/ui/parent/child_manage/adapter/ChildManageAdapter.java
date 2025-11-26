package com.kidsapp.ui.parent.child_manage.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.databinding.ItemChildManageBinding;
import com.kidsapp.ui.parent.child_manage.model.ChildModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho danh sách quản lý bé
 */
public class ChildManageAdapter extends RecyclerView.Adapter<ChildManageAdapter.ChildViewHolder> {

    private List<ChildModel> children = new ArrayList<>();
    private OnChildActionListener listener;

    public interface OnChildActionListener {
        void onViewDetail(ChildModel child, int position);
        void onEdit(ChildModel child, int position);
        void onDelete(ChildModel child, int position);
    }

    public void setOnChildActionListener(OnChildActionListener listener) {
        this.listener = listener;
    }

    public void setChildren(List<ChildModel> children) {
        this.children = children;
        notifyDataSetChanged();
    }

    public void addChild(ChildModel child) {
        children.add(child);
        notifyItemInserted(children.size() - 1);
    }

    public void updateChild(int position, ChildModel child) {
        if (position >= 0 && position < children.size()) {
            children.set(position, child);
            notifyItemChanged(position);
        }
    }

    public void removeChild(int position) {
        if (position >= 0 && position < children.size()) {
            children.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChildManageBinding binding = ItemChildManageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ChildViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        holder.bind(children.get(position));
    }

    @Override
    public int getItemCount() {
        return children.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        private final ItemChildManageBinding binding;

        public ChildViewHolder(ItemChildManageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.btnViewDetail.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onViewDetail(children.get(position), position);
                }
            });

            binding.btnEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEdit(children.get(position), position);
                }
            });

            binding.btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDelete(children.get(position), position);
                }
            });
        }

        public void bind(ChildModel child) {
            binding.txtChildAvatar.setText(child.getAvatar());
            binding.txtChildName.setText(child.getName());
            binding.txtChildClass.setText(child.getClassAndLevel());
            binding.txtChildXP.setText(child.getXPText());
            binding.txtChildCoins.setText(String.valueOf(child.getCoins()));
            binding.txtProgress.setText(child.getProgressText());

            // Set progress bar width
            int progressPercentage = child.getProgressPercentage();
            ViewGroup.LayoutParams params = binding.viewProgress.getLayoutParams();
            params.width = (int) (binding.getRoot().getWidth() * (progressPercentage / 100.0f));
            binding.viewProgress.setLayoutParams(params);
        }
    }
}
