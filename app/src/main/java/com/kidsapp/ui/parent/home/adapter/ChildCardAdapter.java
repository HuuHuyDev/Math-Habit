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

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvChildName;
        private final TextView tvChildClass;
        private final CircularProgressView progressView;
        private final Button btnViewDetail;

        ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChildName = itemView.findViewById(R.id.tvChildName);
            tvChildClass = itemView.findViewById(R.id.tvChildClass);
            progressView = itemView.findViewById(R.id.progressView);
            btnViewDetail = itemView.findViewById(R.id.btnViewDetail);
        }

        void bind(Child child, OnChildClickListener listener) {
            tvChildName.setText(child.getName());
            String grade = child.getLevel() > 0 ? "Lớp " + child.getLevel() : "Lớp 3";
            tvChildClass.setText(grade);

            progressView.animateTo(calculateProgress(child));

            View.OnClickListener clickListener = v -> {
                if (listener != null) {
                    listener.onChildClick(child);
                }
            };
            itemView.setOnClickListener(clickListener);
            btnViewDetail.setOnClickListener(clickListener);
            applyButtonScaleAnimation(btnViewDetail);
        }

        private float calculateProgress(Child child) {
            int level = child.getLevel();
            int xp = child.getTotalXP();
            if (level <= 0 && xp <= 0) {
                return 50f;
            }
            float normalized = Math.min(100f, level * 10f + xp * 0.05f);
            return Math.max(0f, normalized);
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

