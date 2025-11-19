package com.kidsapp.ui.child.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kidsapp.R;
import com.kidsapp.databinding.FragmentChildHomeBinding;
import com.kidsapp.databinding.ViewChildActionListBinding;

/**
 * Child Home Fragment
 */
public class ChildHomeFragment extends Fragment {
    private FragmentChildHomeBinding binding;
    private ViewChildActionListBinding actionListBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChildHomeBinding.inflate(inflater, container, false);
        actionListBinding = binding.actionsContainer;
//        actionListBinding = ViewChildActionListBinding.bind(binding.actionsContainer);
        setupHeader();
        bindTrainingStats();
        setupActionCards();
        return binding.getRoot();
    }

    private void setupHeader() {
        binding.headerUser.setUserName("Hồ Hữu Huy");
        binding.headerUser.setAvatar(R.drawable.ic_user_default);
        binding.headerUser.setNotificationClick(v ->
                Toast.makeText(requireContext(), R.string.feature_coming_soon, Toast.LENGTH_SHORT).show());
    }

    private void bindTrainingStats() {
        int level = 15;
        int currentExp = 100;
        int targetExp = 150;
        int remainExp = Math.max(targetExp - currentExp, 0);
        int progressPercent = (int) (100f * currentExp / targetExp);

        binding.txtLevel.setText(String.valueOf(level));
        binding.txtLevelLabel.setText(getString(R.string.child_level_value, level));
        binding.txtExp.setText(getString(R.string.child_exp_value, currentExp, targetExp));
        binding.progressExp.setProgress(progressPercent);
        binding.txtExpHint.setText(getString(R.string.child_exp_hint, remainExp));
    }

    private void setupActionCards() {
        configureAction(actionListBinding.actionMission.getRoot(),
                R.drawable.bg_action_blue,
                R.drawable.ic_task,
                getString(R.string.child_action_mission));

        configureAction(actionListBinding.actionStore.getRoot(),
                R.drawable.bg_action_purple,
                R.drawable.ic_store,
                getString(R.string.child_action_store));

        configureAction(actionListBinding.actionAchievement.getRoot(),
                R.drawable.bg_action_orange,
                R.drawable.ic_trophy,
                getString(R.string.child_action_achievement));
    }

    private void configureAction(View actionView, int backgroundRes, int iconRes, String title) {
        FrameLayout iconContainer = actionView.findViewById(R.id.iconContainer);
        ImageView icon = actionView.findViewById(R.id.imgIcon);
        TextView titleView = actionView.findViewById(R.id.txtActionTitle);

        iconContainer.setBackgroundResource(backgroundRes);
        icon.setImageResource(iconRes);
        titleView.setText(title);

        actionView.setOnClickListener(v ->
                Toast.makeText(requireContext(), R.string.feature_coming_soon, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        actionListBinding = null;
    }
}

