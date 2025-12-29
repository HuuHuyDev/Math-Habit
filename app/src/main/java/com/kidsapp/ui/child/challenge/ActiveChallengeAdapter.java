package com.kidsapp.ui.child.challenge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.response.ChallengeResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho danh sách challenge đang active
 */
public class ActiveChallengeAdapter extends RecyclerView.Adapter<ActiveChallengeAdapter.ChallengeViewHolder> {

    private List<ChallengeResponse> challenges = new ArrayList<>();
    private OnChallengeActionListener listener;

    public interface OnChallengeActionListener {
        void onJoinChallenge(ChallengeResponse challenge);
        void onViewChallengeDetail(ChallengeResponse challenge);
    }

    public ActiveChallengeAdapter(OnChallengeActionListener listener) {
        this.listener = listener;
    }

    public void setChallenges(List<ChallengeResponse> challenges) {
        this.challenges = challenges != null ? challenges : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_active_challenge, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        ChallengeResponse challenge = challenges.get(position);
        holder.bind(challenge);
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    class ChallengeViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChallengeTitle;
        private TextView txtCategoryName;
        private TextView txtOpponentName;
        private TextView txtTimeRemaining;
        private TextView txtStatus;
        private Button btnJoin;
        private View layoutChallenge;

        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtChallengeTitle = itemView.findViewById(R.id.txtChallengeTitle);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName);
            txtOpponentName = itemView.findViewById(R.id.txtOpponentName);
            txtTimeRemaining = itemView.findViewById(R.id.txtTimeRemaining);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnJoin = itemView.findViewById(R.id.btnJoin);
            layoutChallenge = itemView.findViewById(R.id.layoutChallenge);
        }

        public void bind(ChallengeResponse challenge) {
            txtChallengeTitle.setText(challenge.getTitle() != null ? challenge.getTitle() : "Thách đấu");
            txtCategoryName.setText(challenge.getCategoryName() != null ? challenge.getCategoryName() : "Câu đố mẹo");
            
            // Show opponent name
            String opponentName = challenge.getOpponentName();
            if (opponentName != null && !opponentName.isEmpty()) {
                txtOpponentName.setText("Đối thủ: " + opponentName);
                txtOpponentName.setVisibility(View.VISIBLE);
            } else {
                txtOpponentName.setVisibility(View.GONE);
            }
            
            // Show time remaining
            String timeRemaining = challenge.getTimeRemaining();
            if (timeRemaining != null && !timeRemaining.isEmpty()) {
                txtTimeRemaining.setText("Thời gian: " + timeRemaining);
                txtTimeRemaining.setVisibility(View.VISIBLE);
            } else {
                txtTimeRemaining.setVisibility(View.GONE);
            }
            
            // Show status
            String status = challenge.getStatus();
            txtStatus.setText(getStatusText(status));
            
            // Handle button clicks
            btnJoin.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onJoinChallenge(challenge);
                }
            });

            // Handle item click for detail view
            layoutChallenge.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewChallengeDetail(challenge);
                }
            });
            
            // Show/hide join button based on status
            if ("ACTIVE".equals(status)) {
                btnJoin.setVisibility(View.VISIBLE);
                btnJoin.setText("Tham gia");
            } else if ("IN_PROGRESS".equals(status)) {
                btnJoin.setVisibility(View.VISIBLE);
                btnJoin.setText("Tiếp tục");
            } else {
                btnJoin.setVisibility(View.GONE);
            }
        }
        
        private String getStatusText(String status) {
            if (status == null) return "Không xác định";
            
            switch (status) {
                case "ACTIVE":
                    return "Sẵn sàng";
                case "IN_PROGRESS":
                    return "Đang diễn ra";
                case "WAITING_OPPONENT":
                    return "Chờ đối thủ";
                case "COMPLETED":
                    return "Đã hoàn thành";
                default:
                    return status;
            }
        }
    }
}