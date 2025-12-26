package com.kidsapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Model cho câu hỏi từ API
 */
public class QuestionResponse {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("questionText")
    private String questionText;
    
    @SerializedName("questionType")
    private String questionType;
    
    @SerializedName("imageUrl")
    private String imageUrl;
    
    @SerializedName("audioUrl")
    private String audioUrl;
    
    @SerializedName("explanation")
    private String explanation;
    
    @SerializedName("pointsValue")
    private Integer pointsValue;
    
    @SerializedName("orderIndex")
    private Integer orderIndex;
    
    @SerializedName("answerOptions")
    private List<AnswerOptionResponse> answerOptions;

    // Getters
    public String getId() { return id; }
    public String getQuestionText() { return questionText; }
    public String getQuestionType() { return questionType; }
    public String getImageUrl() { return imageUrl; }
    public String getAudioUrl() { return audioUrl; }
    public String getExplanation() { return explanation; }
    public Integer getPointsValue() { return pointsValue; }
    public Integer getOrderIndex() { return orderIndex; }
    public List<AnswerOptionResponse> getAnswerOptions() { return answerOptions; }
}
