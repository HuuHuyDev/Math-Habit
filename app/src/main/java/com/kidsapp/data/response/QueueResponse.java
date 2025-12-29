package com.kidsapp.data.response;

/**
 * Response trạng thái hàng đợi (dùng cho polling)
 */
public class QueueResponse {
    private String queueId;
    private String childId;
    private String childName;
    private String categoryId;
    private String categoryName;
    private Integer difficultyLevel;
    private String status; // WAITING, MATCHED, EXPIRED, CANCELLED
    private String challengeId;
    private String opponentName;
    private String message;
    private String queuedAt;
    private String matchedAt;
    private String expiresAt;
    private Long remainingSeconds;

    public QueueResponse() {}

    // Getters and Setters
    public String getQueueId() {
        return queueId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getQueuedAt() {
        return queuedAt;
    }

    public void setQueuedAt(String queuedAt) {
        this.queuedAt = queuedAt;
    }

    public String getMatchedAt() {
        return matchedAt;
    }

    public void setMatchedAt(String matchedAt) {
        this.matchedAt = matchedAt;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Long getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(Long remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    @Override
    public String toString() {
        return "QueueResponse{" +
                "queueId='" + queueId + '\'' +
                ", childId='" + childId + '\'' +
                ", childName='" + childName + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", difficultyLevel=" + difficultyLevel +
                ", status='" + status + '\'' +
                ", challengeId='" + challengeId + '\'' +
                ", opponentName='" + opponentName + '\'' +
                ", message='" + message + '\'' +
                ", queuedAt='" + queuedAt + '\'' +
                ", matchedAt='" + matchedAt + '\'' +
                ", expiresAt='" + expiresAt + '\'' +
                ", remainingSeconds=" + remainingSeconds +
                '}';
    }
}
