package com.kidsapp.data.model;

public class LeaderboardItem {
    private String playerId;
    private String playerName;
    private int score;
    private int wins;
    private int losses;
    private int rank;

    public LeaderboardItem() {}

    public LeaderboardItem(String playerId, String playerName, int score, 
                          int wins, int losses, int rank) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.score = score;
        this.wins = wins;
        this.losses = losses;
        this.rank = rank;
    }

    // Getters
    public String getPlayerId() { return playerId; }
    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getRank() { return rank; }

    // Setters
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public void setScore(int score) { this.score = score; }
    public void setWins(int wins) { this.wins = wins; }
    public void setLosses(int losses) { this.losses = losses; }
    public void setRank(int rank) { this.rank = rank; }
    
    public double getWinRate() {
        int total = wins + losses;
        return total > 0 ? (wins * 100.0 / total) : 0;
    }
}
