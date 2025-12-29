package com.kidsapp.ui.child.challenge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.response.ChallengeInviteResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho danh sách lời mời thách đấu
 */
public class ChallengeInviteAdapter extends RecyclerView.Adapter<ChallengeInviteAdapter.InviteViewHolder> {

    private List<ChallengeInviteResponse> invites = new ArrayList<>();
    private OnInviteActionListener listener;

    public interface OnInviteActionListener {
        void onAcceptInvite(ChallengeInviteResponse invite);
        void onDeclineInvite(ChallengeInviteResponse invite);
        void onViewInviteDetail(ChallengeInviteResponse invite);
    }

    public ChallengeInviteAdapter(OnInviteActionListener listener) {
        this.listener = listener;
    }

    public void setInvites(List<ChallengeInviteResponse> invites) {
        this.invites = invites != null ? invites : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InviteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_challenge_invite, parent, false);
        return new InviteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InviteViewHolder holder, int position) {
        ChallengeInviteResponse invite = invites.get(position);
        holder.bind(invite);
    }

    @Override
    public int getItemCount() {
        return invites.size();
    }

    class InviteViewHolder extends RecyclerView.ViewHolder {
        private TextView txtCreatorName;
        private TextView txtChallengeTitle;
        private TextView txtCategoryName;
        private TextView txtTimeAgo;
        private Button btnAccept;
        private Button btnDecline;
        private View layoutInvite;

        public InviteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCreatorName = itemView.findViewById(R.id.txtCreatorName);
            txtChallengeTitle = itemView.findViewById(R.id.txtChallengeTitle);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName);
            txtTimeAgo = itemView.findViewById(R.id.txtTimeAgo);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            layoutInvite = itemView.findViewById(R.id.layoutInvite);
        }

        public void bind(ChallengeInviteResponse invite) {
            txtCreatorName.setText(invite.getCreatorName() != null ? invite.getCreatorName() : "Bạn bè");
            txtChallengeTitle.setText(invite.getChallengeTitle() != null ? invite.getChallengeTitle() : "Thách đấu");
            txtCategoryName.setText(invite.getCategoryName() != null ? invite.getCategoryName() : "Câu đố mẹo");
            txtTimeAgo.setText(invite.getTimeAgo() != null ? invite.getTimeAgo() : "Vừa xong");

            // Handle button clicks
            btnAccept.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAcceptInvite(invite);
                }
            });

            btnDecline.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeclineInvite(invite);
                }
            });

            // Handle item click for detail view
            layoutInvite.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewInviteDetail(invite);
                }
            });

            // Show/hide buttons based on status
            String status = invite.getStatus();
            if ("PENDING".equals(status)) {
                btnAccept.setVisibility(View.VISIBLE);
                btnDecline.setVisibility(View.VISIBLE);
            } else {
                btnAccept.setVisibility(View.GONE);
                btnDecline.setVisibility(View.GONE);
            }
        }
    }
}