package com.kidsapp.data.response;

/**
 * Response cho danh sách thói quen có sẵn từ API
 */
public class HabitTemplateResponse {
    private String id;
    private String name;
    private String description;
    private String category;        // health, study, housework, sport, creativity
    private String categoryName;    // Sức khỏe, Học tập, Việc nhà, Thể thao, Sáng tạo
    private String iconName;
    private String colorHex;
    private String suggestedTime;
    private Integer pointsReward;
    private Integer minGrade;
    private Integer maxGrade;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }

    public String getSuggestedTime() { return suggestedTime; }
    public void setSuggestedTime(String suggestedTime) { this.suggestedTime = suggestedTime; }

    public Integer getPointsReward() { return pointsReward; }
    public void setPointsReward(Integer pointsReward) { this.pointsReward = pointsReward; }

    public Integer getMinGrade() { return minGrade; }
    public void setMinGrade(Integer minGrade) { this.minGrade = minGrade; }

    public Integer getMaxGrade() { return maxGrade; }
    public void setMaxGrade(Integer maxGrade) { this.maxGrade = maxGrade; }
}
