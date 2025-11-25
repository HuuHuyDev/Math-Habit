package com.kidsapp.ui.parent.report.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        private TextView imgChildAvatar;
        private TextView txtChildName;
        private TextView txtChildLevel;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            imgChildAvatar = itemView.findViewById(R.id.imgChildAvatar);
            txtChildName = itemView.findViewById(R.id.txtChildName);
            txtChildLevel = itemView.findViewById(R.id.txtChildLevel);
        }

        public void bind(Child child) {
            imgChildAvatar.setText(child.getAvatar());
            txtChildName.setText(child.getName());
            txtChildLevel.setText(child.getLevelText());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChildSelected(child);
                }
            });
        }
    }
}

