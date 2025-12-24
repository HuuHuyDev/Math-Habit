package com.kidsapp.model;

public class Friend {
    private String id;
    private String name;
    private String level;
    private String status;
    private boolean isOnline;
    private String avatarUrl;
    
    public Friend(String id, String name, String level, String status, boolean isOnline) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.status = status;
        this.isOnline = isOnline;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public boolean isOnline() {
        return isOnline;
    }
    
    public void setOnline(boolean online) {
        isOnline = online;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}