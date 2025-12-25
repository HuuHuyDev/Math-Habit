package com.kidsapp.data.response;

import com.google.gson.annotations.SerializedName;

/**
 * Response model cho API tìm kiếm bạn bè (children)
 */
public class ChildSearchResponse {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("nickname")
    private String nickname;
    
    @SerializedName("avatarUrl")
    private String avatarUrl;
    
    @SerializedName("level")
    private int level;
    
    @SerializedName("grade")
    private Integer grade;
    
    @SerializedName("isOnline")
    private boolean isOnline;

    public ChildSearchResponse() {}

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    /**
     * Lấy tên hiển thị (ưu tiên nickname)
     */
    public String getDisplayName() {
        if (nickname != null && !nickname.isEmpty()) {
            return nickname;
        }
        return name;
    }
}
