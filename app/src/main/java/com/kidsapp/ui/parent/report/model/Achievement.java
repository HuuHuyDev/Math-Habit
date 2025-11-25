package com.kidsapp.ui.parent.report.model;

/**
 * Model class cho thành tích (achievement badge)
 */
public class Achievement {
    private String id;
    private String name;
    private String icon; // emoji
    private int count; // Số lần đạt được

    public Achievement(String id, String name, String icon, int count) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.count = count;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCountText() {
        return "x " + count;
    }
}
