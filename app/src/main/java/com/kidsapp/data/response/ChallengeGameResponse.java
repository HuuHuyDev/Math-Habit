package com.kidsapp.data.response;

/**
 * Response cho trạng thái thách đấu real-time
 */
public class ChallengeGameResponse {
    private String challengeId;
    private String title;
    private String categoryName;
    private Integer difficultyLevel;
    private String status; // ACTIVE, COMPLETED (từ ChallengeStatus enum)
    private Integer totalQuestions;
    private Integer timeLimitMinutes;
    private String startTime;
    private String endTime;
    
    // Thông tin đối thủ
    private OpponentInfo opponent;
    
    // Tiến độ
    private Integer currentQuestionIndex;
    private Integer playerScore;
    private Integer opponentScore;
    private Integer playerCorrectAnswers;
    private Integer opponentCorrectAnswers;
    private Long timeRemaining;
    
    // Trạng thái game
    private Boolean isGameFinished;
    private Boolean isWaiting;
    private String message;

    public ChallengeGameResponse() {}

    // Getters and Setters
    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(Integer timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public OpponentInfo getOpponent() {
        return opponent;
    }

    public void setOpponent(OpponentInfo opponent) {
        this.opponent = opponent;
    }

    public Integer getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void setCurrentQuestionIndex(Integer currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public Integer getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(Integer playerScore) {
        this.playerScore = playerScore;
    }

    public Integer getOpponentScore() {
        return opponentScore;
    }

    public void setOpponentScore(Integer opponentScore) {
        this.opponentScore = opponentScore;
    }

    public Integer getPlayerCorrectAnswers() {
        return playerCorrectAnswers;
    }

    public void setPlayerCorrectAnswers(Integer playerCorrectAnswers) {
        this.playerCorrectAnswers = playerCorrectAnswers;
    }

    public Integer getOpponentCorrectAnswers() {
        return opponentCorrectAnswers;
    }

    public void setOpponentCorrectAnswers(Integer opponentCorrectAnswers) {
        this.opponentCorrectAnswers = opponentCorrectAnswers;
    }

    public Long getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(Long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public Boolean getIsGameFinished() {
        return isGameFinished;
    }

    public void setIsGameFinished(Boolean isGameFinished) {
        this.isGameFinished = isGameFinished;
    }

    public Boolean getIsWaiting() {
        return isWaiting;
    }

    public void setIsWaiting(Boolean isWaiting) {
        this.isWaiting = isWaiting;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Convenience methods for Android
    public boolean isFinished() {
        return isGameFinished != null && isGameFinished;
    }

    public boolean isCanViewResult() {
        return "COMPLETED".equals(status) && isFinished();
    }

    public boolean isCurrentPlayerFinished() {
        return isFinished() || (isWaiting != null && isWaiting);
    }

    public boolean isOpponentFinished() {
        return opponent != null && opponent.getHasFinished() != null && opponent.getHasFinished();
    }

    @Override
    public String toString() {
        return "ChallengeGameResponse{" +
                "challengeId='" + challengeId + '\'' +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", isGameFinished=" + isGameFinished +
                ", isWaiting=" + isWaiting +
                ", totalQuestions=" + totalQuestions +
                ", currentQuestionIndex=" + currentQuestionIndex +
                '}';
    }

    public static class OpponentInfo {
        private String childId;
        private String name;
        private String avatar;
        private Integer level;
        private Integer score;
        private Integer correctAnswers;
        private Boolean isOnline;
        private Boolean hasFinished;

        public OpponentInfo() {}

        // Getters and Setters
        public String getChildId() {
            return childId;
        }

        public void setChildId(String childId) {
            this.childId = childId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }

        public Integer getCorrectAnswers() {
            return correctAnswers;
        }

        public void setCorrectAnswers(Integer correctAnswers) {
            this.correctAnswers = correctAnswers;
        }

        public Boolean getIsOnline() {
            return isOnline;
        }

        public void setIsOnline(Boolean isOnline) {
            this.isOnline = isOnline;
        }

        public Boolean getHasFinished() {
            return hasFinished;
        }

        public void setHasFinished(Boolean hasFinished) {
            this.hasFinished = hasFinished;
        }
    }
}