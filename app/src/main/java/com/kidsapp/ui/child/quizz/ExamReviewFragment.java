package com.kidsapp.ui.child.quizz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.R;
import com.kidsapp.data.model.Question;
import com.kidsapp.databinding.FragmentExamReviewBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment hiển thị danh sách câu sai để xem lại
 */
public class ExamReviewFragment extends Fragment {

    private static final String ARG_WRONG_QUESTIONS = "wrong_questions";
    
    private FragmentExamReviewBinding binding;
    private List<Question> wrongQuestions;
    private ExamReviewAdapter adapter;

    public static ExamReviewFragment newInstance(ArrayList<Question> wrongQuestions) {
        ExamReviewFragment fragment = new ExamReviewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_WRONG_QUESTIONS, wrongQuestions);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentExamReviewBinding.inflate(inflater, container, false);
        
        // Nhận danh sách câu sai
        if (getArguments() != null) {
            wrongQuestions = (List<Question>) getArguments().getSerializable(ARG_WRONG_QUESTIONS);
        }
        
        setupHeader();
        setupRecyclerView();
        
        return binding.getRoot();
    }

    private void setupHeader() {
        // Nút Back
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        
        // Hiển thị số câu sai
        if (wrongQuestions != null) {
            binding.txtReviewTitle.setText("Xem lại " + wrongQuestions.size() + " câu sai");
        }
    }

    private void setupRecyclerView() {
        if (wrongQuestions == null || wrongQuestions.isEmpty()) {
            binding.txtEmptyMessage.setVisibility(View.VISIBLE);
            binding.recyclerReview.setVisibility(View.GONE);
            return;
        }
        
        adapter = new ExamReviewAdapter(wrongQuestions);
        binding.recyclerReview.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerReview.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
