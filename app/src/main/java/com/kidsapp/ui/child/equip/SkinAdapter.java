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

    public interface OnSkinSelectedListener {
        void onSkinSelected(Skin skin);
    }

    public SkinAdapter(List<Skin> list, OnSkinSelectedListener listener) {
        this.skinList = list;
        this.listener = listener;
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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onSkinSelected(skin);
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
