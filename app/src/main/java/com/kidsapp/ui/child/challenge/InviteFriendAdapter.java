package com.kidsapp.ui.child.challenge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.model.Child;
import com.kidsapp.databinding.ItemInviteFriendBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Adapter hiển thị danh sách bạn bè để mời thách đấu
 */
public class InviteFriendAdapter extends RecyclerView.Adapter<InviteFriendAdapter.FriendViewHolder> {

    private List<Child> friends = new ArrayList<>();
    private List<Child> filteredFriends = new ArrayList<>();
    private Set<String> invitedIds = new HashSet<>();
    private OnInviteClickListener listener;

    public interface OnInviteClickListener {
        void onInviteClick(Child friend);
    }

    public InviteFriendAdapter(OnInviteClickListener listener) {
        this.listener = listener;
    }

    public void setFriends(List<Child> friends) {
        this.friends = friends;
        this.filteredFriends = new ArrayList<>(friends);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        filteredFriends.clear();
        if (query == null || query.isEmpty()) {
            filteredFriends.addAll(friends);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            for (Child friend : friends) {
                if (friend.getName() != null && 
                    friend.getName().toLowerCase().contains(lowerQuery)) {
                    filteredFriends.add(friend);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void markAsInvited(String childId) {
        invitedIds.add(childId);
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return filteredFriends.isEmpty();
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInviteFriendBinding binding = ItemInviteFriendBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FriendViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        holder.bind(filteredFriends.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredFriends.size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {
        private final ItemInviteFriendBinding binding;

        FriendViewHolder(ItemInviteFriendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Child friend) {
            // Name
            binding.txtName.setText(friend.getName());

            // Level
            binding.txtLevel.setText("Level " + friend.getLevel());

            // XP with formatting (sử dụng totalPoints thay vì totalXP)
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            binding.txtXP.setText(formatter.format(friend.getTotalPoints()) + " XP");

            // Avatar - có thể load từ URL nếu có
            // Glide.with(binding.getRoot()).load(friend.getAvatarUrl()).into(binding.imgAvatar);
            binding.imgAvatar.setImageResource(R.drawable.ic_child_avatar);

            // Check if already invited
            boolean isInvited = invitedIds.contains(friend.getId());
            binding.btnInvite.setVisibility(isInvited ? View.GONE : View.VISIBLE);
            binding.txtInvited.setVisibility(isInvited ? View.VISIBLE : View.GONE);

            // Invite button click
            binding.btnInvite.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onInviteClick(friend);
                }
            });
        }
    }
}
