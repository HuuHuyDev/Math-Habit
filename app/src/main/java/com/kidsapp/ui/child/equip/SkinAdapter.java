package com.kidsapp.ui.child.equip;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kidsapp.R;
import com.kidsapp.data.model.Skin;

import java.util.List;

public class SkinAdapter extends RecyclerView.Adapter<SkinAdapter.SkinViewHolder> {

    private List<Skin> skinList;
    private OnSkinSelectedListener listener;
    private int selectedPosition = -1;

    public interface OnSkinSelectedListener {
        void onSkinSelected(Skin skin, int position);
    }

    public SkinAdapter(List<Skin> list, OnSkinSelectedListener listener) {
        this.skinList = list;
        this.listener = listener;
    }

    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;

        if (previousPosition != -1) {
            notifyItemChanged(previousPosition);
        }
        notifyItemChanged(selectedPosition);
    }

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

        // Hiển thị giá (giả lập)
        int price = (position + 1) * 100;
        if (position == 0) {
            holder.txtPrice.setText("Miễn phí");
        } else {
            holder.txtPrice.setText(String.valueOf(price));
        }

        // Hiển thị viền khi được chọn
        if (position == selectedPosition) {
            holder.cardSkin.setStrokeColor(0xFF2563EB); // Primary color
            holder.cardSkin.setStrokeWidth(3);
            holder.cardSkin.setCardElevation(8f);
            holder.txtStatus.setText("Đang chọn");
            holder.txtStatus.setBackgroundResource(R.drawable.bg_status_equipped);
            holder.txtStatus.setVisibility(View.VISIBLE);
        } else {
            holder.cardSkin.setStrokeColor(0xFFE5E7EB); // Border color
            holder.cardSkin.setStrokeWidth(1);
            holder.cardSkin.setCardElevation(2f);
            
            if (position == 0) {
                holder.txtStatus.setText("Đang dùng");
                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_owned);
                holder.txtStatus.setVisibility(View.VISIBLE);
            } else {
                holder.txtStatus.setText("Mua");
                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_badge);
                holder.txtStatus.setVisibility(View.VISIBLE);
            }
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
        MaterialCardView cardSkin;
        ImageView imgSkin;
        TextView txtSkinName;
        TextView txtPrice;
        TextView txtStatus;

        SkinViewHolder(@NonNull View itemView) {
            super(itemView);
            cardSkin = itemView.findViewById(R.id.cardSkin);
            imgSkin = itemView.findViewById(R.id.imgSkin);
            txtSkinName = itemView.findViewById(R.id.txtSkinName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }
}
