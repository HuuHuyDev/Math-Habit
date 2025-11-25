package com.kidsapp.ui.parent.report.model;

/**
 * Model class cho thông tin bé
 */
public class Child {
    private String id;
    private String name;
    private int level;
    private int totalXP;
    private String avatar; // emoji

    public Child(String id, String name, int level, int totalXP) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.totalXP = totalXP;
        this.avatar = "👦"; // default avatar
    }

    public Child(String id, String name, int level, int totalXP, String avatar) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.totalXP = totalXP;
        this.avatar = avatar;
    }

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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTotalXP() {
        return totalXP;
    }

    public void setTotalXP(int totalXP) {
        this.totalXP = totalXP;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLevelText() {
        return "Lớp " + level;
    }
}
