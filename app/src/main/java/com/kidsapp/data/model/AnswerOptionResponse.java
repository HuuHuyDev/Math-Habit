package com.kidsapp.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model cho lựa chọn đáp án từ API
 */
public class AnswerOptionResponse {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("optionText")
    private String optionText;
    
    @SerializedName("imageUrl")
    private String imageUrl;
    
    @SerializedName("orderIndex")
    private Integer orderIndex;

    // Getters
    public String getId() { return id; }
    public String getOptionText() { return optionText; }
    public String getImageUrl() { return imageUrl; }
    public Integer getOrderIndex() { return orderIndex; }
}
