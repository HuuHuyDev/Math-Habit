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
import com.kidsapp.ui.child.equip.equip;
import com.kidsapp.ui.child.progress.ProgresssFragment;
import com.kidsapp.ui.child.shop.ShopFragment;
import com.kidsapp.ui.child.task.ChildTaskListFragment;

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
        binding.layoutLevel.setOnClickListener(v -> navigateToProgress());
        
        return binding.getRoot();
    }

    private void setupHeader() {
        binding.headerUser.setUserName("Hồ Hữu Huy");
        binding.headerUser.setAvatar(R.drawable.ic_user_default);
        
        // CLICK VÀO AVATAR → ĐI TỚI TRANG PROFILE
        binding.headerUser.setAvatarClick(v -> navigateToProfile());

        binding.headerUser.setOnClickListener(v -> navigateToEquip());
        // CLICK VÀO CHUÔNG → HIỆN / ẨN CARD THÔNG BÁO NHỎ
        binding.headerUser.setNotificationClick(v -> {
            if (binding.cardNotification.getVisibility() == View.VISIBLE) {
                binding.cardNotification.setVisibility(View.GONE);
            } else {
                binding.cardNotification.setAlpha(0f);
                binding.cardNotification.setScaleX(0.9f);
                binding.cardNotification.setScaleY(0.9f);
                binding.cardNotification.setVisibility(View.VISIBLE);

                binding.cardNotification.animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(200)
                        .start();

                binding.contentScroll.post(() ->
                        binding.contentScroll.smoothScrollTo(0, 0));
            }
        });
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
        // Xem nhiệm vụ - chuyển đến ChildTaskListFragment
        configureAction(actionListBinding.actionMission.getRoot(),
                R.drawable.bg_action_blue,
                R.drawable.ic_task,
                getString(R.string.child_action_mission),
                () -> navigateToTaskList());
        // Mua vật phẩm - chuyển đến ShopFragment
        configureAction(actionListBinding.actionStore.getRoot(),
                R.drawable.bg_action_purple,
                R.drawable.ic_store,
                getString(R.string.child_action_store),
                () -> navigateToShop());

        // Thành tựu - chuyển đến RewardFragment (Achievement)
        configureAction(actionListBinding.actionAchievement.getRoot(),
                R.drawable.bg_action_orange,
                R.drawable.ic_trophy,
                getString(R.string.child_action_achievement),
                () -> navigateToAchievement());
    }

    private void configureAction(View actionView, int backgroundRes, int iconRes, String title, Runnable onClick) {
        FrameLayout iconContainer = actionView.findViewById(R.id.iconContainer);
        ImageView icon = actionView.findViewById(R.id.imgIcon);
        TextView titleView = actionView.findViewById(R.id.txtActionTitle);

        iconContainer.setBackgroundResource(backgroundRes);
        icon.setImageResource(iconRes);
        titleView.setText(title);

        actionView.setOnClickListener(v -> onClick.run());
    }

    private void navigateToTaskList() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.childHomeHost, new ChildTaskListFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToShop() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.childHomeHost, new ShopFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }
    private void navigateToProgress() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.childHomeHost, new ProgresssFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToEquip() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.childHomeHost, new equip())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToAchievement() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.childHomeHost, new com.kidsapp.ui.child.reward.RewardFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Chuyển đến trang Profile của Bé
     */
    private void navigateToProfile() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.childHomeHost, new com.kidsapp.ui.child.profile.ChildProfileFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        actionListBinding = null;
    }

}

