package com.kidsapp.ui.child.detailTask;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.R;
import com.kidsapp.databinding.FragmentDetailTaskBinding;
import com.kidsapp.ui.child.practice.PracticeFragment;

import java.util.ArrayList;
import java.util.List;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailTaskFragment extends Fragment {
    private static final String ARG_TASK_ID = "arg_task_id";

    private FragmentDetailTaskBinding binding;
    private String taskId;
    private DetailTaskAdapter stepAdapter;

    public interface OnStartTaskClickListener {
        void onStartTask(@NonNull String taskId, @NonNull TaskMode mode);
    }

    public enum TaskMode {
        PRACTICE,
        START
    }

    private OnStartTaskClickListener startTaskClickListener;


    public void setOnStartTaskClickListener(OnStartTaskClickListener listener) {
        this.startTaskClickListener = listener;
    }


    public static DetailTaskFragment newInstance(@NonNull String taskId) {
        DetailTaskFragment fragment = new DetailTaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TASK_ID, taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskId = getArguments().getString(ARG_TASK_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailTaskBinding.inflate(inflater, container, false);
        setupHeader();
        bindTaskInfo();
        setupStepsRecycler();
        setupStartButton();
        setupBackButton();
        return binding.getRoot();
    }

    private void setupHeader() {
        binding.headerUser.setUserName("Hồ Hữu Huy");
        binding.headerUser.setAvatar(R.drawable.ic_user_default);
        binding.headerUser.setNotificationClick(v ->
                Toast.makeText(requireContext(),
                        R.string.feature_coming_soon,
                        Toast.LENGTH_SHORT).show());
    }

    private void setupBackButton() {
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed(); // Quay lại Fragment trước đó
        });
    }

    /**
     * Ở đây tạm binding data mẫu.
     * Sau này bạn có thể lấy Task theo taskId từ ViewModel / Repository.
     */
    private void bindTaskInfo() {
        // Tiêu đề màn hình
        binding.txtScreenTitle.setText("Chi tiết bài tập");

        // Thông tin bài tập (mock data)
        binding.txtTaskTitle.setText("Bài 1: Luyện phép cộng");
        binding.txtTaskDescription.setText(
                "Trong bài này, bé sẽ luyện 10 phép cộng cơ bản để cộng số nhanh.");
        binding.txtQuestionCount.setText("10 câu hỏi");
        binding.txtDuration.setText("Thời gian dự kiến: 15 phút");
        binding.txtDifficulty.setText("Độ khó: Dễ");
        binding.txtRating.setText("4.8");

        // Phần thưởng
        binding.txtRewardCoin.setText("+20 Coin");
        binding.txtRewardXp.setText("+30 XP");

        // Tiến độ
        binding.progressChild.setMax(10);
        binding.progressChild.setProgress(4);
        binding.txtProgressLabel.setText("Tiến độ: 4/10 câu hoàn thành");

        // Ghi chú
        binding.txtNote.setText("Bé có thể làm lại sau khi hoàn tất tất cả nội dung số!");
    }

    private void setupStepsRecycler() {
        List<Step> steps = createSampleSteps();

        stepAdapter = new DetailTaskAdapter(steps);
        binding.recyclerSteps.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerSteps.setAdapter(stepAdapter);
    }

    private List<Step> createSampleSteps() {
        List<Step> steps = new ArrayList<>();
        steps.add(new Step(1, "Phép cộng 1 chữ số"));
        steps.add(new Step(2, "Phép cộng 2 chữ số"));
        steps.add(new Step(3, "Bài toán minh hoạ"));
        steps.add(new Step(4, "Kiểm tra tốc độ"));
        steps.add(new Step(5, "Phép cộng có nhớ"));
        steps.add(new Step(6, "Bài tập thực hành"));
        steps.add(new Step(7, "Đố vui toán học"));
        steps.add(new Step(8, "Tính nhanh"));
        steps.add(new Step(9, "Bài toán ứng dụng"));
        steps.add(new Step(10, "Tổng kết kiến thức"));
        return steps;
    }

    private void setupStartButton() {
        binding.btnStartTask.setOnClickListener(v -> showChooseModeDialog());
    }

    private void showChooseModeDialog() {
        ChooseModeBottomSheet bottomSheet = new ChooseModeBottomSheet();
        bottomSheet.setModeListener(this::handleStartMode);
        bottomSheet.show(getChildFragmentManager(), "ChooseModeBottomSheet");
    }

    private void handleStartMode(@NonNull TaskMode mode) {
        if (mode == TaskMode.PRACTICE) {
            openPracticeFragment();
            return;
        }
        if (startTaskClickListener != null && taskId != null) {
            startTaskClickListener.onStartTask(taskId, mode);
        } else {
            String action = mode == TaskMode.PRACTICE ? "Ôn tập" : "Bắt đầu làm bài";
            Toast.makeText(requireContext(),
                    action + ": " + (taskId != null ? taskId : ""),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void openPracticeFragment() {
        if (getActivity() == null) {
            return;
        }
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.childHomeHost, new PracticeFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}