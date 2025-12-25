package com.kidsapp.ui.child.task.exercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.R;
import com.kidsapp.data.model.ComprehensiveTest;
import com.kidsapp.data.repository.ComprehensiveTestRepository;
import com.kidsapp.databinding.FragmentExerciseContentBinding;
import com.kidsapp.databinding.CardComprehensiveTestBinding;
import com.kidsapp.ui.child.practice.PracticeFragment;
import com.kidsapp.ui.child.quizz.ExamFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment để chọn nội dung bài tập cụ thể
 * Hiển thị danh sách các chủ đề: Phép cộng 1 chữ số, Phép cộng 2 chữ số, v.v.
 */
public class ExerciseContentFragment extends Fragment {

    private FragmentExerciseContentBinding binding;
    private CardComprehensiveTestBinding comprehensiveTestBinding;
    private ExerciseContentAdapter adapter;
    private String taskTitle = "Bài 1: Luyện phép cộng";
    private ComprehensiveTestRepository comprehensiveTestRepository;

    public static ExerciseContentFragment newInstance(String taskTitle) {
        ExerciseContentFragment fragment = new ExerciseContentFragment();
        Bundle args = new Bundle();
        args.putString("task_title", taskTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentExerciseContentBinding.inflate(inflater, container, false);
        comprehensiveTestBinding = CardComprehensiveTestBinding.bind(binding.cardComprehensiveTest.getRoot());
        comprehensiveTestRepository = ComprehensiveTestRepository.getInstance();
        
        loadArguments();
        setupHeader();
        setupComprehensiveTest();
        setupRecyclerView();
        
        return binding.getRoot();
    }

    private void loadArguments() {
        if (getArguments() != null) {
            taskTitle = getArguments().getString("task_title", "Bài tập");
        }
    }

    private void setupHeader() {
        binding.txtTitle.setText(taskTitle);
        binding.txtSubtitle.setText("Chọn nội dung bạn muốn luyện tập");
        
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupRecyclerView() {
        List<ExerciseContent> contents = createSampleContents();
        
        adapter = new ExerciseContentAdapter(contents, this::onContentClick);
        binding.recyclerContents.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerContents.setAdapter(adapter);
    }

    /**
     * Thiết lập card Làm bài test tổng
     */
    private void setupComprehensiveTest() {
        // Lấy thông tin bài test tổng từ repository
        ComprehensiveTest comprehensiveTest = comprehensiveTestRepository.getComprehensiveTest(taskTitle);
        
        // Hiển thị thông tin
        comprehensiveTestBinding.txtTestTitle.setText(comprehensiveTest.getTitle());
        comprehensiveTestBinding.txtTestDescription.setText(comprehensiveTest.getDescription());
        comprehensiveTestBinding.txtQuestionCount.setText(comprehensiveTest.getTotalQuestions() + " câu");
        comprehensiveTestBinding.txtDuration.setText(comprehensiveTest.getDuration() + " phút");
        comprehensiveTestBinding.txtPassingScore.setText("Đạt " + comprehensiveTest.getPassingScore() + "%");
        
        // Kiểm tra trạng thái khóa/mở
        if (comprehensiveTest.isAvailable()) {
            comprehensiveTestBinding.btnStartTest.setEnabled(true);
            comprehensiveTestBinding.btnStartTest.setAlpha(1.0f);
            comprehensiveTestBinding.layoutLocked.setVisibility(View.GONE);
            
            // Xử lý click nút bắt đầu
            comprehensiveTestBinding.btnStartTest.setOnClickListener(v -> {
                showConfirmDialog(comprehensiveTest);
            });
        } else {
            comprehensiveTestBinding.btnStartTest.setEnabled(false);
            comprehensiveTestBinding.btnStartTest.setAlpha(0.5f);
            comprehensiveTestBinding.layoutLocked.setVisibility(View.VISIBLE);
            comprehensiveTestBinding.txtLockedMessage.setText(comprehensiveTest.getAvailabilityMessage());
        }
    }
    
    /**
     * Hiển thị dialog xác nhận trước khi bắt đầu làm bài
     */
    private void showConfirmDialog(ComprehensiveTest comprehensiveTest) {
        if (getContext() == null) return;
        
        // Inflate custom dialog layout
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_test, null);
        
        // Tạo dialog
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(getContext())
            .setView(dialogView)
            .setCancelable(true)
            .create();
        
        // Set transparent background để bo góc hoạt động
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        
        // Bind data
        TextView txtQuestions = dialogView.findViewById(R.id.txtDialogQuestions);
        TextView txtDuration = dialogView.findViewById(R.id.txtDialogDuration);
        TextView txtPassingScore = dialogView.findViewById(R.id.txtDialogPassingScore);
        
        txtQuestions.setText(comprehensiveTest.getTotalQuestions() + " câu");
        txtDuration.setText(comprehensiveTest.getDuration() + " phút");
        txtPassingScore.setText(comprehensiveTest.getPassingScore() + "%");
        
        // Nút Hủy
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> {
            dialog.dismiss();
        });
        
        // Nút Bắt đầu
        dialogView.findViewById(R.id.btnStart).setOnClickListener(v -> {
            dialog.dismiss();
            startComprehensiveTest(comprehensiveTest);
        });
        
        dialog.show();
    }
    
    /**
     * Bắt đầu làm bài test tổng
     */
    private void startComprehensiveTest(ComprehensiveTest comprehensiveTest) {
        if (getActivity() != null) {
            // Tạo ExamFragment với ID đặc biệt cho comprehensive test
            ExamFragment fragment = ExamFragment.newInstance(
                comprehensiveTest.getId(),
                comprehensiveTest.getTitle()
            );
            
            getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.childHomeHost, fragment)
                .addToBackStack(null)
                .commit();
        }
    }

    private List<ExerciseContent> createSampleContents() {
        List<ExerciseContent> contents = new ArrayList<>();
        
        // Phép cộng 1 chữ số
        contents.add(new ExerciseContent(
            "1",
            "Phép cộng 1 chữ số",
            "Cộng các số từ 0 đến 9",
            10,
            5,
            R.drawable.ic_task,
            true,  // đã mở khóa
            8      // đã làm 8/10 câu
        ));
        
        // Phép cộng 2 chữ số
        contents.add(new ExerciseContent(
            "2",
            "Phép cộng 2 chữ số",
            "Cộng các số từ 10 đến 99",
            15,
            8,
            R.drawable.ic_task,
            true,
            5      // đã làm 5/15 câu
        ));
        
        // Bài toán minh họa
        contents.add(new ExerciseContent(
            "3",
            "Bài toán minh họa",
            "Áp dụng phép cộng vào thực tế",
            12,
            10,
            R.drawable.ic_book,
            true,
            0      // chưa làm
        ));
        
        // Kiểm tra tốc độ
        contents.add(new ExerciseContent(
            "4",
            "Kiểm tra tốc độ",
            "Làm nhanh trong 5 phút",
            20,
            5,
            R.drawable.ic_clock,
            false, // chưa mở khóa
            0
        ));
        
        // Phép cộng có nhớ
        contents.add(new ExerciseContent(
            "5",
            "Phép cộng có nhớ",
            "Cộng các số có nhớ sang hàng chục",
            15,
            10,
            R.drawable.ic_task,
            false, // chưa mở khóa
            0
        ));
        
        return contents;
    }

    private void onContentClick(ExerciseContent content) {
        if (!content.isUnlocked()) {
            android.widget.Toast.makeText(requireContext(), 
                "Hãy hoàn thành nội dung trước đó để mở khóa!", 
                android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Hiển thị dialog chọn chế độ
        showModeDialog(content);
    }

    private void showModeDialog(ExerciseContent content) {
        ExerciseModeBottomSheet bottomSheet = ExerciseModeBottomSheet.newInstance(
            content.getTitle(),
            content.getTotalQuestions(),
            content.getCompletedQuestions()
        );
        
        bottomSheet.setModeListener(mode -> {
            if (mode == ExerciseModeBottomSheet.Mode.PRACTICE) {
                openPractice(content);
            } else if (mode == ExerciseModeBottomSheet.Mode.TEST) {
                openTest(content);
            }
        });
        
        bottomSheet.show(getChildFragmentManager(), "ExerciseModeBottomSheet");
    }

    private void openPractice(ExerciseContent content) {
        if (getActivity() != null) {
            PracticeFragment fragment = PracticeFragment.newInstance(
                content.getId(), 
                content.getTitle()
            );
            getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.childHomeHost, fragment)
                .addToBackStack(null)
                .commit();
        }
    }

    private void openTest(ExerciseContent content) {
        if (getActivity() != null) {
            ExamFragment fragment = ExamFragment.newInstance(
                content.getId(), 
                content.getTitle()
            );
            getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.childHomeHost, fragment)
                .addToBackStack(null)
                .commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}