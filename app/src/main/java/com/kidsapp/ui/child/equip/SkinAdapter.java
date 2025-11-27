package com.kidsapp.ui.child.equip;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.model.Skin;

import java.util.List;

public class SkinAdapter extends RecyclerView.Adapter<SkinAdapter.SkinViewHolder> {

    private List<Skin> skinList;
    private OnSkinSelectedListener listener;
    private int selectedPosition = -1; // Vị trí item đang được chọn

    public interface OnSkinSelectedListener {
        void onSkinSelected(Skin skin, int position);
    }

    public SkinAdapter(List<Skin> list, OnSkinSelectedListener listener) {
        this.skinList = list;
        this.listener = listener;
    }
    
    /**
     * Đặt item được chọn
     */
    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        
        // Refresh item cũ và mới
        if (previousPosition != -1) {
            notifyItemChanged(previousPosition);
        }
        notifyItemChanged(selectedPosition);
    }
    
    /**
     * Lấy vị trí đang chọn
     */
    public int getSelectedPosition() {
        return selectedPosition;
    }

    @NonNull
    @Override
    public SkinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_skin, parent, false);
        return new SkinViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SkinViewHolder holder, int position) {
        Skin skin = skinList.get(position);
        holder.imgSkin.setImageResource(skin.getIconRes());
        holder.txtSkinName.setText(skin.getName());

        // Hiển thị viền khi được chọn
        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.bg_round_20);
            holder.itemView.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0xFF2D8CFF) // Màu xanh
            );
            holder.itemView.setElevation(8f);
        } else {
            holder.itemView.setBackground(null);
            holder.itemView.setElevation(0f);
        }

        holder.itemView.setOnClickListener(v -> {
            setSelectedPosition(position);
            if (listener != null) listener.onSkinSelected(skin, position);
        });
    }

    @Override
    public int getItemCount() {
        return skinList.size();
    }

    static class SkinViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSkin;
        TextView txtSkinName;

        SkinViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSkin = itemView.findViewById(R.id.imgSkin);
            txtSkinName = itemView.findViewById(R.id.txtSkinName);
        }
    }
}
