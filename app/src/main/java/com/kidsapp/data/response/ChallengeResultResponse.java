package com.kidsapp.data.response;

import java.util.List;

/**
 * Response cho kết quả thách đấu
 */
public class ChallengeResultResponse {
    private String challengeId;
    private String title;
    private String categoryName;
    private Integer difficultyLevel;
    private String startTime;
    private String endTime;
    private Long durationSeconds;
    private PlayerResult playerResult;
    private PlayerResult opponentResult;
    private String winner;
    private String winnerName;
    private String resultMessage;
    private Boolean isPlayerWinner;
    private RewardInfo rewards;
    private List<QuestionResult> questionResults;

    public ChallengeResultResponse() {}

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

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public PlayerResult getPlayerResult() {
        return playerResult;
    }

    public void setPlayerResult(PlayerResult playerResult) {
        this.playerResult = playerResult;
    }

    public PlayerResult getOpponentResult() {
        return opponentResult;
    }

    public void setOpponentResult(PlayerResult opponentResult) {
        this.opponentResult = opponentResult;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public Boolean getIsPlayerWinner() {
        return isPlayerWinner;
    }

    public void setIsPlayerWinner(Boolean isPlayerWinner) {
        this.isPlayerWinner = isPlayerWinner;
    }

    public RewardInfo getRewards() {
        return rewards;
    }

    public void setRewards(RewardInfo rewards) {
        this.rewards = rewards;
    }

    public List<QuestionResult> getQuestionResults() {
        return questionResults;
    }

    public void setQuestionResults(List<QuestionResult> questionResults) {
        this.questionResults = questionResults;
    }

    public static class PlayerResult {
        private String childId;
        private String name;
        private String avatar;
        private Integer level;
        private Integer totalScore;
        private Integer correctAnswers;
        private Integer wrongAnswers;
        private Integer totalQuestions;
        private Double accuracy;
        private Long timeSpent;
        private Integer rank;

        public PlayerResult() {}

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

        public Integer getTotalScore() {
            return totalScore;
        }

        public void setTotalScore(Integer totalScore) {
            this.totalScore = totalScore;
        }

        public Integer getCorrectAnswers() {
            return correctAnswers;
        }

        public void setCorrectAnswers(Integer correctAnswers) {
            this.correctAnswers = correctAnswers;
        }

        public Integer getWrongAnswers() {
            return wrongAnswers;
        }

        public void setWrongAnswers(Integer wrongAnswers) {
            this.wrongAnswers = wrongAnswers;
        }

        public Integer getTotalQuestions() {
            return totalQuestions;
        }

        public void setTotalQuestions(Integer totalQuestions) {
            this.totalQuestions = totalQuestions;
        }

        public Double getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(Double accuracy) {
            this.accuracy = accuracy;
        }

        public Long getTimeSpent() {
            return timeSpent;
        }

        public void setTimeSpent(Long timeSpent) {
            this.timeSpent = timeSpent;
        }

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }
    }

    public static class RewardInfo {
        private Integer xpEarned;
        private Integer coinsEarned;
        private String rewardType;
        private List<String> badges;

        public RewardInfo() {}

        // Getters and Setters
        public Integer getXpEarned() {
            return xpEarned;
        }

        public void setXpEarned(Integer xpEarned) {
            this.xpEarned = xpEarned;
        }

        public Integer getCoinsEarned() {
            return coinsEarned;
        }

        public void setCoinsEarned(Integer coinsEarned) {
            this.coinsEarned = coinsEarned;
        }

        public String getRewardType() {
            return rewardType;
        }

        public void setRewardType(String rewardType) {
            this.rewardType = rewardType;
        }

        public List<String> getBadges() {
            return badges;
        }

        public void setBadges(List<String> badges) {
            this.badges = badges;
        }
    }

    public static class QuestionResult {
        private Integer questionIndex;
        private String questionText;
        private String playerAnswer;
        private String opponentAnswer;
        private String correctAnswer;
        private Boolean playerCorrect;
        private Boolean opponentCorrect;
        private Integer pointsEarned;

        public QuestionResult() {}

        // Getters and Setters
        public Integer getQuestionIndex() {
            return questionIndex;
        }

        public void setQuestionIndex(Integer questionIndex) {
            this.questionIndex = questionIndex;
        }

        public String getQuestionText() {
            return questionText;
        }

        public void setQuestionText(String questionText) {
            this.questionText = questionText;
        }

        public String getPlayerAnswer() {
            return playerAnswer;
        }

        public void setPlayerAnswer(String playerAnswer) {
            this.playerAnswer = playerAnswer;
        }

        public String getOpponentAnswer() {
            return opponentAnswer;
        }

        public void setOpponentAnswer(String opponentAnswer) {
            this.opponentAnswer = opponentAnswer;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer;
        }

        public Boolean getPlayerCorrect() {
            return playerCorrect;
        }

        public void setPlayerCorrect(Boolean playerCorrect) {
            this.playerCorrect = playerCorrect;
        }

        public Boolean getOpponentCorrect() {
            return opponentCorrect;
        }

        public void setOpponentCorrect(Boolean opponentCorrect) {
            this.opponentCorrect = opponentCorrect;
        }

        public Integer getPointsEarned() {
            return pointsEarned;
        }

        public void setPointsEarned(Integer pointsEarned) {
            this.pointsEarned = pointsEarned;
        }
    }
}