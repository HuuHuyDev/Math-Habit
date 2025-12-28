package com.kidsapp.data.response;

/**
 * Response trạng thái hàng đợi (dùng cho polling)
 */
public class QueueResponse {
    private String queueId;
    private String childId;
    private String status; // WAITING, MATCHED, EXPIRED, CANCELLED
    private String challengeId;
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

    public Long getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(Long remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }
}
