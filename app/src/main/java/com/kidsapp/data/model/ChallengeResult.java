package com.kidsapp.data.model;

public class ChallengeResult {
    private String challengeId;
    private String player1Name;
    private String player2Name;
    private int player1Score;
    private int player2Score;
    private int correctAnswers;
    private int wrongAnswers;
    private long totalTime;
    private boolean isWin;
    private String date;

    public ChallengeResult() {}

    public ChallengeResult(String challengeId, String player1Name, String player2Name,
                          int player1Score, int player2Score, int correctAnswers, 
                          int wrongAnswers, long totalTime, boolean isWin, String date) {
        this.challengeId = challengeId;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.player1Score = player1Score;
        this.player2Score = player2Score;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.totalTime = totalTime;
        this.isWin = isWin;
        this.date = date;
    }

    // Getters
    public String getChallengeId() { return challengeId; }
    public String getPlayer1Name() { return player1Name; }
    public String getPlayer2Name() { return player2Name; }
    public int getPlayer1Score() { return player1Score; }
    public int getPlayer2Score() { return player2Score; }
    public int getCorrectAnswers() { return correctAnswers; }
    public int getWrongAnswers() { return wrongAnswers; }
    public long getTotalTime() { return totalTime; }
    public boolean isWin() { return isWin; }
    public String getDate() { return date; }

    // Setters
    public void setChallengeId(String challengeId) { this.challengeId = challengeId; }
    public void setPlayer1Name(String player1Name) { this.player1Name = player1Name; }
    public void setPlayer2Name(String player2Name) { this.player2Name = player2Name; }
    public void setPlayer1Score(int player1Score) { this.player1Score = player1Score; }
    public void setPlayer2Score(int player2Score) { this.player2Score = player2Score; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }
    public void setWrongAnswers(int wrongAnswers) { this.wrongAnswers = wrongAnswers; }
    public void setTotalTime(long totalTime) { this.totalTime = totalTime; }
    public void setWin(boolean win) { isWin = win; }
    public void setDate(String date) { this.date = date; }
}
