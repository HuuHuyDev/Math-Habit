package com.kidsapp.ui.child.quizz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.kidsapp.R;
import com.kidsapp.databinding.FragmentExamResultBinding;

import java.util.ArrayList;

public class ExamResultFragment extends Fragment {

    private static final String ARG_CORRECT = "arg_correct";
    private static final String ARG_WRONG = "arg_wrong";
    private static final String ARG_TOTAL = "arg_total";
    private static final String ARG_PERCENT = "arg_percent";
    private static final String ARG_TIMEOUT = "arg_timeout";
    private static final String ARG_WRONG_TITLES = "arg_wrong_titles";

    private FragmentExamResultBinding binding;

    public static ExamResultFragment newInstance(int correct, int wrong, int total, int percent,
                                                 @NonNull ArrayList<String> wrongTitles,
                                                 boolean isTimeout) {
        Bundle args = new Bundle();
        args.putInt(ARG_CORRECT, correct);
        args.putInt(ARG_WRONG, wrong);
        args.putInt(ARG_TOTAL, total);
        args.putInt(ARG_PERCENT, percent);
        args.putBoolean(ARG_TIMEOUT, isTimeout);
        args.putStringArrayList(ARG_WRONG_TITLES, wrongTitles);
        ExamResultFragment fragment = new ExamResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentExamResultBinding.inflate(inflater, container, false);
        setupCloseAction();
        bindSummary();
        return binding.getRoot();
    }

    private void setupCloseAction() {
        binding.btnCloseResult.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void bindSummary() {
        Bundle args = requireArguments();
        int correct = args.getInt(ARG_CORRECT);
        int wrong = args.getInt(ARG_WRONG);
        int total = args.getInt(ARG_TOTAL);
        int percent = args.getInt(ARG_PERCENT);
        boolean isTimeout = args.getBoolean(ARG_TIMEOUT);
        ArrayList<String> wrongTitles = args.getStringArrayList(ARG_WRONG_TITLES);

        binding.txtScorePercent.setText(getString(R.string.percent_value, percent));
        binding.txtCorrectValue.setText(String.valueOf(correct));
        binding.txtWrongValue.setText(String.valueOf(wrong));
        binding.txtTotalValue.setText(String.valueOf(total));

        binding.txtWrongTitle.setVisibility(wrong == 0 ? View.GONE : View.VISIBLE);
        binding.cardWrongList.setVisibility(wrong == 0 ? View.GONE : View.VISIBLE);

        if (isTimeout) {
            binding.txtResultTitle.setText(R.string.exam_time_up);
        }

        binding.layoutWrongQuestions.removeAllViews();
        if (wrongTitles != null && !wrongTitles.isEmpty()) {
            for (String title : wrongTitles) {
                Chip chip = createWrongChip(title);
                binding.layoutWrongQuestions.addView(chip);
            }
        } else if (wrong == 0) {
            TextView textView = new TextView(requireContext());
            textView.setText(R.string.exam_all_correct);
            textView.setTextColor(getResources().getColor(R.color.status_success));
            textView.setTextSize(16f);
            binding.layoutWrongQuestions.addView(textView);
        }
    }

    private Chip createWrongChip(String title) {
        Chip chip = new Chip(requireContext());
        chip.setText(title);
        chip.setChipBackgroundColorResource(R.color.surface_variant);
        chip.setTextColor(getResources().getColor(R.color.text_primary));
        chip.setChipStrokeColorResource(R.color.border);
        chip.setChipStrokeWidth(2f);
        chip.setRippleColorResource(R.color.secondary);
        chip.setClickable(false);
        return chip;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

