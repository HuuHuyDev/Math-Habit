package com.kidsapp.ui.child.review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.data.model.Question;
import com.kidsapp.databinding.FragmentReviewAnswersBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fragment để xem lại tất cả câu hỏi và đáp án sau khi nộp bài
 */
public class ReviewAnswersFragment extends Fragment {

    private FragmentReviewAnswersBinding binding;
    private ReviewAnswersAdapter adapter;
    private List<Question> questions;
    private Map<String, Integer> selectedAnswers;

    private static final String ARG_QUESTIONS = "questions";
    private static final String ARG_SELECTED_ANSWERS = "selected_answers";

    public static ReviewAnswersFragment newInstance(ArrayList<Question> questions, 
                                                     Map<String, Integer> selectedAnswers) {
        ReviewAnswersFragment fragment = new ReviewAnswersFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_QUESTIONS, questions);
        args.putSerializable(ARG_SELECTED_ANSWERS, (java.io.Serializable) selectedAnswers);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReviewAnswersBinding.inflate(inflater, container, false);
        
        loadArguments();
        setupHeader();
        setupRecyclerView();
        
        return binding.getRoot();
    }

    private void loadArguments() {
        if (getArguments() != null) {
            questions = getArguments().getParcelableArrayList(ARG_QUESTIONS);
            selectedAnswers = (Map<String, Integer>) getArguments().getSerializable(ARG_SELECTED_ANSWERS);
        }
        
        if (questions == null) {
            questions = new ArrayList<>();
        }
    }

    private void setupHeader() {
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        
        // Hiển thị thống kê
        int correctCount = 0;
        for (Question question : questions) {
            Integer selectedIndex = selectedAnswers != null ? selectedAnswers.get(question.getId()) : null;
            if (selectedIndex != null && selectedIndex == question.getCorrectIndex()) {
                correctCount++;
            }
        }
        
        binding.txtStats.setText(String.format("Đúng: %d/%d câu", correctCount, questions.size()));
    }

    private void setupRecyclerView() {
        adapter = new ReviewAnswersAdapter(questions, selectedAnswers);
        binding.recyclerReview.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerReview.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
