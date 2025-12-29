package com.kidsapp.data.response;

/**
 * Response khi tìm thấy đối thủ hoặc đang chờ
 */
public class MatchFoundResponse {
    private boolean matched;
    private String challengeId;
    private String opponentName;
    private String opponentId;
    private String categoryName;
    private Integer difficultyLevel;
    private Integer totalQuestions;
    private Integer timeLimitMinutes;
    private String message;

    public MatchFoundResponse() {}

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private MatchFoundResponse response = new MatchFoundResponse();

        public Builder matched(boolean matched) {
            response.matched = matched;
            return this;
        }

        public Builder challengeId(String challengeId) {
            response.challengeId = challengeId;
            return this;
        }

        public Builder opponentName(String opponentName) {
            response.opponentName = opponentName;
            return this;
        }

        public Builder opponentId(String opponentId) {
            response.opponentId = opponentId;
            return this;
        }

        public Builder categoryName(String categoryName) {
            response.categoryName = categoryName;
            return this;
        }

        public Builder difficultyLevel(Integer difficultyLevel) {
            response.difficultyLevel = difficultyLevel;
            return this;
        }

        public Builder totalQuestions(Integer totalQuestions) {
            response.totalQuestions = totalQuestions;
            return this;
        }

        public Builder timeLimitMinutes(Integer timeLimitMinutes) {
            response.timeLimitMinutes = timeLimitMinutes;
            return this;
        }

        public Builder message(String message) {
            response.message = message;
            return this;
        }

        public MatchFoundResponse build() {
            return response;
        }
    }

    // Getters and Setters
    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
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

    public String getOpponentId() {
        return opponentId;
    }

    public void setOpponentId(String opponentId) {
        this.opponentId = opponentId;
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

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(Integer timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MatchFoundResponse{" +
                "matched=" + matched +
                ", challengeId='" + challengeId + '\'' +
                ", opponentName='" + opponentName + '\'' +
                ", opponentId='" + opponentId + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", difficultyLevel=" + difficultyLevel +
                ", totalQuestions=" + totalQuestions +
                ", timeLimitMinutes=" + timeLimitMinutes +
                ", message='" + message + '\'' +
                '}';
    }
}
