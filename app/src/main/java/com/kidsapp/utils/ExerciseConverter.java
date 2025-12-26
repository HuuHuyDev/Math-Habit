package com.kidsapp.utils;

import com.kidsapp.R;
import com.kidsapp.data.model.AnswerOption;
import com.kidsapp.data.model.AnswerOptionResponse;
import com.kidsapp.data.model.Question;
import com.kidsapp.data.model.QuestionResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class để convert giữa API models và UI models
 */
public class ExerciseConverter {

    // Map để lưu correctIndex của mỗi câu hỏi (questionId -> correctIndex)
    private static final Map<String, Integer> correctAnswerMap = new HashMap<>();
    
    // Map để lưu optionId của mỗi câu hỏi (questionId -> List<optionId>)
    private static final Map<String, List<String>> optionIdMap = new HashMap<>();

    /**
     * Convert QuestionResponse (từ API) sang Question (UI model)
     */
    public static Question convertToQuestion(QuestionResponse response) {
        if (response == null) return null;

        // Convert answer options
        List<AnswerOption> options = new ArrayList<>();
        List<String> optionIds = new ArrayList<>();
        int correctIndex = -1;

        if (response.getAnswerOptions() != null) {
            // Tạo labels A, B, C, D...
            String[] labels = {"A", "B", "C", "D", "E", "F"};
            
            for (int i = 0; i < response.getAnswerOptions().size(); i++) {
                AnswerOptionResponse optionResponse = response.getAnswerOptions().get(i);
                String label = i < labels.length ? labels[i] : String.valueOf(i + 1);
                
                // Tạo AnswerOption cho UI
                options.add(new AnswerOption(label, optionResponse.getOptionText()));
                
                // Lưu optionId để submit sau này
                optionIds.add(optionResponse.getId());
            }
        }

        // Lưu optionIds vào map để dùng khi submit
        optionIdMap.put(response.getId(), optionIds);

        // Note: Backend không trả về correctIndex trong response
        // Chỉ biết đúng/sai khi submit
        // Tạm thời set correctIndex = -1, sẽ update sau khi submit
        correctIndex = -1;
        
        // Lưu correctIndex vào map (sẽ update sau khi submit)
        correctAnswerMap.put(response.getId(), correctIndex);

        // Tạo Question object
        return new Question(
            response.getId(),
            response.getQuestionText(),
            options,
            correctIndex,
            response.getExplanation() != null ? response.getExplanation() : ""
        );
    }

    /**
     * Convert list QuestionResponse sang list Question
     */
    public static List<Question> convertToQuestions(List<QuestionResponse> responses) {
        List<Question> questions = new ArrayList<>();
        if (responses != null) {
            for (QuestionResponse response : responses) {
                Question question = convertToQuestion(response);
                if (question != null) {
                    questions.add(question);
                }
            }
        }
        return questions;
    }

    /**
     * Lấy optionId từ questionId và selectedIndex
     * Dùng khi submit answer
     */
    public static String getOptionId(String questionId, int selectedIndex) {
        List<String> optionIds = optionIdMap.get(questionId);
        if (optionIds != null && selectedIndex >= 0 && selectedIndex < optionIds.size()) {
            return optionIds.get(selectedIndex);
        }
        return null;
    }

    /**
     * Update correctIndex cho question sau khi nhận kết quả từ backend
     */
    public static void updateCorrectIndex(String questionId, int correctIndex) {
        correctAnswerMap.put(questionId, correctIndex);
    }

    /**
     * Lấy correctIndex đã lưu
     */
    public static int getCorrectIndex(String questionId) {
        Integer index = correctAnswerMap.get(questionId);
        return index != null ? index : -1;
    }

    /**
     * Clear cache khi không cần nữa
     */
    public static void clearCache() {
        correctAnswerMap.clear();
        optionIdMap.clear();
    }

    /**
     * Convert ExerciseContent từ API sang UI model (ExerciseContent cũ)
     * Dùng cho ExerciseContentAdapter
     */
    public static com.kidsapp.ui.child.task.exercise.ExerciseContent convertToUIExerciseContent(
            com.kidsapp.data.model.ExerciseContent apiModel) {
        
        if (apiModel == null) return null;

        // Chọn icon dựa trên subject hoặc topic
        int iconRes = getIconForExercise(apiModel.getSubject(), apiModel.getTopic());

        return new com.kidsapp.ui.child.task.exercise.ExerciseContent(
            apiModel.getId(),
            apiModel.getTitle(),
            apiModel.getDescription() != null ? apiModel.getDescription() : "",
            apiModel.getTotalQuestions() != null ? apiModel.getTotalQuestions() : 0,
            apiModel.getEstimatedTimeMinutes() != null ? apiModel.getEstimatedTimeMinutes() : 10,
            iconRes,
            apiModel.getIsUnlocked() != null ? apiModel.getIsUnlocked() : true,
            apiModel.getCompletedQuestions() != null ? apiModel.getCompletedQuestions() : 0
        );
    }

    /**
     * Convert list ExerciseContent từ API sang UI model
     */
    public static List<com.kidsapp.ui.child.task.exercise.ExerciseContent> convertToUIExerciseContents(
            List<com.kidsapp.data.model.ExerciseContent> apiModels) {
        
        List<com.kidsapp.ui.child.task.exercise.ExerciseContent> uiModels = new ArrayList<>();
        if (apiModels != null) {
            for (com.kidsapp.data.model.ExerciseContent apiModel : apiModels) {
                com.kidsapp.ui.child.task.exercise.ExerciseContent uiModel = 
                    convertToUIExerciseContent(apiModel);
                if (uiModel != null) {
                    uiModels.add(uiModel);
                }
            }
        }
        return uiModels;
    }

    /**
     * Chọn icon phù hợp dựa trên subject/topic
     */
    private static int getIconForExercise(String subject, String topic) {
        // Default icon
        int iconRes = R.drawable.ic_task;

        if (subject != null) {
            switch (subject.toLowerCase()) {
                case "math":
                case "toán":
                case "toán học":
                    iconRes = R.drawable.ic_task;
                    break;
                case "reading":
                case "đọc":
                    iconRes = R.drawable.ic_book;
                    break;
                default:
                    iconRes = R.drawable.ic_task;
            }
        }

        // Override bằng topic nếu có
        if (topic != null) {
            String topicLower = topic.toLowerCase();
            if (topicLower.contains("tốc độ") || topicLower.contains("speed") || 
                topicLower.contains("time") || topicLower.contains("thời gian")) {
                iconRes = R.drawable.ic_clock;
            } else if (topicLower.contains("minh họa") || topicLower.contains("story") || 
                       topicLower.contains("word problem")) {
                iconRes = R.drawable.ic_book;
            }
        }

        return iconRes;
    }
}
