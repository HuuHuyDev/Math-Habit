package com.kidsapp.ui.parent.child_manage.model;

/**
 * Model cho thông tin Bé trong quản lý
 */
public class ChildModel {
    private String id;
    private String name;
    private String className;
    private int level;
    private int currentXP;
    private int maxXP;
    private int coins;
    private String avatar;
    private Boolean gender; // true = Nam, false = Nữ
    private String username;
    private String password;
    private String nickname;
    private String school;
    private String birthDate;
    private int totalPoints;

    public ChildModel(String id, String name, String className, int level,
                     int currentXP, int maxXP, int coins, String avatar) {
        this.id = id;
        this.name = name;
        this.className = className;
        this.level = level;
        this.currentXP = currentXP;
        this.maxXP = maxXP;
        this.coins = coins;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCurrentXP() {
        return currentXP;
    }

    public void setCurrentXP(int currentXP) {
        this.currentXP = currentXP;
    }

    public int getMaxXP() {
        return maxXP;
    }

    public void setMaxXP(int maxXP) {
        this.maxXP = maxXP;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    /**
     * Lấy số lớp từ className (vd: "Lớp 3" -> 3)
     */
    public Integer getGradeNumber() {
        if (className == null || className.isEmpty()) return null;
        try {
            // Lấy số từ className
            String numStr = className.replaceAll("[^0-9]", "");
            return numStr.isEmpty() ? null : Integer.parseInt(numStr);
        } catch (Exception e) {
            return null;
        }
    }

    public String getClassAndLevel() {
        return className + " • Cấp " + level;
    }

    public String getXPText() {
        return currentXP + " XP";
    }

    public String getProgressText() {
        return currentXP + "/" + maxXP + " XP";
    }

    public int getProgressPercentage() {
        if (maxXP == 0) return 0;
        return (int) ((currentXP * 100.0f) / maxXP);
    }
}
