package com.kidsapp.data.model;

/**
 * Model cho Category (chủ đề thách đấu)
 */
public class Category {
    private String id;
    private String code;
    private String name;
    private String nameEn;
    private String description;
    private String iconName;
    private String colorHex;
    private Integer minGrade;
    private Integer maxGrade;
    private Boolean isActive;
    private Boolean isFeatured;
    private Integer orderIndex;

    // Constructors
    public Category() {}

    public Category(String id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public Integer getMinGrade() {
        return minGrade;
    }

    public void setMinGrade(Integer minGrade) {
        this.minGrade = minGrade;
    }

    public Integer getMaxGrade() {
        return maxGrade;
    }

    public void setMaxGrade(Integer maxGrade) {
        this.maxGrade = maxGrade;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getFeatured() {
        return isFeatured;
    }

    public void setFeatured(Boolean featured) {
        isFeatured = featured;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
