package com.kidsapp.ui.parent.home.adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.kidsapp.R;
import com.kidsapp.data.model.Child;
import com.kidsapp.ui.components.CircularProgressView;

import java.util.List;

public class ChildCardAdapter extends RecyclerView.Adapter<ChildCardAdapter.ChildViewHolder> {

    public interface OnChildClickListener {
        void onChildClick(Child child);
    }

    private final List<Child> children;
    private final OnChildClickListener listener;

    public ChildCardAdapter(List<Child> children, OnChildClickListener listener) {
        this.children = children;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_child_card, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        holder.bind(children.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return children != null ? children.size() : 0;
    }

    /**
     * Cập nhật danh sách children
     */
    public void updateChildren(List<Child> newChildren) {
        this.children.clear();
        if (newChildren != null) {
            this.children.addAll(newChildren);
        }
        notifyDataSetChanged();
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgChildAvatar;
        private final TextView tvChildName;
        private final TextView tvChildClass;
        private final CircularProgressView progressView;
        private final Button btnViewDetail;

        ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            imgChildAvatar = itemView.findViewById(R.id.imgChildAvatar);
            tvChildName = itemView.findViewById(R.id.tvChildName);
            tvChildClass = itemView.findViewById(R.id.tvChildClass);
            progressView = itemView.findViewById(R.id.progressView);
            btnViewDetail = itemView.findViewById(R.id.btnViewDetail);
        }

        void bind(Child child, OnChildClickListener listener) {
            tvChildName.setText(child.getName());
            
            // Hiển thị lớp từ grade
            Integer grade = child.getGrade();
            String gradeText = (grade != null && grade > 0) ? "Lớp " + grade : "Lớp 1";
            tvChildClass.setText(gradeText);

            // Load avatar bằng Glide
            loadAvatar(child.getAvatarUrl());

            // Hiển thị progress (% task hoàn thành trong ngày)
            float progress = child.getDailyProgress();
            progressView.animateTo(progress);

            View.OnClickListener clickListener = v -> {
                if (listener != null) {
                    listener.onChildClick(child);
                }
            };
            itemView.setOnClickListener(clickListener);
            btnViewDetail.setOnClickListener(clickListener);
            applyButtonScaleAnimation(btnViewDetail);
        }

        private void loadAvatar(String avatarUrl) {
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(avatarUrl)
                        .transform(new CircleCrop())
                        .placeholder(R.drawable.ic_user_default)
                        .error(R.drawable.ic_user_default)
                        .into(imgChildAvatar);
            } else {
                imgChildAvatar.setImageResource(R.drawable.ic_user_default);
            }
        }

        private void applyButtonScaleAnimation(Button button) {
            int duration = button.getResources().getInteger(R.integer.anim_duration_short);
            button.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(duration).start();
                } else if (event.getAction() == MotionEvent.ACTION_UP ||
                        event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(duration).start();
                }
                return false;
            });
        }
    }
}

