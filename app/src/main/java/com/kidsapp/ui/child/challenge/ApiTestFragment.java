package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kidsapp.R;
import com.kidsapp.data.repository.ChallengeRepository;
import com.kidsapp.data.local.SharedPref;

import java.util.List;

/**
 * Fragment để test API connection và debug issues
 */
public class ApiTestFragment extends Fragment {

    private TextView txtResults;
    private Button btnTestConnection;
    private Button btnTestQuestions;
    private ChallengeRepository repository;
    private SharedPref sharedPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_api_test, container, false);
        
        txtResults = view.findViewById(R.id.txtResults);
        btnTestConnection = view.findViewById(R.id.btnTestConnection);
        btnTestQuestions = view.findViewById(R.id.btnTestQuestions);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        repository = new ChallengeRepository(requireContext());
        sharedPref = new SharedPref(requireContext());
        
        setupViews();
        showCurrentConfig();
    }

    private void setupViews() {
        btnTestConnection.setOnClickListener(v -> testConnection());
        btnTestQuestions.setOnClickListener(v -> testQuestions());
    }

    private void showCurrentConfig() {
        StringBuilder config = new StringBuilder();
        config.append("=== CURRENT CONFIG ===\n");
        config.append("Base URL: ").append(com.kidsapp.utils.Constants.BASE_URL).append("\n");
        config.append("Token: ").append(sharedPref.getAuthToken() != null ? "EXISTS" : "NULL").append("\n");
        config.append("ChildId: ").append(sharedPref.getChildId() != null ? sharedPref.getChildId() : "NULL").append("\n");
        config.append("UserId: ").append(sharedPref.getUserId() != null ? sharedPref.getUserId() : "NULL").append("\n");
        config.append("UserEmail: ").append(sharedPref.getUserEmail() != null ? sharedPref.getUserEmail() : "NULL").append("\n");
        config.append("IsLoggedIn: ").append(sharedPref.isLoggedIn()).append("\n");
        config.append("UserRole: ").append(sharedPref.getUserRole() != null ? sharedPref.getUserRole() : "NULL").append("\n");
        config.append("\n");
        
        txtResults.setText(config.toString());
    }

    private void testConnection() {
        appendResult("=== TESTING CONNECTION ===");
        appendResult("Loading challenge categories...");
        
        repository.getChallengeCategories(new ChallengeRepository.ResultCallback<List<com.kidsapp.data.model.Category>>() {
            @Override
            public void onSuccess(List<com.kidsapp.data.model.Category> categories) {
                appendResult("✅ SUCCESS: Loaded " + categories.size() + " categories");
                for (com.kidsapp.data.model.Category category : categories) {
                    appendResult("  - " + category.getName() + " (ID: " + category.getId() + ")");
                }
                Toast.makeText(requireContext(), "Connection test successful!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                appendResult("❌ ERROR: " + error);
                Toast.makeText(requireContext(), "Connection failed: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void testQuestions() {
        appendResult("=== TESTING QUESTIONS ===");
        
        // Test with a sample challenge ID - we need to create one first
        String testChallengeId = "test-challenge-id";
        appendResult("Testing with challengeId: " + testChallengeId);
        
        repository.getChallengeQuestions(testChallengeId, new ChallengeRepository.ResultCallback<List<com.kidsapp.data.response.QuestionResponse>>() {
            @Override
            public void onSuccess(List<com.kidsapp.data.response.QuestionResponse> questions) {
                appendResult("✅ SUCCESS: Loaded " + questions.size() + " questions");
                if (!questions.isEmpty()) {
                    com.kidsapp.data.response.QuestionResponse firstQ = questions.get(0);
                    appendResult("First question: " + firstQ.getQuestionText());
                    appendResult("Options: " + (firstQ.getOptions() != null ? firstQ.getOptions().size() : 0));
                }
                Toast.makeText(requireContext(), "Questions test successful!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                appendResult("❌ ERROR: " + error);
                Toast.makeText(requireContext(), "Questions test failed: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void appendResult(String text) {
        if (txtResults != null) {
            String current = txtResults.getText().toString();
            txtResults.setText(current + text + "\n");
        }
        android.util.Log.d("ApiTestFragment", text);
    }
}