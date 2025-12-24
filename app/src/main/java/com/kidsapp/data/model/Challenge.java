package com.kidsapp.data.model;

public class Challenge {
    private String id;
    private String challengeCode;
    private String creatorId;
    private String creatorName;
    private String opponentId;
    private String opponentName;
    private int questionCount; // 5, 10, 15
    private int timePerQuestion; // 15, 20 seconds
    private String topic; // "fun", "logic", "folk"
    private String difficulty; // "easy", "medium"
    private String status; // "waiting", "in_progress", "completed"
    private long createdAt;

    public Challenge() {}
    
    public Challenge(String id, String challengeCode, String creatorId, String creatorName,
                    int questionCount, int timePerQuestion, String topic, String difficulty) {
        this.id = id;
        this.challengeCode = challengeCode;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.questionCount = questionCount;
        this.timePerQuestion = timePerQuestion;
        this.topic = topic;
        this.difficulty = difficulty;
        this.status = "waiting";
        this.createdAt = System.currentTimeMillis();
    }

    // Getters
    public String getId() { return id; }
    public String getChallengeCode() { return challengeCode; }
    public String getCreatorId() { return creatorId; }
    public String getCreatorName() { return creatorName; }
    public String getOpponentId() { return opponentId; }
    public String getOpponentName() { return opponentName; }
    public int getQuestionCount() { return questionCount; }
    public int getTimePerQuestion() { return timePerQuestion; }
    public String getTopic() { return topic; }
    public String getDifficulty() { return difficulty; }
    public String getStatus() { return status; }
    public long getCreatedAt() { return createdAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setChallengeCode(String challengeCode) { this.challengeCode = challengeCode; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    public void setOpponentId(String opponentId) { this.opponentId = opponentId; }
    public void setOpponentName(String opponentName) { this.opponentName = opponentName; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }
    public void setTimePerQuestion(int timePerQuestion) { this.timePerQuestion = timePerQuestion; }
    public void setTopic(String topic) { this.topic = topic; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
