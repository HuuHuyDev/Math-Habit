package com.kidsapp.ui.parent.report.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kidsapp.R;
import com.kidsapp.ui.parent.report.model.Child;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho danh sách bé trong BottomSheet
 */
public class ReportChildSelectorAdapter extends RecyclerView.Adapter<ReportChildSelectorAdapter.ChildViewHolder> {

    private List<Child> childList = new ArrayList<>();
    private OnChildSelectedListener listener;

    public interface OnChildSelectedListener {
        void onChildSelected(Child child);
    }

    public ReportChildSelectorAdapter(OnChildSelectedListener listener) {
        this.listener = listener;
    }

    public void setChildList(List<Child> childList) {
        this.childList = childList != null ? childList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report_child_option, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        Child child = childList.get(position);
        holder.bind(child);
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgChildAvatarImage;
        private TextView imgChildAvatar;
        private TextView txtChildName;
        private TextView txtChildLevel;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            imgChildAvatarImage = itemView.findViewById(R.id.imgChildAvatarImage);
            imgChildAvatar = itemView.findViewById(R.id.imgChildAvatar);
            txtChildName = itemView.findViewById(R.id.txtChildName);
            txtChildLevel = itemView.findViewById(R.id.txtChildLevel);
        }

        public void bind(Child child) {
            // Load avatar - hỗ trợ URL, drawable name, và emoji
            loadAvatar(child.getAvatar());
            
            txtChildName.setText(child.getName());
            txtChildLevel.setText(child.getLevelText());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChildSelected(child);
                }
            });
        }
        
        /**
         * Load avatar - hỗ trợ HTTP URL, drawable name, và emoji
         */
        private void loadAvatar(String avatar) {
            Context context = itemView.getContext();
            
            if (avatar == null || avatar.isEmpty()) {
                // Default emoji
                imgChildAvatarImage.setVisibility(View.GONE);
                imgChildAvatar.setVisibility(View.VISIBLE);
                imgChildAvatar.setText("👤");
                return;
            }
            
            if (avatar.startsWith("http")) {
                // Load từ URL
                imgChildAvatarImage.setVisibility(View.VISIBLE);
                imgChildAvatar.setVisibility(View.GONE);
                Glide.with(context)
                        .load(avatar)
                        .placeholder(R.drawable.ic_user_default)
                        .error(R.drawable.ic_user_default)
                        .circleCrop()
                        .into(imgChildAvatarImage);
            } else if (avatar.startsWith("ic_") || avatar.startsWith("avatar_")) {
                // Load từ drawable name
                int resId = context.getResources().getIdentifier(
                        avatar, "drawable", context.getPackageName());
                if (resId != 0) {
                    imgChildAvatarImage.setVisibility(View.VISIBLE);
                    imgChildAvatar.setVisibility(View.GONE);
                    imgChildAvatarImage.setImageResource(resId);
                } else {
                    // Fallback to emoji
                    imgChildAvatarImage.setVisibility(View.GONE);
                    imgChildAvatar.setVisibility(View.VISIBLE);
                    imgChildAvatar.setText("👤");
                }
            } else {
                // Emoji hoặc text
                imgChildAvatarImage.setVisibility(View.GONE);
                imgChildAvatar.setVisibility(View.VISIBLE);
                imgChildAvatar.setText(avatar);
            }
        }
    }
}

