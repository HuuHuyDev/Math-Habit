package com.kidsapp.data.request;

/**
 * Request để tham gia hàng đợi thách đấu nhanh
 */
public class JoinQueueRequest {
    private String childId;
    private String categoryId;
    private Integer difficultyLevel;

    public JoinQueueRequest() {}

    public JoinQueueRequest(String childId, String categoryId, Integer difficultyLevel) {
        this.childId = childId;
        this.categoryId = categoryId;
        this.difficultyLevel = difficultyLevel;
    }

    // Getters and Setters
    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
}
