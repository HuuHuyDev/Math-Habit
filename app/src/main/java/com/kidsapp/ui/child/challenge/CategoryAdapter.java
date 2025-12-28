package com.kidsapp.ui.child.challenge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách Category (chủ đề thách đấu)
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories = new ArrayList<>();
    private String selectedCategoryId;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public void setSelectedCategory(String categoryId) {
        this.selectedCategoryId = categoryId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imgIcon;
        TextView txtName;
        View viewSelected;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardCategory);
            imgIcon = itemView.findViewById(R.id.imgCategoryIcon);
            txtName = itemView.findViewById(R.id.txtCategoryName);
            viewSelected = itemView.findViewById(R.id.viewSelected);
        }

        void bind(Category category) {
            txtName.setText(category.getName());
            
            // Set icon based on category code
            int iconRes = getIconResource(category.getCode());
            imgIcon.setImageResource(iconRes);
            
            // Highlight if selected
            boolean isSelected = category.getId().equals(selectedCategoryId);
            viewSelected.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            cardView.setCardElevation(isSelected ? 8f : 4f);
            
            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }

        private int getIconResource(String code) {
            if (code == null) {
                return R.drawable.ic_question;
            }
            
            switch (code) {
                case "logic":
                    return R.drawable.ic_brain;
                case "trick":
                    return R.drawable.ic_magic;
                case "iq":
                    return R.drawable.ic_lightbulb;
                case "riddle":
                    return R.drawable.ic_question;
                default:
                    return R.drawable.ic_question;
            }
        }
    }
}
