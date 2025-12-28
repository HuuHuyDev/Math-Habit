package com.kidsapp.data.response;

import com.google.gson.annotations.SerializedName;

public class FriendResponse {
    @SerializedName("childId")
    private String childId;  // Backend trả về childId (UUID as String)
    
    @SerializedName("name")
    private String name;     // Backend trả về name
    
    @SerializedName("nickname")
    private String nickname;
    
    @SerializedName("avatarUrl")
    private String avatarUrl;
    
    @SerializedName("level")
    private Integer level;   // Backend trả về level (không phải currentLevel)
    
    @SerializedName("totalXp")
    private Integer totalXp;
    
    @SerializedName("currentStreak")
    private Integer currentStreak;
    
    @SerializedName("isOnline")
    private Boolean isOnline;

    // Getter/Setter cho childId
    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    // Getter/Setter cho name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter/Setter cho nickname
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    // Getter/Setter cho avatarUrl
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    // Getter/Setter cho level
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    // Getter/Setter cho totalXp
    public Integer getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(Integer totalXp) {
        this.totalXp = totalXp;
    }

    // Getter/Setter cho currentStreak
    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }

    // Getter/Setter cho isOnline
    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    // Compatibility methods for existing code
    public String getId() {
        return childId;  // Map childId to id
    }

    public void setId(String id) {
        this.childId = id;
    }

    public Integer getCurrentLevel() {
        return level != null ? level : 1;  // Default to level 1 if null
    }

    public void setCurrentLevel(Integer currentLevel) {
        this.level = currentLevel;
    }
}
