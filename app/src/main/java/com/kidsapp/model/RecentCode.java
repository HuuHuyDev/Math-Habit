package com.kidsapp.model;

public class RecentCode {
    private String code;
    private String fromUser;
    private String timeAgo;
    
    public RecentCode(String code, String fromUser, String timeAgo) {
        this.code = code;
        this.fromUser = fromUser;
        this.timeAgo = timeAgo;
    }
    
    // Getters and Setters
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getFromUser() {
        return fromUser;
    }
    
    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }
    
    public String getTimeAgo() {
        return timeAgo;
    }
    
    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }
    
    public String getDisplayInfo() {
        return "Từ " + fromUser + " • " + timeAgo;
    }
}