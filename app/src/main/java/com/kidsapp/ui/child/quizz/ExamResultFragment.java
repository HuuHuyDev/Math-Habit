package com.kidsapp.ui.child.quizz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kidsapp.R;
import com.kidsapp.databinding.FragmentExamResultBinding;
import com.kidsapp.ui.child.home.ChildHomeFragment;
import com.kidsapp.ui.child.quizz.ExamFragment;

import java.util.ArrayList;

public class ExamResultFragment extends Fragment {

    private static final String ARG_CORRECT = "arg_correct";
    private static final String ARG_WRONG = "arg_wrong";
    private static final String ARG_TOTAL = "arg_total";
    private static final String ARG_PERCENT = "arg_percent";
    private static final String ARG_TIMEOUT = "arg_timeout";
    private static final String ARG_WRONG_TITLES = "arg_wrong_titles";
    private static final String ARG_WRONG_QUESTIONS = "arg_wrong_questions";
    private static final String ARG_CONTENT_ID = "arg_content_id";
    private static final String ARG_CONTENT_TITLE = "arg_content_title";

    private FragmentExamResultBinding binding;
    private int ratingStars = 4; // Mặc định 4 sao
    private ArrayList<com.kidsapp.data.model.Question> wrongQuestions;
    private String contentId;
    private String contentTitle;

    public static ExamResultFragment newInstance(int correct, int wrong, int total, int percent,
                                                 @NonNull ArrayList<String> wrongTitles,
                                                 boolean isTimeout,
                                                 @NonNull ArrayList<com.kidsapp.data.model.Question> wrongQuestions) {
        return newInstance(correct, wrong, total, percent, wrongTitles, isTimeout, wrongQuestions, "", "");
    }

    public static ExamResultFragment newInstance(int correct, int wrong, int total, int percent,
                                                 @NonNull ArrayList<String> wrongTitles,
                                                 boolean isTimeout,
                                                 @NonNull ArrayList<com.kidsapp.data.model.Question> wrongQuestions,
                                                 String contentId,
                                                 String contentTitle) {
        Bundle args = new Bundle();
        args.putInt(ARG_CORRECT, correct);
        args.putInt(ARG_WRONG, wrong);
        args.putInt(ARG_TOTAL, total);
        args.putInt(ARG_PERCENT, percent);
        args.putBoolean(ARG_TIMEOUT, isTimeout);
        args.putStringArrayList(ARG_WRONG_TITLES, wrongTitles);
        args.putSerializable(ARG_WRONG_QUESTIONS, wrongQuestions);
        args.putString(ARG_CONTENT_ID, contentId);
        args.putString(ARG_CONTENT_TITLE, contentTitle);
        ExamResultFragment fragment = new ExamResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentExamResultBinding.inflate(inflater, container, false);
        setupActions();
        bindSummary();
        setupRatingStars();
        return binding.getRoot();
    }

    private void setupActions() {
        // Nút Back/Close
        binding.btnCloseResult.setOnClickListener(v -> requireActivity().onBackPressed());

        // Nút Xem lại bài
        binding.btnReviewLesson.setOnClickListener(v -> {
            if (wrongQuestions != null && !wrongQuestions.isEmpty()) {
                // Chuyển sang trang xem lại câu sai
                ExamReviewFragment reviewFragment = ExamReviewFragment.newInstance(wrongQuestions);
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.childHomeHost, reviewFragment)
                            .addToBackStack(null)
                            .commit();
                }
            } else {
                Toast.makeText(requireContext(), "Bạn đã làm đúng tất cả! Không có câu nào để xem lại.", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Làm lại bài
        binding.btnDoAgain.setOnClickListener(v -> {
            if (getActivity() != null) {
                ExamFragment examFragment;
                if (contentId != null && !contentId.isEmpty()) {
                    // Làm lại bài với cùng nội dung
                    examFragment = ExamFragment.newInstance(contentId, contentTitle);
                } else {
                    // Làm lại bài mặc định
                    examFragment = new ExamFragment();
                }
                
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.childHomeHost, examFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Nút Về trang chính
        binding.btnGoHome.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.childHomeHost, new ChildHomeFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void bindSummary() {
        Bundle args = requireArguments();
        int correct = args.getInt(ARG_CORRECT);
        int wrong = args.getInt(ARG_WRONG);
        int total = args.getInt(ARG_TOTAL);
        int percent = args.getInt(ARG_PERCENT);
        boolean isTimeout = args.getBoolean(ARG_TIMEOUT);
        wrongQuestions = (ArrayList<com.kidsapp.data.model.Question>) args.getSerializable(ARG_WRONG_QUESTIONS);
        contentId = args.getString(ARG_CONTENT_ID, "");
        contentTitle = args.getString(ARG_CONTENT_TITLE, "");

        // Hiển thị % điểm
        binding.txtScorePercent.setText(getString(R.string.percent_value, percent));

        // Hiển thị số đúng/sai/tổng
        binding.txtCorrectValue.setText(String.valueOf(correct));
        binding.txtWrongValue.setText(String.valueOf(wrong));
        binding.txtTotalValue.setText(String.valueOf(total));

        // Cập nhật text chúc mừng nếu hết giờ
        if (isTimeout) {
            binding.txtResultTitle.setText(R.string.exam_time_up);
            binding.txtCongratulations.setText(R.string.exam_time_up);
            binding.txtWellDone.setText(getString(R.string.exam_result_title));
        } else {
            binding.txtResultTitle.setText(R.string.exam_result_title);
            binding.txtCongratulations.setText(R.string.result_congratulations);
            binding.txtWellDone.setText(R.string.result_well_done);
        }

        // Tính số sao dựa trên % điểm
        calculateRating(percent);
    }

    private void calculateRating(int percent) {
        // Tính số sao dựa trên % điểm
        if (percent >= 90) {
            ratingStars = 5;
        } else if (percent >= 70) {
            ratingStars = 4;
        } else if (percent >= 50) {
            ratingStars = 3;
        } else if (percent >= 30) {
            ratingStars = 2;
        } else {
            ratingStars = 1;
        }
        updateRatingStars();
    }

    private void setupRatingStars() {
        // Setup click listeners cho các sao (optional - có thể cho phép user chọn)
        ImageView[] stars = {
                binding.star1,
                binding.star2,
                binding.star3,
                binding.star4,
                binding.star5
        };

        for (int i = 0; i < stars.length; i++) {
            final int starIndex = i + 1;
            stars[i].setOnClickListener(v -> {
                ratingStars = starIndex;
                updateRatingStars();
            });
        }

        updateRatingStars();
    }

    private void updateRatingStars() {
        ImageView[] stars = {
                binding.star1,
                binding.star2,
                binding.star3,
                binding.star4,
                binding.star5
        };

        for (int i = 0; i < stars.length; i++) {
            if (i < ratingStars) {
                stars[i].setImageResource(R.drawable.ic_star_filled);
            } else {
                stars[i].setImageResource(R.drawable.ic_star_outline);
            }
        }

        // Cập nhật hint text dựa trên số sao
        if (ratingStars == 5) {
            binding.txtRatingHint.setText(getString(R.string.result_rating_hint).replace("5 sao", "giữ 5 sao"));
        } else {
            binding.txtRatingHint.setText(R.string.result_rating_hint);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
