package com.kidsapp.ui.parent.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.databinding.ItemNotificationBinding;
import com.kidsapp.ui.parent.home.model.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho danh sách thông báo
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notifications = new ArrayList<>();
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification, int position);
    }

    public void setOnNotificationClickListener(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    public void markAllAsRead() {
        for (Notification notification : notifications) {
            notification.setRead(true);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new NotificationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final ItemNotificationBinding binding;

        public NotificationViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.layoutNotification.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    Notification notification = notifications.get(position);
                    notification.setRead(true);
                    notifyItemChanged(position);
                    listener.onNotificationClick(notification, position);
                }
            });
        }

        public void bind(Notification notification) {
            binding.txtChildAvatar.setText(notification.getChildAvatar());
            binding.txtNotificationMessage.setText(notification.getMessage());
            binding.txtNotificationTime.setText(notification.getTime());

            // Set icon theo loại task
            if (notification.isHabit()) {
                binding.imgTaskIcon.setImageResource(R.drawable.ic_book);
            } else {
                binding.imgTaskIcon.setImageResource(R.drawable.ic_target);
            }

            // Hiển thị chấm chưa đọc
            binding.viewUnreadIndicator.setVisibility(
                    notification.isRead() ? View.GONE : View.VISIBLE);

            // Background khác nếu chưa đọc
            if (!notification.isRead()) {
                binding.layoutNotification.setBackgroundColor(
                        binding.getRoot().getContext().getColor(R.color.notification_unread_bg));
            } else {
                binding.layoutNotification.setBackgroundResource(
                        android.R.drawable.list_selector_background);
            }
        }
    }
}
