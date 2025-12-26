package com.kidsapp.data.request;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Request để submit bài làm
 */
public class SubmitAnswerRequest {
    
    @SerializedName("exerciseId")
    private String exerciseId;
    
    @SerializedName("answers")
    private List<QuestionAnswer> answers;
    
    @SerializedName("timeSpentSeconds")
    private Integer timeSpentSeconds;

    public SubmitAnswerRequest(String exerciseId, List<QuestionAnswer> answers, Integer timeSpentSeconds) {
        this.exerciseId = exerciseId;
        this.answers = answers;
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public static class QuestionAnswer {
        @SerializedName("questionId")
        private String questionId;
        
        @SerializedName("selectedOptionId")
        private String selectedOptionId;

        public QuestionAnswer(String questionId, String selectedOptionId) {
            this.questionId = questionId;
            this.selectedOptionId = selectedOptionId;
        }
    }
}
