package com.kidsapp.data.request;

public class CreateChallengeRequest {
    private String childId;
    private String categoryId;
    private String title;
    private String description;
    private Integer difficultyLevel;
    private Integer timeLimitMinutes;
    private Integer totalQuestions;

    public CreateChallengeRequest() {
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(Integer timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    @Override
    public String toString() {
        return "CreateChallengeRequest{" +
                "childId='" + childId + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", difficultyLevel=" + difficultyLevel +
                ", timeLimitMinutes=" + timeLimitMinutes +
                ", totalQuestions=" + totalQuestions +
                '}';
    }
}
